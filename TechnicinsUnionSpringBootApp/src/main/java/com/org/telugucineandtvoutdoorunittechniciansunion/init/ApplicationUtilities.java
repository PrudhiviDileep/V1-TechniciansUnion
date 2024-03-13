
package com.org.telugucineandtvoutdoorunittechniciansunion.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationUtilities {

	public static void error(Class<?> className, Exception e, String methodName) {
		Logger logger = LoggerFactory.getLogger(className.getClass());
		logger.error("ERROR::{}>>{}>>{}", String.valueOf(String.valueOf(className.getName())), methodName,
				e.getMessage());
		;

	}

	public static void error(Class<?> className, String methodName, Exception e) {
		Logger logger = LoggerFactory.getLogger(className.getClass());
		logger.error("ERROR::{}>>{}>>{}", String.valueOf(String.valueOf(className.getName())), methodName,
				e.getMessage());

	}

	org.slf4j.Logger logger = LoggerFactory.getLogger(ApplicationUtilities.class);

	public static void debug(Class<?> className, String debugMessabe) {
		Logger logger = LoggerFactory.getLogger(className.getClass());
		logger.debug("DEBUG::{}", debugMessabe);
		logger.info("DEBUG : {}", debugMessabe);
	}
}
