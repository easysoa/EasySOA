package org.easysoa.sca.frascati;

import java.io.File;
import org.easysoa.sca.visitors.ApiReferenceBindingVisitor;
import org.easysoa.sca.visitors.ApiServiceBindingVisitor;
import org.easysoa.sca.visitors.ScaVisitor;
import org.eclipse.stp.sca.Composite;

/**
 * Sca Importer (Nuxeo free), uses the Registry API to register services
 * @author jguillemotte
 *
 */
public class ApiFraSCAtiScaImporter extends FraSCAtiScaImporterBase {

	/**
	 * Default constructor
	 * @throws Exception
	 */
	public ApiFraSCAtiScaImporter(File scaComposite) throws Exception {
		super(scaComposite);
	}

    @Override	
    public ScaVisitor createServiceBindingVisitor() {
        // Visitor using Notification API
    	return new ApiServiceBindingVisitor(this);
    }
    
    /**
     * creates and returns a ReferenceBindingVisitor 
     * @return
     */
    @Override
    public ScaVisitor createReferenceBindingVisitor() {
        // Visitor using Notification API
    	return new ApiReferenceBindingVisitor(this);
    }
    
    public void visitComposite(Composite composite){
    	super.visitComposite(composite);
    }

	@Override
	public String getModelProperty(String arg0, String arg1) throws Exception {
		return null;
	}

	@Override
	public Object getDocumentManager() {
		return null;
	}

	@Override
	public void setParentAppliImpl(Object appliImplModel) {
		// Nothing to do here
	}
	
}
