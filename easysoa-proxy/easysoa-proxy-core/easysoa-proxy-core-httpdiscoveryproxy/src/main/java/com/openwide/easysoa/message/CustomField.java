package com.openwide.easysoa.message;

/**
 * A key/value object
 * @author jguillemotte
 *
 */
public class CustomField {

	private String name;
	private String value;
	
	public CustomField(){
		this.name = "";
		this.value = "";
	}
	
	public CustomField(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
