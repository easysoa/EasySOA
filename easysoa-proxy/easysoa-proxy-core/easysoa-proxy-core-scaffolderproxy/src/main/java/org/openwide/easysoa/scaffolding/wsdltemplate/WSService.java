package org.openwide.easysoa.scaffolding.wsdltemplate;

import javax.xml.namespace.QName;

public class WSService {

	private QName qname;
	
	public WSService(QName qname){
		this.qname = qname;
	}

	public String getName(){
		return qname.getLocalPart();
	}
	
}
