package org.easysoa.sca.xml;

import javax.xml.namespace.QName;

public class WSBindingInfoProvider extends XMLBindingInfoProviderBase {

	public WSBindingInfoProvider(XMLScaImporter xmlScaImporter) {
		super(xmlScaImporter);
	}

	@Override
	public boolean isOkFor(String namespace, String bindingName) {
		QName bindingQName = new QName(namespace, bindingName);
        return bindingQName.equals(new QName(XMLScaImporter.SCA_URI, "binding.ws"));
	}

	@Override
	public String getBindingUrl() {
        // getting referenced service url
        String refUrl = xmlScaImporter.getCompositeReader().getAttributeValue(null, "uri"); // rather than "" ?! // TODO SCA_URI
        if (refUrl == null) {
            String wsdlLocation = xmlScaImporter.getCompositeReader().getAttributeValue(XMLScaImporter.WSDLINSTANCE_URI , "wsdlLocation");
            if (wsdlLocation != null) {
                refUrl = wsdlLocation.replace("?wsdl", "");
            }
        }
        return refUrl;
	}

}
