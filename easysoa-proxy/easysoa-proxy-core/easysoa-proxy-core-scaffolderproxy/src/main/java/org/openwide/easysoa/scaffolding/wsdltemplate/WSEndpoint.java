package org.openwide.easysoa.scaffolding.wsdltemplate;

import java.net.URI;

/**
 * 
 * @author jguillemotte
 */
public class WSEndpoint {

	private String name;
	private URI address;
	
	public WSEndpoint(String name, URI address){
		this.name = name;
		this.address = address;
	}
	
	public URI getAddress(){
		return this.address;
	}
	
	public String getName(){
		return this.name;
	}
	
}
