package com.openwide.easysoa.monitoring.soa;

public class Node {

	private String description;
	private String url;
	private String title;
	
	/**
	 * @param url Node url
	 */
	public Node(String url){
		if(url == null || "".equals(url)){
			throw new IllegalArgumentException("url must not be null or empty");
		}
		this.url = url;
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	
}
