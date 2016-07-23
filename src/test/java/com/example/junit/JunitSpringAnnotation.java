package com.example.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;

import com.example.spring.config.RootConfig;

/**
 * @author gimbyeongsu
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringApplicationConfiguration(classes = { JunitConfig.class, RootConfig.class })
@WebAppConfiguration
@ActiveProfiles(profiles = { "junit" })
@TestPropertySource(locations = "classpath:application-junit.properties")
public @interface JunitSpringAnnotation {

}
