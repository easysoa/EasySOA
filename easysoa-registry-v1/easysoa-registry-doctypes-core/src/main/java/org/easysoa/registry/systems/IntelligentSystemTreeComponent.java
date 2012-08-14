package org.easysoa.registry.systems;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.IntelligentSystem;
import org.easysoa.registry.types.IntelligentSystemTreeRoot;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * 
 * @author mkalam-alami
 *
 */
public class IntelligentSystemTreeComponent extends DefaultComponent implements IntelligentSystemTreeService {

    public static final String EXTENSION_POINT_CLASSIFIERS = "classifiers";
    
    public static final String EXTENSION_POINT_ISTS = "intelligentSystemTrees";
    
    private static Logger logger = Logger.getLogger(IntelligentSystemTreeComponent.class);
    
    private Map<String, Class<? extends IntelligentSystemTreeClassifier>> classifiers
            = new HashMap<String, Class<? extends IntelligentSystemTreeClassifier>>(); 
    
    private Map<String, IntelligentSystemTreeClassifier> ists = new HashMap<String, IntelligentSystemTreeClassifier>(); 
    
    // TODO Store IST information directly on the document models
    private Map<String, IntelligentSystemTreeDescriptor> istDescriptors = new HashMap<String, IntelligentSystemTreeDescriptor>();
    
    @Override
    public void registerContribution(Object contribution, String extensionPoint,
            ComponentInstance contributor) throws Exception {
        if (EXTENSION_POINT_CLASSIFIERS.equals(extensionPoint)) {
            IntelligentSystemTreeClassifierDescriptor descriptor = null;
            try {
                // Validate descriptor
                descriptor = (IntelligentSystemTreeClassifierDescriptor) contribution;
                if (descriptor.name == null || descriptor.name.isEmpty()) {
                    throw new InvalidParameterException("'name' must not be null");
                }
                
                // Register classifier class (override potiential previous descriptor)
                Class<? extends IntelligentSystemTreeClassifier> classifierClass = Class.forName(descriptor.className.trim()).asSubclass(IntelligentSystemTreeClassifier.class);
                classifiers.put(descriptor.name, classifierClass);
            }
            catch (Exception e) {
                String contribName = (descriptor != null) ? "'" + descriptor.name + "'" : "";
                logger.error("Failed to register contribution " + contribName + " to '" + EXTENSION_POINT_CLASSIFIERS + "'", e);
            }
        }
        else if (EXTENSION_POINT_ISTS.equals(extensionPoint)) {
            IntelligentSystemTreeDescriptor descriptor = null;
            try {
                // Validate descriptor
                descriptor = (IntelligentSystemTreeDescriptor) contribution;
                if (descriptor.getName() == null || descriptor.getName().isEmpty()) {
                    throw new InvalidParameterException("'name' must not be null");
                }
                if (descriptor.getClassifier() == null || descriptor.getClassifier().isEmpty()) {
                    throw new InvalidParameterException("'classifier' must not be null");
                }
                if (!classifiers.containsKey(descriptor.getClassifier())){
                    throw new InvalidParameterException("Classifier '" + descriptor.getClassifier() + "' does not exist");
                }
                
                // Register descriptor
                istDescriptors.put(descriptor.getName(), descriptor);
                
                // Initialize and register IST
                Class<? extends IntelligentSystemTreeClassifier> istClass = classifiers.get(descriptor.getClassifier());
                IntelligentSystemTreeClassifier ist = istClass.newInstance();
                ist.initialize(descriptor.getParameters());
                ists.put(descriptor.getName(), ist);
            }
            catch (Exception e) {
                String contribName = (descriptor != null) ? "'" + descriptor.getName() + "'" : "";
                logger.error("Failed to register contribution " + contribName + " to '" + EXTENSION_POINT_CLASSIFIERS + "'", e);
            }
            
        }
    }
    
