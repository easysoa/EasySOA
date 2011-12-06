package com.openwide.easysoa.message;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A key/value object
 * @author jguillemotte
 *
 */
public class CustomField {

	private String name;
	private String value;
	
	public CustomField(){}
	
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

	public String getName() {
		return name;
	}
	
}
