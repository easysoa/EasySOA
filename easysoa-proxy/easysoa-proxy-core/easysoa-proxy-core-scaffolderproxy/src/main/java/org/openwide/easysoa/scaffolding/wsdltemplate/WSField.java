package org.openwide.easysoa.scaffolding.wsdltemplate;

public class WSField {

	private String name;
	private String type;
	
	public WSField(String name, String type){
		this.name = name;
		this.type = type;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getType(){
		return this.type;
	}
	
}
