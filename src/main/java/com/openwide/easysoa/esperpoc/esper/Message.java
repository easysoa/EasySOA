package com.openwide.easysoa.esperpoc.esper;

public class Message {

	private String protocol;
	private String host;
	private int port;
	private String pathName;
	private String parameters;
	private String content;
	private String method;
	private String type;

	/**
	 * Constructor
	 * @param host
	 * @param port
	 * @param pathName
	 * @param parameters
	 * @param content
	 */
	public Message(String protocol, String host, int port, String pathName, String parameters, String content, String method, String type){
		if(protocol.toLowerCase().contains("http")){
			this.protocol = "http";
		} else {
			this.protocol = protocol;
		}
		this.host = host;
		this.port = port;
		this.pathName = pathName;
		this.parameters = parameters;
		this.content = content;
		this.method = method;
		this.type = type;
	}

	/**
	 * Constructor
	 * @param host
	 * @param port
	 * @param pathName
	 * @param parameters
	 */
	public Message(String protocol, String host, int port, String pathName, String parameters, String type){
		if(protocol.toLowerCase().contains("http")){
			this.protocol = "http";
		} else {
			this.protocol = protocol;
		}
		this.host = host;
		this.port = port;
		this.pathName = pathName;
		this.parameters = parameters;
		this.type = type;
		this.content = "";
		this.method = "";
	}
	
	/**
	 * 
	 * @return
	 */
	public String getParameters(){
		return parameters;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getHost() {
		return host;
	}

	/**
	 * 
	 * @return
	 */
	public String getPathName() {
		return pathName;
	}

	/**
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getProtocol(){
		return protocol;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getContent(){
		return content;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMethod(){
		return method;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCompleteMessage(){
		StringBuffer sb = new StringBuffer();
		sb.append(protocol);
		sb.append("://");
		sb.append(host);
		sb.append(":");
		sb.append(port);
		sb.append(pathName);
		sb.append("?");
		sb.append(parameters);
		return sb.toString();
	}

	/**
	 * 
	 * @return
	 */
	public String getCompletePath(){
		StringBuffer sb = new StringBuffer();
		sb.append(protocol);
		sb.append("://");
		sb.append(host);
		sb.append(":");
		sb.append(port);
		sb.append(pathName);
		return sb.toString();
	}	
	
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("Message => [");
		sb.append("protocol: ");
		sb.append(protocol);		
		sb.append(", port: ");
		sb.append(port);
		sb.append(", host: ");
		sb.append(host);
		sb.append(", pathname: ");
		sb.append(pathName);
		sb.append(", parameters: ");
		sb.append(parameters);
		sb.append(", content: ");
		sb.append(content);		
		sb.append("]");
		return sb.toString();
	}
	
}
