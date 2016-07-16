package com.example.embedded;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.example.spring.config.WebAppContextConfig;

/**
 * @author gimbyeongsu
 * 
 */
public class EmbeddedWebInitalizer implements ServletContextInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedWebInitalizer.class);

	public EmbeddedWebInitalizer() {
		LOGGER.debug("생성자 EmbeddedWebInitalizer()");
	}

	@Override
	public void onStartup(ServletContext container) throws ServletException {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

		context.register(WebAppContextConfig.class);
		context.setServletContext(container);

		DispatcherServlet dispatcher = new DispatcherServlet(context);
		ServletRegistration.Dynamic servlet = container.addServlet("dispatcher", dispatcher);

		servlet.setLoadOnStartup(1);
		servlet.addMapping("/");
	}
}