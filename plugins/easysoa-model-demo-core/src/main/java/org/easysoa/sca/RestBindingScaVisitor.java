package org.easysoa.sca;

import javax.xml.namespace.QName;

/**
 * Visitor for REST bindings
 * Creates new services when <binding.rest> tags are found.
 * @author mkalam-alami
 *
 */
public class RestBindingScaVisitor extends ServiceBindingVisitorBase {
	
	public RestBindingScaVisitor(ScaImporter scaImporter) {
		super(scaImporter);
	}
	
	public boolean isOkFor(QName bindingQName) {
		return bindingQName.equals(new QName(ScaImporter.SCA_URI, "binding.rest"));
	}

	@Override
	protected String getBindingUrl() {
		String serviceUrl = compositeReader.getAttributeValue(ScaImporter.FRASCATI_URI, "uri");
		return serviceUrl;
	}

}
