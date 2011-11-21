package org.easysoa.sca;

/**
 * Specific to a binding implementation ex. WS, REST...
 * @author jguillemotte, mdutoo
 *
 */
public interface BindingInfoProvider {

	/**
	 * 
	 * @param object The object to test
	 * @return True is the object is ok, false otherwise
	 */
	boolean isOkFor(Object object);

	/**
	 * 
	 * @return
	 */
	String getBindingUrl();

}
