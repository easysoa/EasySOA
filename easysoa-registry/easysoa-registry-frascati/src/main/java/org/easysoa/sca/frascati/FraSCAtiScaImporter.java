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
public class FraSCAtiScaImporter extends FraSCAtiScaImporterBase {

	/**
	 * Default constructor
	 * @throws Exception
	 */
	public FraSCAtiScaImporter() throws Exception {
		super();
	}

	/**
	 * 
	 * @param documentManager
	 * @param compositeFile
	 * @throws Exception 
	 * @throws ClientException 
	 */
	public FraSCAtiScaImporter(CoreSession documentManager, Blob compositeFile) throws ClientException, Exception{
		super(documentManager, compositeFile);
	}
	
    /**
     * creates and returns a ServiceBindingVisitor
     * @return
     */
	@Override
    public ScaVisitor createServiceBindingVisitor() {
        return new ServiceBindingVisitor(this);
    }
    
    /**
     * creates and returns a ReferenceBindingVisitor 
     * @return
     */
    public ScaVisitor createReferenceBindingVisitor() {
    	return new ReferenceBindingVisitor(this);
    }
    
    public void visitComposite(Composite composite){
    	super.visitComposite(composite);
    }
	
}
