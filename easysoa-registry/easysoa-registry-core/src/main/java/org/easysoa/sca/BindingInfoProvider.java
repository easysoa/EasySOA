package org.easysoa.sca;

//import javax.xml.namespace.QName;

/**
 * Specific to a binding implementation ex. WS, REST...
 * @author jguillemotte, mdutoo
 *
 */
public interface BindingInfoProvider {

	boolean isOkFor(String namespace, String bindingName);
	//boolean isOkFor(QName bindingQName);

	String getBindingUrl();

}
