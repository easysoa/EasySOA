package com.axxx.dps.apv.ws;

import javax.xml.bind.annotation.XmlElement;

/**
 * TODO XmlAdapter to have the value as serialization
 * 
 * @author mdutoo
 *
 */
public enum TypeStructure {

	ASSOCIATION_NAT("Association Nat.");
	
    @XmlElement(nillable=false, required=true)
	private String name = null;

	private TypeStructure(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
