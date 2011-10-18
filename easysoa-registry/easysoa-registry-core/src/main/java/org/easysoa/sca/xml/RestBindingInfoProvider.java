package org.easysoa.sca.xml;

import javax.xml.namespace.QName;

public class RestBindingInfoProvider extends XMLBindingInfoProviderBase {

	public RestBindingInfoProvider(XMLScaImporter xmlScaImporter) {
		super(xmlScaImporter);
	}

	@Override
	public boolean isOkFor(String namespace, String bindingName) {
		QName bindingQName = new QName(namespace, bindingName);
        return bindingQName.equals(new QName(XMLScaImporter.SCA_URI, "binding.rest"));
	}

	@Override
	public String getBindingUrl() {
        // getting referenced service url
        String refUrl = xmlScaImporter.getCompositeReader().getAttributeValue(XMLScaImporter.FRASCATI_URI, "uri");
        return refUrl;
	}

}
