package org.easysoa.sca;

import javax.xml.namespace.QName;

public interface ScaVisitor {

	/**
	 * If this visitor applies 
	 * @param bindingQName
	 * @return
	 */
	boolean isOkFor(QName bindingQName);

	/**
	 * 
	 * @throws Exception when local, not fatal error
	 */
	void visit() throws Exception;

	/**
	 * To resolve linking when visit is finished
	 * @throws Exception when local, not fatal error
	 */
	void postCheck() throws Exception;

	/**
	 * Describes the scaVisitor instance for debugging purpose
	 * @return
	 */
	String getDescription();

}
