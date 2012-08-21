package org.easysoa.discovery.code.model;

public class MavenDeliverable extends SoaNode {

    public MavenDeliverable(String groupId, String artifactId) {
        super("Deliverable", groupId + ":" + artifactId);
    }

}
