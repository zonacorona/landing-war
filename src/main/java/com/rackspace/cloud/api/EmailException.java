package com.rackspace.cloud.api;

public class EmailException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6301702907074452203L;

	public EmailException(){
		super();
	}
	
	public EmailException(String message){
		super(message);
	}

}
