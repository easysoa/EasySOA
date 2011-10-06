package org.easysoa.sca.visitors;

import javax.xml.namespace.QName;

import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.ScaImporter;

/**
 * Visitor for WS bindings.
 * Creates new services when <binding.ws> tags are found.
 * @author mkalam-alami
 *
 */
public class WSBindingScaVisitor extends ServiceBindingVisitorBase {
    
    public WSBindingScaVisitor(IScaImporter scaImporter) {
        super(scaImporter);
    }
    
    public boolean isOkFor(QName bindingQName) {
        return bindingQName.equals(new QName(ScaImporter.SCA_URI, "binding.ws"));
    }

    @Override
    protected String getBindingUrl() {
        String serviceUrl = compositeReader.getAttributeValue(null, "uri"); // rather than "" ?! // TODO SCA_URI
        if (serviceUrl == null) {
            String wsdlLocation = compositeReader.getAttributeValue(ScaImporter.WSDLINSTANCE_URI , "wsdlLocation");
            if (wsdlLocation != null) {
                serviceUrl = wsdlLocation.replace("?wsdl", "");
            }
        }
        return serviceUrl;
    }

}
