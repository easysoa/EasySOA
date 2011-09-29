package org.openwide.easysoa.scaffolding.wsdltemplate;

import javax.xml.namespace.QName;

public class WSOperation {

	private QName qName;
	
	public WSOperation(QName qname){
		this.qName = qname;
	}
	
	public String getName(){
		return this.qName.getLocalPart();
	}
	
	public QName getQName(){
		return this.qName;
	}
	
}
