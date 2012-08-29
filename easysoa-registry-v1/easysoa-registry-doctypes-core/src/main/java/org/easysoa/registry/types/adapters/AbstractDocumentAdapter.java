package org.easysoa.registry.types.adapters;

import java.io.Serializable;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.Document;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.schema.types.Type;

/**
 * 
 * @author mkalam-alami
 *
 */
public abstract class AbstractDocumentAdapter implements Document {

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

    public String getTitle() throws ClientException {
        return documentModel.getTitle();
    }

    public void setTitle(String title) throws PropertyException, ClientException {
        documentModel.setPropertyValue(Document.XPATH_TITLE, title);
    };
    
    public Object getProperty(String xpath) throws Exception {
        return documentModel.getPropertyValue(xpath);
    }
    
    public void setProperty(String xpath, Serializable value) throws Exception {
        documentModel.setPropertyValue(xpath, value);
    }

}
