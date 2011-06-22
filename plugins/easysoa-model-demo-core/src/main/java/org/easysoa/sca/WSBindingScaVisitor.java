package org.easysoa.sca;

import javax.xml.namespace.QName;

import org.nuxeo.ecm.core.api.ClientException;

/**
 * Visitor for WS bindings.
 * Creates new services when <binding.ws> tags are found.
 * @author mkalam-alami
 *
 */
public class WSBindingScaVisitor extends ServiceBindingVisitorBase {
	
	public WSBindingScaVisitor(ScaImporter scaImporter) {
		super(scaImporter);
	}
	
	public boolean isOkFor(QName bindingQName) {
		return bindingQName.equals(new QName(ScaImporter.SCA_URI, "binding.ws"));
	}

	@Override
	protected String getBindingUrl() {
		String serviceUrl = compositeReader.getAttributeValue("", "uri");
		if (serviceUrl == null) {
			String wsdlLocation = compositeReader.getAttributeValue(ScaImporter.WSDLINSTANCE_URI , "wsdlLocation");
			if (wsdlLocation != null) {
				serviceUrl = wsdlLocation.replace("?wsdl", "");
			}
		}
		return serviceUrl;
	}

	@Override
	public void postCheck() throws ClientException {
		// TODO Auto-generated method stub
		
	}

}
