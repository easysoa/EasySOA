package org.easysoa.registry.types.adapters.java;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.adapters.DeliverableAdapter;
import org.easysoa.registry.types.java.MavenDeliverable;
import org.nuxeo.ecm.core.api.DocumentModel;


/**
 * 
 * @author mkalam-alami
 *
 */
public class MavenDeliverableAdapter extends DeliverableAdapter implements MavenDeliverable {

    private String groupId;
    
    private String artifactId;
    
    public MavenDeliverableAdapter(DocumentModel documentModel) throws InvalidDoctypeException {
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
