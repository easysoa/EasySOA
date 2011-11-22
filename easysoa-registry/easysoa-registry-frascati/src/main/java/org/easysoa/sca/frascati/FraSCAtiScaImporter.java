package org.easysoa.sca.frascati;

import java.io.File;

import org.easysoa.api.EasySOAApiSession;
import org.easysoa.registry.frascati.FraSCAtiService;
import org.easysoa.sca.visitors.ReferenceBindingVisitor;
import org.easysoa.sca.visitors.ScaVisitor;
import org.easysoa.sca.visitors.ServiceBindingVisitor;
import org.easysoa.services.DocumentService;
import org.eclipse.stp.sca.Composite;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

/**
 * Sca Importer : Uses Nuxeo API's to register services
 * 
 * @author jguillemotte
 * 
 */
public class FraSCAtiScaImporter extends FraSCAtiScaImporterBase {

    private DocumentModel parentAppliImplModel;
    // private Blob blobCompositeFile;

    // Needed to fetch documents (should be gone when the API is complete)
    private DocumentService documentService;
    private CoreSession documentManager;

    /**
     * 
     * 
     * @param documentManager
     * @param compositeFile
     * @throws Exception
     * @throws ClientException
     */
    public FraSCAtiScaImporter(EasySOAApiSession api, File compositeFile, CoreSession documentManager) throws ClientException, Exception {
        super(api, compositeFile);

        // WARNING: The constructor must match its validation and use in org.easysoa.sca.extension.ScaImporterComponent
        
        // this.blobCompositeFile = new FileBlob(compositeFile);
        this.documentManager = documentManager;
        if (documentManager != null) {
            this.documentService = Framework.getRuntime().getService(DocumentService.class);
            this.parentAppliImplModel = documentService.getDefaultAppliImpl(documentManager);
        }
        init();
    }

    /**
     * Initialization
     * 
     * @throws Exception
     */
    private void init() throws Exception {
        frascatiService = Framework.getService(FraSCAtiService.class);
    }

    @Override
    public ScaVisitor createServiceBindingVisitor() {
        return new ServiceBindingVisitor(this, getApi());
    }

    @Override
    public ScaVisitor createReferenceBindingVisitor() {
        return new ReferenceBindingVisitor(this, getApi());
    }

    @Override
    public void visitComposite(Composite composite) {
        super.visitComposite(composite);
    }

    @Override
    public String getModelProperty(String arg0, String arg1) throws Exception {
        return (String) (parentAppliImplModel.getProperty(arg0, arg1));
    }

    @Override
    public Object getDocumentManager() {
        return documentManager;
    }

    @Override
    public void setParentAppliImpl(Object appliImplModel) {
        if (appliImplModel instanceof DocumentModel) {
            parentAppliImplModel = (DocumentModel) appliImplModel;
        }
    }

}
