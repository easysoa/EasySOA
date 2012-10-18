package org.easysoa.registry;

import java.util.Set;

import org.apache.log4j.Logger;
import org.easysoa.registry.systems.IntelligentSystemTreeService;
import org.easysoa.registry.types.Repository;
import org.easysoa.registry.types.SoaNode;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.schema.types.CompositeType;
import org.nuxeo.ecm.core.schema.types.Field;
import org.nuxeo.ecm.core.schema.types.Schema;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 *
 */
public class RepositoryManagementListener implements EventListener {

    private static Logger logger = Logger.getLogger(RepositoryManagementListener.class);
    
    @Override
    public void handleEvent(Event event) throws ClientException {
    	
        // Ensure event nature
        EventContext context = event.getContext();
        if (!(context instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext documentContext = (DocumentEventContext) context;
        DocumentModel sourceDocument = documentContext.getSourceDocument();
        if (!sourceDocument.hasSchema(SoaNode.SCHEMA)) {
            return;
        }
        
        // Initialize
        CoreSession documentManager = documentContext.getCoreSession();
        DocumentService documentService;
        SoaMetamodelService soaMetamodel;
        SchemaManager schemaManager;
        try {
			documentService = Framework.getService(DocumentService.class);
			soaMetamodel = Framework.getService(SoaMetamodelService.class);
			schemaManager = Framework.getService(SchemaManager.class);
		} catch (Exception e) {
			logger.error("A required service is missing, aborting SoaNode repository management: " + e.getMessage());
			return;
		}
        
        // Working copy/proxies management
        if (!DocumentEventTypes.ABOUT_TO_REMOVE.equals(event.getName())) {
	        try {
	            
	            // If a document has been created through the Nuxeo UI, move it to the repository and leave only a proxy
	            String sourceFolderPath = documentService.getSourceFolderPath(sourceDocument.getType());
	            DocumentModel parentModel = documentManager.getDocument(sourceDocument.getParentRef());
	            if (!sourceDocument.isProxy() && !parentModel.getPathAsString().equals(sourceFolderPath)
	                    || sourceDocument.isProxy() && parentModel.hasSchema(SoaNode.SCHEMA)
	                        && !sourceDocument.getPathAsString().startsWith(Repository.REPOSITORY_PATH)) {
	                documentService.ensureSourceFolderExists(documentManager, sourceDocument.getType());
	                String soaName = (String) sourceDocument.getPropertyValue(SoaNode.XPATH_SOANAME);
	                if (soaName == null || soaName.isEmpty()) {
	                    sourceDocument.setPropertyValue(SoaNode.XPATH_SOANAME, sourceDocument.getName());
	                }
	                
	                PathRef sourcePathRef = new PathRef(documentService.getSourcePath(
	                        documentService.createSoaNodeId(sourceDocument)));
	                if (documentManager.exists(sourcePathRef)) {
	                    // If the source document already exists, only keep one
	                	DocumentModel repositoryDocument = documentManager.getDocument(sourcePathRef);
	                    repositoryDocument.copyContent(sourceDocument); // Merge
	                    documentManager.saveDocument(repositoryDocument);
	                    documentManager.save();
	                    documentManager.removeDocument(sourceDocument.getRef());
	                    sourceDocument = repositoryDocument;
	                }
	                else {
	                    // Move to repository otherwise
	                	sourceDocument = documentManager.move(sourceDocument.getRef(),
	                        new PathRef(sourceFolderPath),
	                        sourceDocument.getName());
	                }
	                
	                // Create a proxy at the expected location
	                if (documentService.isSoaNode(documentManager, parentModel.getType())) {
	                    parentModel = documentService.find(documentManager, documentService.createSoaNodeId(parentModel));
	                }
	                documentManager.createProxy(sourceDocument.getRef(), parentModel.getRef());
	            }
	            documentManager.save();
	            
	            // Intelligent system trees update
	            IntelligentSystemTreeService intelligentSystemTreeServiceCache =
	                    Framework.getService(IntelligentSystemTreeService.class);
	            intelligentSystemTreeServiceCache.handleDocumentModel(documentManager, sourceDocument,
	                    !DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName()));
	            documentManager.save();
	            
	        } catch (Exception e) {
	            logger.error("Failed to check document after creation", e);
	        }
        }

    	Set<String> inheritedFacets = soaMetamodel.getInheritedFacets(sourceDocument.getFacets());
    	if (!inheritedFacets.isEmpty()) {

	        // Copy metadata from inherited facets to children
    		if (DocumentEventTypes.DOCUMENT_UPDATED.equals(event.getName())) {
		        try {
		        	DocumentModelList sourceDocumentChildren = documentService.getChildren(documentManager, sourceDocument.getRef(), null);
		        	for (String inheritedFacet : inheritedFacets) {
		        		for (DocumentModel sourceDocumentChild : sourceDocumentChildren) {
		        			if (sourceDocumentChild.hasFacet(inheritedFacet)) {
		        				CompositeType facetToCopy = schemaManager.getFacet(inheritedFacet);
		        				for (String schemaToCopy : facetToCopy.getSchemaNames()) {
		        					sourceDocumentChild.setProperties(schemaToCopy, sourceDocument.getProperties(schemaToCopy));
		        				}
		        			}
		        		}
		        	}
		        	documentManager.saveDocuments(sourceDocumentChildren.toArray(new DocumentModel[]{}));
		        }
		        catch (Exception e) {
		        	logger.error("Failed to clone metadata from a SoaNode to its children", e);
		        }
    		}
    		
    		else {
    	        // Reset metadata after move/deletion
    			if (!DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName())
    					&& !DocumentEventTypes.DOCUMENT_CREATED_BY_COPY.equals(event.getName())) {
    				if (DocumentEventTypes.ABOUT_TO_REMOVE.equals(event.getName())) {
    					if (!sourceDocument.isProxy()) {
    						// Working copy deleted, nothing to do
    						sourceDocument = null;
    					}
    					else {
    						// Proxy deleted, update the true document
    						sourceDocument = documentManager.getWorkingCopy(sourceDocument.getRef());
    					}
    				}
    				
    				if (sourceDocument != null) {
	    				for (String inheritedFacet : inheritedFacets) {
	    					CompositeType facetToReset = schemaManager.getFacet(inheritedFacet);
	        				for (Schema schemaToReset : facetToReset.getSchemas()) {
	        					for (Field fieldToReset : schemaToReset.getFields()) {
		        					sourceDocument.setPropertyValue(fieldToReset.getName().toString(), null);
	        					}
	        				}
			        	}
	    				documentManager.saveDocument(sourceDocument);
    				}
		        }
    			
    	        // Copy metadata from inherited facets from parents
    			if (!DocumentEventTypes.ABOUT_TO_REMOVE.equals(event.getName())) {
    				try {
			        	DocumentModelList sourceDocumentParents = documentService.findAllParents(documentManager, sourceDocument);
			        	for (String inheritedFacet : inheritedFacets) {
			        		for (DocumentModel sourceDocumentParent : sourceDocumentParents) {
			        			if (sourceDocumentParent.hasFacet(inheritedFacet)) {
			        				// XXX What if several parents have the same inherited facet?
			        				CompositeType facetToCopy = schemaManager.getFacet(inheritedFacet);
			        				for (String schemaToCopy : facetToCopy.getSchemaNames()) {
			        					sourceDocument.setProperties(schemaToCopy, sourceDocumentParent.getProperties(schemaToCopy));
			        				}
			        			}
			        		}
			        	}
			        	documentManager.saveDocument(sourceDocument);
			        }
			        catch (Exception e) {
			        	logger.error("Failed to clone metadata from a SoaNode to its children", e);
			        }
    			}
    		}

    	}
        
    }

}
