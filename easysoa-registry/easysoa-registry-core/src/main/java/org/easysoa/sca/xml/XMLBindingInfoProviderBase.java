package org.easysoa.sca.xml;

import org.easysoa.sca.BindingInfoProvider;

public abstract class XMLBindingInfoProviderBase implements BindingInfoProvider {

    protected XMLScaImporter xmlScaImporter;
    
    public XMLBindingInfoProviderBase(XMLScaImporter xmlScaImporter) {
    	this.xmlScaImporter = xmlScaImporter;
    }
    
}
