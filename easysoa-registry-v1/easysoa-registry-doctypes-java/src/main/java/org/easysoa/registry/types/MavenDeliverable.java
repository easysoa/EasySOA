package org.easysoa.registry.types;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.Deliverable;
import org.nuxeo.ecm.core.api.DocumentModel;


/**
 * 
 * @author mkalam-alami
 *
 */
public class MavenDeliverable extends Deliverable {

    public static final String NATURE = "maven";
    
    private String groupId;
    
    private String artifactId;
    
    public MavenDeliverable(DocumentModel documentModel) throws InvalidDoctypeException {
        super(documentModel);
        
        // Identifiers format:
        // [GROUPID]:[ARTIFACTID]
        String[] tokens = documentModel.getName().split(":");
        if (tokens.length == 2) {
            this.groupId = tokens[0];
            this.artifactId = tokens[1];
        }
        else {
            throw new InvalidDoctypeException("Could not parse document identifier '" + documentModel.getName() + "' as Maven ID");
        }
    }

    public String getGroupId() {
        return groupId;
    }
    
    public String getArtifactId() {
        return artifactId;
    }
    
}
