package org.easysoa.discovery.code;

import java.io.File;

public class DeliverableInfo {

    private String id;
    
    private String name;

    private String version;
    
    private File location;
    
    private String mavenGroupId;
    
    private String mavenArtifactId;
    
    private String mavenVersion;
    

    public DeliverableInfo(String name, File location, String mavenGroupId,
            String mavenArtifactId, String mavenVersion) {
        this.id = mavenGroupId + ":" + mavenArtifactId;
        this.name = name;
        this.version = mavenVersion;
        this.location = location;
        this.mavenGroupId = mavenGroupId;
        this.mavenArtifactId = mavenArtifactId;
        this.mavenVersion = mavenVersion;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getVersion() {
        return version;
    }

    public File getLocation() {
        return location;
    }

    public String getMavenGroupId() {
        return mavenGroupId;
    }

    public String getMavenArtifactId() {
        return mavenArtifactId;
    }

    public String getMavenVersion() {
        return mavenVersion;
    }

}
