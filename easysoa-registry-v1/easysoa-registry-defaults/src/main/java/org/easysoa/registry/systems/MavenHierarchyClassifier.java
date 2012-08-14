package org.easysoa.registry.systems;

import java.util.Map;

import org.apache.log4j.Logger;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.types.java.MavenDeliverable;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * System hierachy made of Maven groups.
 * 
 * @author mkalam-alami
 *
 */
public class MavenHierarchyClassifier implements IntelligentSystemTreeClassifier {

    public static final String DEFAULT_ENVIRONMENT = "Unspecified";

    private static Logger logger = Logger.getLogger(MavenHierarchyClassifier.class);
    
    @Override
    public void initialize(Map<String, String> params) {
        // No parameters
    }

    @Override
    public String classify(DocumentModel model) throws ClientException {
        // Filter non-Maven documents
        if (!Deliverable.DOCTYPE.equals(model.getType())) {
            return null;
        }
        Deliverable deliverable = model.getAdapter(Deliverable.class);
        if (!MavenDeliverable.NATURE.equals(deliverable.getNature())) {
            return null;
        }
        
        try {
            // Gather information
            MavenDeliverable mavenDeliverable = model.getAdapter(MavenDeliverable.class);
            String groupId = mavenDeliverable.getGroupId();
            
            // Build classification
            String classification = "", groupPrefix = "";
            String[] groups = groupId.split("\\.");
            for (String group : groups) {
                classification += groupPrefix + group + "/";
                groupPrefix += group + ".";
            }
            
            // Remove first group (usually "org" or something, which is not interesting)
            if (groups.length > 1) {
                classification = classification.replaceFirst("^[^/]*/", "");
            }
            
            return classification;
        }
        catch (Exception e) {
            logger.warn("Failed to classify deliverable: " + e.getMessage());
            return null;
        }
        
    }

}
