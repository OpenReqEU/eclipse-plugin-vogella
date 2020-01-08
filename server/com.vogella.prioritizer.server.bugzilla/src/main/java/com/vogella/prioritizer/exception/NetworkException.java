package com.vogella.prioritizer.exception;

/**
 * Used to report IOExceptions during access of the services
 */
public class NetworkException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NetworkException(Throwable cause) {
		super(cause);
	}

}
