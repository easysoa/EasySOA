package org.easysoa.rest.soapui;

import java.util.*;


public class SoapUIWSDL {

    String url;
    
    String contents;
    
    String name;
    
    String bindingName;
    
    String endpointUrl;
    
    List<SoapUIOperation> operations = new LinkedList<SoapUIOperation>();
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBindingName() {
        return bindingName;
    }

    public void setBindingName(String bindingName) {
        this.bindingName = bindingName;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }
    
    public List<SoapUIOperation> getOperations() {
        return operations;
    }
    
    public void addOperation(SoapUIOperation operation) {
        this.operations.add(operation);
    }
    
}
