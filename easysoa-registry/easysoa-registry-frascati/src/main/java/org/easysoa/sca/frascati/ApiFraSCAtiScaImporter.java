package org.easysoa.sca.frascati;

import org.easysoa.sca.visitors.ApiReferenceBindingVisitor;
import org.easysoa.sca.visitors.ApiServiceBindingVisitor;
import org.easysoa.sca.visitors.ScaVisitor;
import org.eclipse.stp.sca.Composite;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * 
 * @author jguillemotte
 *
 */
public class ApiFraSCAtiScaImporter extends FraSCAtiScaImporterBase {

	/**
	 * Default constructor
	 * @throws Exception
	 */
	public ApiFraSCAtiScaImporter() throws Exception {
		super();
	}

	/**
	 * 
	 * @param documentManager
	 * @param compositeFile
	 * @throws Exception 
	 * @throws ClientException 
	 */
	// TODO : change this constructor : No need to pass a coreSession object
	public ApiFraSCAtiScaImporter(CoreSession documentManager, Blob compositeFile) throws ClientException, Exception{
		super(documentManager, compositeFile);
	}
	
    /**
     * creates and returns a ServiceBindingVisitor
     * @return
     */
    public ScaVisitor createServiceBindingVisitor() {
        // Visitor using Notification API
    	return new ApiServiceBindingVisitor(this);
    }
    
    /**
     * creates and returns a ReferenceBindingVisitor 
     * @return
     */
    public ScaVisitor createReferenceBindingVisitor() {
        // Visitor using Notification API
    	return new ApiReferenceBindingVisitor(this);
    }
    
    public void visitComposite(Composite composite){
    	// do not call the supertype method !
    	
    	//super.visitComposite(composite);
    }
	
}
