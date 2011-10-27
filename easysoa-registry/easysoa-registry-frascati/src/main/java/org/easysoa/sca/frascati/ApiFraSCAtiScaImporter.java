package org.easysoa.sca.frascati;

import org.easysoa.sca.visitors.ReferenceBindingVisitor;
import org.easysoa.sca.visitors.ScaVisitor;
import org.easysoa.sca.visitors.ServiceBindingVisitor;
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
	public ApiFraSCAtiScaImporter(CoreSession documentManager, Blob compositeFile) throws ClientException, Exception{
		super(documentManager, compositeFile);
	}
	
    /**
     * creates and returns a ServiceBindingVisitor
     * @return
     */
    public ScaVisitor createServiceBindingVisitor() {
        //return new ServiceBindingVisitor(this);
    	// TODO return visitors that works with registry api (nuxeo free)
    	return null;
    }
    
    /**
     * creates and returns a ReferenceBindingVisitor 
     * @return
     */
    public ScaVisitor createReferenceBindingVisitor() {
    	// TODO return visitors that works with registry api (nuxeo free)
    	//return new ReferenceBindingVisitor(this);
    	return null;
    }
    
    public void visitComposite(Composite composite){
    	super.visitComposite(composite);
    }
	
}
