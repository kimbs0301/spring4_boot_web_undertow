package com.example.spring.logic.shutdown.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.spring.logic.shutdown.service.ShutdownService;

/**
 * @author gimbyeongsu
 * 
 */
@Service
public class ShutdownServiceImpl implements ShutdownService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownServiceImpl.class);

	public ShutdownServiceImpl() {
		LOGGER.debug("생성자 ShutdownServiceImpl()");
	}

	@Override
	public void shutdown() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				LOGGER.info("");
				LOGGER.info("");
				LOGGER.info("shutdown start");
				System.exit(0);
			}
		}).start();
	}
}
