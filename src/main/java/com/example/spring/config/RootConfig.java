package com.example.spring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author gimbyeongsu
 * 
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class RootConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(RootConfig.class);

	public RootConfig() {
		LOGGER.debug("생성자 RootConfig()");
	}
}