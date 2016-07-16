package com.example.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import com.example.embedded.EmbeddedUndertowConfig;
import com.example.spring.config.WebAppContextConfig;

/**
 * @author gimbyeongsu
 * 
 */
@Configurable
@EnableAutoConfiguration(exclude = { EmbeddedServletContainerAutoConfiguration.class, //
		JmxAutoConfiguration.class, //
		SpringApplicationAdminJmxAutoConfiguration.class, //
		WebSocketAutoConfiguration.class, //
		DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = { "com.example.spring" }, basePackageClasses = { EmbeddedUndertowConfig.class }, excludeFilters = {
		@Filter(value = { WebAppContextConfig.class }, type = FilterType.ASSIGNABLE_TYPE),
		@Filter(pattern = { "com.example.spring.*.model.*" }, type = FilterType.REGEX) })
public class Application {
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	public Application() {
		LOGGER.debug("생성자 Application()");
	}

	public static void main(String[] args) throws Exception {
		LOGGER.debug("start");
		SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(Application.class);
		SpringApplication springApplication = springApplicationBuilder.build();
		springApplication.run();
	}
}