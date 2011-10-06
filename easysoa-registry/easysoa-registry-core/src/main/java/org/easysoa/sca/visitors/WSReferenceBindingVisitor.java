package org.easysoa.sca.visitors;

import javax.xml.namespace.QName;

import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.ScaImporter;

/**
 * Visitor for WS reference bindings
 * Creates a new reference when a <binding.rest> tags is found.
 * @author mkalam-alami
 *
 */
public class WSReferenceBindingVisitor extends ReferenceBindingVisitorBase {
    
    public WSReferenceBindingVisitor(IScaImporter scaImporter) {
        super(scaImporter);
    }
    
    public boolean isOkFor(QName bindingQName) {
        return bindingQName.equals(new QName(ScaImporter.SCA_URI, "binding.ws"));
    }

    @Override
    protected String getBindingUrl() {
        // getting referenced service url
        String refUrl = compositeReader.getAttributeValue(null, "uri"); // rather than "" ?! // TODO SCA_URI
        if (refUrl == null) {
            String wsdlLocation = compositeReader.getAttributeValue(ScaImporter.WSDLINSTANCE_URI , "wsdlLocation");
            if (wsdlLocation != null) {
                refUrl = wsdlLocation.replace("?wsdl", "");
            }
        }
        return refUrl;
    }

}
