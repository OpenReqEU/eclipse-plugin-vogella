package com.vogella.spring.datacrawler;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Simple class to store key value pairs.
 * 
 * TODO persist, better solution
 * 
 * @author david
 *
 */
@Component
public class KeyValueStore {
	private static final Logger log = Logger.getLogger(KeyValueStore.class.getName());

	public static final String LAST_SYNC_BUGS_KEY = "last_sync_bugs";

	private Properties properties;

	public KeyValueStore() {
		properties = new Properties();
	}

	public String getValue(String key) {
		log.debug("Get value for key: " + key);
		return properties.getProperty(key);
	}

	public String setValue(String key, String value) {
		log.debug("Set value for key: " + key);
		return (String) properties.setProperty(key, value);
	}
}
