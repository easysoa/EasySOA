package org.easysoa.sca.visitors;

import org.easysoa.sca.IScaImporter;

public interface BindingVisitorFactory {

	/**
	 * Return the document manager
	 * @return The document manager or null if no document manager is set
	 */
	public Object getDocumentManager();
	
	/**
	 * Create and return a new ScaVisitor object
	 * @return
	 */
	public ScaVisitor createReferenceBindingVisitor(IScaImporter scaImporter);
	
	/**
	 * Create and return a new ScaVisitor object
	 * @return
	 */
	public ScaVisitor createServiceBindingVisitor(IScaImporter scaImporter);
	
}