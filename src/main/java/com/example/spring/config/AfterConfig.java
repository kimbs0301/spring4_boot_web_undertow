package com.example.spring.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * @author gimbyeongsu
 * 
 */
@Configuration
@DependsOn(value = { "rootConfig" })
public class AfterConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(AfterConfig.class);

	public AfterConfig() {
		LOGGER.debug("생성자 AfterConfig()");
	}

	@Bean
	public Map<String, Object> configurationBeanInfo(ApplicationContext applicationContext) throws Exception {
		Map<String, Object> map = applicationContext.getBeansWithAnnotation(Configuration.class);
		for (String key : map.keySet()) {
			LOGGER.debug("{} {}", key, map.get(key));
		}
		return map;
	}
}