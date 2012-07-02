package org.easysoa.discovery.code;

public class ServiceImplInfo {
    
    private String id;
    
    private String name;
    
    private String version;
    
    public ServiceImplInfo(DeliverableInfo deplInfo, String serviceType,
            String serviceName) {
        this.id = deplInfo.getId() + "," + serviceType + ":" + serviceName;
        this.name = serviceName;
        this.version = deplInfo.getMavenVersion();
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

}
