package org.easysoa.discovery.rest.model;


public class Endpoint extends SoaNode {

    private String url;
    private String wsdl;

    public Endpoint() {}
    
    public Endpoint(Environment environment, String url, String wsdl, String version) {
        super(environment.getName() + "," + url, url, version);
        this.setUrl(url);
        this.setWsdl(wsdl);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getWsdl() {
        return wsdl;
    }

    public void setWsdl(String wsdl) {
        this.wsdl = wsdl;
    }
    
    @Override
    public SoaNodeType getSoaNodeType() {
        return SoaNodeType.Endpoint;
    }
    
}
