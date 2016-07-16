package com.example.spring.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.spring.logic.shutdown.service.ShutdownService;

/**
 * @author gimbyeongsu
 * 
 */
@Controller
public class CommonController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonController.class);

	@Autowired
	private ShutdownService shutdownService;

	public CommonController() {
		LOGGER.debug("생성자 CommonController()");
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public ResponseEntity<Void> test() {
		LOGGER.debug("");
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<Void>(headers, HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/shutdown", method = RequestMethod.GET)
	public ResponseEntity<Void> shutdown() {
		LOGGER.debug("");
		shutdownService.shutdown();
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<Void>(headers, HttpStatus.NO_CONTENT);
	}
}
