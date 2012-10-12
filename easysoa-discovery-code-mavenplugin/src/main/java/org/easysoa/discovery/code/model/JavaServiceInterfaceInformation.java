package org.easysoa.discovery.code.model;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;

public class JavaServiceInterfaceInformation {

    private SoaNodeId mavenDeliverableId;
    
    private String interfaceName;

    public JavaServiceInterfaceInformation(String mavenGroupId, String mavenArtifactId, String interfaceName) throws Exception {
        this.interfaceName = interfaceName;
        this.mavenDeliverableId = new MavenDeliverableInformation(mavenGroupId, mavenGroupId).getSoaNodeId();
    }
    
    public String getInterfaceName() {
        return interfaceName;
    }
    
    public SoaNodeId getMavenDeliverableId() {
        return mavenDeliverableId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return obj.equals(interfaceName);
        }
        else {
            return super.equals(obj);
        }
    }
    
}
