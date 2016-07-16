package com.example.embedded;

import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.UndertowOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration.EmbeddedServletContainerCustomizerBeanPostProcessorRegistrar;
import org.springframework.boot.context.embedded.Compression;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.xnio.Options;

/**
 * @author gimbyeongsu
 * 
 */
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@Import(EmbeddedServletContainerCustomizerBeanPostProcessorRegistrar.class)
public class EmbeddedUndertowConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedUndertowConfig.class);

	@Autowired
	private Environment environment;

	public EmbeddedUndertowConfig() {
		LOGGER.debug("생성자 UndertowEmbeddedConfig()");
	}

	@Bean
	public UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory() {
		String ip = environment.getRequiredProperty("server.undertow.ip");
		int port = environment.getRequiredProperty("server.undertow.port", Integer.class);
		String contextPath = environment.getRequiredProperty("context.path");

		UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory(contextPath, port);
		factory.setListenAddress(ip);

		Builder builder = Undertow.builder();
		builder.setServerOption(UndertowOptions.NO_REQUEST_TIMEOUT, 6000);
		builder.setServerOption(UndertowOptions.MAX_HEADER_SIZE, 512);
		builder.setServerOption(UndertowOptions.MAX_ENTITY_SIZE, 1024 * 8L);
		builder.setServerOption(UndertowOptions.MAX_PARAMETERS, 30);
		builder.setServerOption(UndertowOptions.MAX_HEADERS, 30);
		builder.setServerOption(UndertowOptions.MAX_COOKIES, 30);

		builder.setSocketOption(Options.WORKER_IO_THREADS, 1);
		builder.setSocketOption(Options.TCP_NODELAY, true);
		builder.setSocketOption(Options.REUSE_ADDRESSES, true);

		builder.setWorkerOption(Options.WORKER_IO_THREADS, 1);
		builder.setWorkerOption(Options.WORKER_TASK_CORE_THREADS, 1);
		builder.setWorkerOption(Options.WORKER_TASK_LIMIT, 1);
		builder.setWorkerOption(Options.WORKER_TASK_MAX_THREADS, 1);
		builder.setWorkerOption(Options.TCP_NODELAY, true);

		factory.setBuilder(builder);

		factory.setAccessLogEnabled(environment.getRequiredProperty("server.undertow.accesslog.enabled", Boolean.class));
		factory.setAccessLogDirectory(new File(environment.getRequiredProperty("server.undertow.accesslog.dir")));
		factory.setAccessLogPattern(environment.getRequiredProperty("server.undertow.accesslog.pattern"));

		Compression compression = new Compression();
		compression.setEnabled(environment.getRequiredProperty("server.compression.enabled", Boolean.class));
		compression.setMinResponseSize(environment.getRequiredProperty("server.compression.min-response-size",
				Integer.class));
		factory.setCompression(compression);

		List<ServletContextInitializer> servletContextInitializers = new ArrayList<>();
		servletContextInitializers.add(new EmbeddedWebInitalizer());
		factory.setInitializers(servletContextInitializers);
		return factory;
	}
}