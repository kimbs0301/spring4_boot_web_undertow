package com.example.embedded;

import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.UndertowMessages;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.server.handlers.accesslog.AccessLogReceiver;
import io.undertow.server.handlers.accesslog.DefaultAccessLogReceiver;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.handlers.resource.URLResource;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.MimeMapping;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.api.ServletStackTraces;
import io.undertow.servlet.handlers.DefaultServlet;
import io.undertow.servlet.util.ImmediateInstanceFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.context.embedded.AbstractEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.embedded.MimeMappings.Mapping;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.undertow.FileSessionPersistence;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainer;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 * org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory
 */
public class UndertowEmbeddedServletContainerFactory extends AbstractEmbeddedServletContainerFactory implements
		ResourceLoaderAware {
	private static final Set<Class<?>> NO_CLASSES = Collections.emptySet();

	private Builder builder;

	private ResourceLoader resourceLoader;
	private File accessLogDirectory;
	private String accessLogPattern;
	private boolean accessLogEnabled = false;
	private boolean useForwardHeaders;
	private String listenAddress = "0.0.0.0";

	public UndertowEmbeddedServletContainerFactory() {
		super();
		getJspServlet().setRegistered(false);
	}

	public UndertowEmbeddedServletContainerFactory(int port) {
		super(port);
		getJspServlet().setRegistered(false);
	}

	public UndertowEmbeddedServletContainerFactory(String contextPath, int port) {
		super(contextPath, port);
		getJspServlet().setRegistered(false);
	}

	@Override
	public EmbeddedServletContainer getEmbeddedServletContainer(ServletContextInitializer... initializers) {
		DeploymentManager manager = createDeploymentManager(initializers);
		int port = getPort();
		builder.addHttpListener(port, listenAddress);
		return getUndertowEmbeddedServletContainer(builder, manager, port);
	}

	private DeploymentManager createDeploymentManager(ServletContextInitializer... initializers) {
		DeploymentInfo deployment = Servlets.deployment();
		registerServletContainerInitializerToDriveServletContextInitializers(deployment, initializers);
		deployment.setClassLoader(getServletClassLoader());
		deployment.setContextPath(getContextPath());
		deployment.setDisplayName(getDisplayName());
		deployment.setDeploymentName("spring-boot");
		if (isRegisterDefaultServlet()) {
			deployment.addServlet(Servlets.servlet("default", DefaultServlet.class));
		}
		configureErrorPages(deployment);
		deployment.setServletStackTraces(ServletStackTraces.NONE);
		deployment.setResourceManager(getDocumentRootResourceManager());
		configureMimeMappings(deployment);

		if (isAccessLogEnabled()) {
			configureAccessLog(deployment);
		}
		if (isPersistSession()) {
			File dir = getValidSessionStoreDir();
			deployment.setSessionPersistenceManager(new FileSessionPersistence(dir));
		}
		DeploymentManager manager = Servlets.newContainer().addDeployment(deployment);
		manager.deploy();
		SessionManager sessionManager = manager.getDeployment().getSessionManager();
		int sessionTimeout = (getSessionTimeout() > 0 ? getSessionTimeout() : -1);
		sessionManager.setDefaultSessionTimeout(sessionTimeout);
		return manager;
	}

	private void configureAccessLog(DeploymentInfo deploymentInfo) {
		deploymentInfo.addInitialHandlerChainWrapper(new HandlerWrapper() {

			@Override
			public HttpHandler wrap(HttpHandler handler) {
				return createAccessLogHandler(handler);
			}
		});
	}

	private AccessLogHandler createAccessLogHandler(HttpHandler handler) {
		createAccessLogDirectoryIfNecessary();
		Executor worker = Executors.newFixedThreadPool(1, new ThreadFactoryImpl("XNIO_ACCESS_LOG", true,
				Thread.NORM_PRIORITY));
		AccessLogReceiver accessLogReceiver = new DefaultAccessLogReceiver(worker, this.accessLogDirectory,
				"access_log.");
		String formatString = (this.accessLogPattern != null) ? this.accessLogPattern : "common";
		return new AccessLogHandler(handler, accessLogReceiver, formatString, Undertow.class.getClassLoader());
	}

	private void createAccessLogDirectoryIfNecessary() {
		Assert.state(this.accessLogDirectory != null, "Access log directory is not set");
		if (!this.accessLogDirectory.isDirectory() && !this.accessLogDirectory.mkdirs()) {
			throw new IllegalStateException("Failed to create access log directory '" + this.accessLogDirectory + "'");
		}
	}

	private void registerServletContainerInitializerToDriveServletContextInitializers(DeploymentInfo deployment,
			ServletContextInitializer... initializers) {
		ServletContextInitializer[] mergedInitializers = mergeInitializers(initializers);
		Initializer initializer = new Initializer(mergedInitializers);
		deployment.addServletContainerInitalizer(new ServletContainerInitializerInfo(Initializer.class,
				new ImmediateInstanceFactory<ServletContainerInitializer>(initializer), NO_CLASSES));
	}

	private ClassLoader getServletClassLoader() {
		if (this.resourceLoader != null) {
			return this.resourceLoader.getClassLoader();
		}
		return getClass().getClassLoader();
	}

	private ResourceManager getDocumentRootResourceManager() {
		File root = getCanonicalDocumentRoot();
		if (root.isDirectory()) {
			return new FileResourceManager(root, 0);
		}
		if (root.isFile()) {
			return new JarResourceManager(root);
		}
		return ResourceManager.EMPTY_RESOURCE_MANAGER;
	}

	private File getCanonicalDocumentRoot() {
		try {
			File root = getValidDocumentRoot();
			root = (root != null ? root : createTempDir("undertow-docbase"));
			return root.getCanonicalFile();
		} catch (IOException e) {
			throw new IllegalStateException("Cannot get canonical document root", e);
		}
	}

	private void configureErrorPages(DeploymentInfo servletBuilder) {
		for (ErrorPage errorPage : getErrorPages()) {
			servletBuilder.addErrorPage(getUndertowErrorPage(errorPage));
		}
	}

	private io.undertow.servlet.api.ErrorPage getUndertowErrorPage(ErrorPage errorPage) {
		if (errorPage.getStatus() != null) {
			return new io.undertow.servlet.api.ErrorPage(errorPage.getPath(), errorPage.getStatusCode());
		}
		if (errorPage.getException() != null) {
			return new io.undertow.servlet.api.ErrorPage(errorPage.getPath(), errorPage.getException());
		}
		return new io.undertow.servlet.api.ErrorPage(errorPage.getPath());
	}

	private void configureMimeMappings(DeploymentInfo servletBuilder) {
		for (Mapping mimeMapping : getMimeMappings()) {
			servletBuilder.addMimeMapping(new MimeMapping(mimeMapping.getExtension(), mimeMapping.getMimeType()));
		}
	}

	protected UndertowEmbeddedServletContainer getUndertowEmbeddedServletContainer(Builder builder,
			DeploymentManager manager, int port) {
		return new UndertowEmbeddedServletContainer(builder, manager, getContextPath(), port, isUseForwardHeaders(),
				port >= 0, getCompression(), getServerHeader());
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public void setBuilder(Builder builder) {
		this.builder = builder;
	}

	public void setAccessLogDirectory(File accessLogDirectory) {
		this.accessLogDirectory = accessLogDirectory;
	}

	public void setAccessLogPattern(String accessLogPattern) {
		this.accessLogPattern = accessLogPattern;
	}

	public void setAccessLogEnabled(boolean accessLogEnabled) {
		this.accessLogEnabled = accessLogEnabled;
	}

	public boolean isAccessLogEnabled() {
		return this.accessLogEnabled;
	}

	protected final boolean isUseForwardHeaders() {
		return this.useForwardHeaders;
	}

	public void setUseForwardHeaders(boolean useForwardHeaders) {
		this.useForwardHeaders = useForwardHeaders;
	}

	public void setListenAddress(String listenAddress) {
		this.listenAddress = listenAddress;
	}

	private static class JarResourceManager implements ResourceManager {
		private final String jarPath;

		JarResourceManager(File jarFile) {
			this(jarFile.getAbsolutePath());
		}

		JarResourceManager(String jarPath) {
			this.jarPath = jarPath;
		}

		@Override
		public Resource getResource(String path) throws IOException {
			URL url = new URL("jar:file:" + this.jarPath + "!" + path);
			URLResource resource = new URLResource(url, url.openConnection(), path);
			if (resource.getContentLength() < 0) {
				return null;
			}
			return resource;
		}

		@Override
		public boolean isResourceChangeListenerSupported() {
			return false;
		}

		@Override
		public void registerResourceChangeListener(ResourceChangeListener listener) {
			throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();

		}

		@Override
		public void removeResourceChangeListener(ResourceChangeListener listener) {
			throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
		}

		@Override
		public void close() throws IOException {
		}
	}

	private static class Initializer implements ServletContainerInitializer {
		private final ServletContextInitializer[] initializers;

		Initializer(ServletContextInitializer[] initializers) {
			this.initializers = initializers;
		}

		@Override
		public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
			for (ServletContextInitializer initializer : this.initializers) {
				initializer.onStartup(servletContext);
			}
		}
	}
}
