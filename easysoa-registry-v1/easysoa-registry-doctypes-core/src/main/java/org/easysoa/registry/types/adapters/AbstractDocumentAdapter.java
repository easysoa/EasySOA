package org.easysoa.registry.types.adapters;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.SoaMetamodelService;
import org.easysoa.registry.types.Document;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.schema.types.Type;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 *
 */
public abstract class AbstractDocumentAdapter implements Document {

    private static Logger logger = Logger.getLogger(CoreDoctypesAdapterFactory.class);
    
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
            	// Look for inherited facets
                boolean inheritsFacet = false;
				try {
					SoaMetamodelService soaMetamodel = Framework.getService(SoaMetamodelService.class);
					SchemaManager schemaManager = Framework.getService(SchemaManager.class);
	            	Set<String> inheritedFacets = soaMetamodel.getInheritedFacets(documentModel.getFacets());
	            	Set<String> adapterDoctypeFacets = new HashSet<String>(schemaManager.getDocumentType(adapterDoctype).getFacets());
	            	adapterDoctypeFacets.retainAll(inheritedFacets);
	            	if (!adapterDoctypeFacets.isEmpty()) {
	            		inheritsFacet = true;
	            	}
				} catch (Exception e) {
					logger.warn("Failed to check if type  " + documentModel.getType() 
							+ " inherits a facet from " + getDoctype() + ": " + e.getMessage());
				}
				
				if (!inheritsFacet) {
	                throw new InvalidDoctypeException("Type " + documentModel.getType() 
	                		+ " is incompatible with expected type " + getDoctype());
				}
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
