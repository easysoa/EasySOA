/**
 * 
 */
package org.easysoa.sca.visitors;

import org.easysoa.sca.IScaImporter;

/**
 * @author jguillemotte
 *
 */
public class ApiBindingVisitorFactory extends AbstractBindingVisitorFactoryBase {

	private Object documentManager;

	public ApiBindingVisitorFactory(){
		super(null);
	}
	
	public Object getDocumentManager() {
		return this.documentManager;
	}
	
	/**
	 * Create and return a new ScaVisitor object
	 * @return
	 */
	public ScaVisitor createReferenceBindingVisitor(IScaImporter scaImporter){
    	return new ApiReferenceBindingVisitor(scaImporter);
	}
	
	/**
	 * Create and return a new ScaVisitor object
	 * @return
	 */
	public ScaVisitor createServiceBindingVisitor(IScaImporter scaImporter){
    	return new ApiServiceBindingVisitor(scaImporter);
	}	
	
	// How to create several bindingVisitors ?
	
}
