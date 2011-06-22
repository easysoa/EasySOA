package org.easysoa.sca;

import javax.xml.namespace.QName;

import org.nuxeo.ecm.core.api.ClientException;

public interface ScaVisitor {

	boolean isOkFor(QName bindingQName);

	void visit() throws ClientException;

	void postCheck() throws ClientException;

}
