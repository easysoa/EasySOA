package org.easysoa.registry.types;

import org.easysoa.registry.InvalidDoctypeException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.schema.types.Type;

/**
 * 
 * @author mkalam-alami
 *
 */
public abstract class AbstractDocumentAdapter {

    protected final DocumentModel documentModel;

    public AbstractDocumentAdapter(DocumentModel documentModel) throws InvalidDoctypeException {
        this.documentModel = documentModel;
        
        // Make sure that the model doctype is compatible
        String adapterDoctype = getDoctype();
        if (!adapterDoctype.equals(documentModel.getType())) {
            boolean isChildDoctype = false;
            for (Type type : documentModel.getDocumentType().getTypeHierarchy()) {
                if (adapterDoctype.equals(type.getName())) {
                    isChildDoctype = true;
                    break;
                }
            }
            if (!isChildDoctype) {
                throw new InvalidDoctypeException("Type " + documentModel.getType() + " is incompatible with expected type " + getDoctype());
            }
        }
    }
    
    public abstract String getDoctype();
    
    public DocumentModel getDocumentModel() {
        return documentModel;
    }
    
    public String getName() {
        return documentModel.getName();
    }

    
}
