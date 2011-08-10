package com.openwide.sca.intents.utils;

public class RequestElement {

	private long requestTime;
	private String request; 
	
	public RequestElement(String request, long requestTime) throws IllegalArgumentException {
		if(request != null){
			this.request = request;
		} else {
			throw new IllegalArgumentException("request parameter must not be null");
		}
		if(requestTime > 0){
			this.requestTime = requestTime;
		} else {
			throw new IllegalArgumentException("requestTime parameter must be set with a value > 0");
		}
	}
	
	public String getRequest(){
		return this.request;
	}
	
	public long getRequestTime(){
		return this.requestTime;
	}
	
}
