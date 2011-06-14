package org.easysoa.sca;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.nuxeo.ecm.core.api.ClientException;

public interface ScaVisitor {

	boolean isOkFor(QName bindingQName);

	void visit(XMLStreamReader compositeReader, String serviceNameString) throws ClientException;

}
