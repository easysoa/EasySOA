package org.easysoa.sca.visitors;

import javax.xml.namespace.QName;

import org.easysoa.sca.ScaImporter;

/**
 * Visitor for REST reference bindings
 * Creates a new reference when a <binding.rest> tags is found.
 * @author mdutoo
 *
 */
public class RestReferenceBindingVisitor extends ReferenceBindingVisitorBase {
    
    public RestReferenceBindingVisitor(ScaImporter scaImporter) {
        super(scaImporter);
    }
    
    public boolean isOkFor(QName bindingQName) {
        return bindingQName.equals(new QName(ScaImporter.SCA_URI, "binding.rest"));
    }

    @Override
    protected String getBindingUrl() {
        // getting referenced service url
        String refUrl = compositeReader.getAttributeValue(ScaImporter.FRASCATI_URI, "uri");
        return refUrl;
    }

}
