package com.example.junit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author gimbyeongsu
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@JunitSpringAnnotation
public class JunitConfigTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(JunitConfigTest.class);

	@Test
	public void test() throws Exception {
		LOGGER.debug("");
	}
}