    public void handleDocumentModel(CoreSession documentManager, DocumentModel model, boolean force) throws Exception {
        // Filter documents from other intelligent trees
        String parentType = documentManager.getDocument(model.getParentRef()).getType();
        if (!force && IntelligentSystem.DOCTYPE.equals(parentType) || IntelligentSystemTreeRoot.DOCTYPE.equals(parentType)) {
            return;
        }
        
        // Find the document source & proxies
        DocumentService documentService = Framework.getService(DocumentService.class);
        DocumentModelList proxyModels = documentService.findAllInstances(documentManager, model);
        DocumentModel sourceModel = null;
        for (DocumentModel instance : proxyModels) {
            if (!instance.isProxy()) {
                sourceModel = instance;
            }
        }
        if (sourceModel == null) {
            logger.error("Can't find source document for " + model + ", won't be add it to intelligent system trees");
            return;
        }
        proxyModels.remove(sourceModel);
        
        // Run the classifiers
        for (Entry<String, IntelligentSystemTreeClassifier> istEntry : ists.entrySet()) {
            IntelligentSystemTreeDescriptor istDescriptor = istDescriptors.get(istEntry.getKey());

            // Filter disabled ISTs
            if (istDescriptor.isEnabled()) {
                // Fetch or create the IST model
                DocumentModel istModel = documentService.find(documentManager,
                        IntelligentSystemTreeRoot.DOCTYPE, istDescriptor.getClassifier()+':'+istDescriptor.getName());
                if (istModel == null) {
                    istModel = documentService.create(documentManager, IntelligentSystemTreeRoot.DOCTYPE,
                            "/default-domain/workspaces", istDescriptor.getClassifier()+':'+istDescriptor.getName(),
                            istDescriptor.getTitle());
                }

                // Find eventual presence of model in the IST
                DocumentModel existingModel = null;
                for (DocumentModel proxyModel : proxyModels) {
                    if (proxyModel.getPathAsString().startsWith(istModel.getPathAsString())) {
                        existingModel = proxyModel;
                    }
                }
                
                // Run classifier!
                String classification = istEntry.getValue().classify(model);
                
                // Handling when model is accepted
                if (classification != null) {
                    // Make path uniform
                    if (classification.length() > 0 && classification.charAt(0) == '/') {
                        classification = classification.substring(1);
                    }
                    if (classification.length() > 0 && classification.charAt(classification.length() - 1) == '/') {
                        classification = classification.substring(0, classification.length() - 1);
                    }
                    
                    // Check if the model is at its right place
                    PathRef expectedParentPath = new PathRef(istModel.getPathAsString() +
                            (("".equals(classification)) ? "" : "/" + classification));

                    // Ensure the parent systems exist
                    if (!documentManager.exists(expectedParentPath)) {
                        String[] parentSystems = classification.split("/");
                        DocumentModel currentFolder = istModel;
                        for (String parentSystem : parentSystems) {
                            String childPath = currentFolder.getPathAsString() + '/' + parentSystem;
                            if (!documentManager.exists(new PathRef(childPath))) {
                                currentFolder = documentService.create(documentManager, IntelligentSystem.DOCTYPE,
                                        currentFolder.getPathAsString(), parentSystem, parentSystem);
                            }
                        }
                    }
                    
                    // If the model is missing, create a proxy
                    if (existingModel == null) {
                        documentManager.createProxy(sourceModel.getRef(), expectedParentPath);
                    }

                    // If in the IST but not at the right place, move the document
                    else if (!existingModel.getPathAsString().equals(expectedParentPath.toString() + '/' + existingModel.getName())){
                        List<DocumentModel> parents = documentManager.getParentDocuments(existingModel.getRef());
                        documentManager.move(existingModel.getRef(), expectedParentPath, existingModel.getName());
                        removeEmptyParentSystems(documentManager, parents);
                    }
                }
                
                // Handling when model is rejected
                else {
                    // If the model was in the IST, delete it
                    for (DocumentModel proxyModel : proxyModels) {
                        if (proxyModel.getPathAsString().startsWith(istModel.getPathAsString())) {
                            List<DocumentModel> parents = documentManager.getParentDocuments(proxyModel.getRef());
                            documentManager.removeDocument(proxyModel.getRef());
                            removeEmptyParentSystems(documentManager, parents);
                            break;
                        }
                    }
                }
            }
        }
        
    }

    private void removeEmptyParentSystems(CoreSession documentManager, List<DocumentModel> hierarchy)
            throws ClientException {
        for (int i = hierarchy.size() - 2; i >= 0; i--) {
            DocumentModel parentModel = hierarchy.get(i);
            if (!documentManager.hasChildren(parentModel.getRef())
                    && IntelligentSystem.DOCTYPE.equals(parentModel.getType())) {
                documentManager.removeDocument(parentModel.getRef());
            }
            else {
                break;
            }
        }
    }
    
}
