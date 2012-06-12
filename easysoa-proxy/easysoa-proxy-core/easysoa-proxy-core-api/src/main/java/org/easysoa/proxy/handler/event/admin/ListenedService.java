package org.easysoa.proxy.handler.event.admin;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author fntangke
 *
 */


@XmlRootElement(name = "listenedService")
public class ListenedService {

	private int id;
	private String url;
	
	public ListenedService() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
}
