package com.example.junit;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring.config.WebAppContextConfig;

/**
 * @author gimbyeongsu
 * 
 */
@Configuration
@EnableAutoConfiguration(exclude = { EmbeddedServletContainerAutoConfiguration.class, //
		JmxAutoConfiguration.class, //
		SpringApplicationAdminJmxAutoConfiguration.class, //
		WebSocketAutoConfiguration.class, //
		DataSourceAutoConfiguration.class, //
		SpringDataWebAutoConfiguration.class, //
		SecurityAutoConfiguration.class //
})
@ComponentScan(basePackages = { "com.example.spring" }, excludeFilters = {
		@Filter(value = { WebAppContextConfig.class }, type = FilterType.ASSIGNABLE_TYPE),
		@Filter(value = { RestController.class, Controller.class }, type = FilterType.ANNOTATION),
		@Filter(pattern = { "com.example.spring.*.model.*" }, type = FilterType.REGEX) })
@DependsOn(value = { "rootConfig" })
public class JunitConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(JunitConfig.class);

	public JunitConfig() {
		LOGGER.debug("생성자 JunitConfig()");
	}

	@Bean
	public Map<String, Object> configurationBeanInfo(ApplicationContext applicationContext) throws Exception {
		Map<String, Object> map = applicationContext.getBeansWithAnnotation(Configuration.class);
		for (String key : map.keySet()) {
			LOGGER.debug("{} {}", key, map.get(key));
		}

		LOGGER.debug("");
		String[] beanNames = applicationContext.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		for (String beanName : beanNames) {
			LOGGER.debug("{}", beanName);
		}
		return map;
	}
}