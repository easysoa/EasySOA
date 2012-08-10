package org.easysoa.registry.systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.common.xmap.annotation.XContent;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * 
 * @author mkalam-alami
 *
 */
@XObject("intelligentSystemTree")
public class IntelligentSystemTreeDescriptor {
    
    @XNode("@name")
    private String name;
    
    @XNode("@enabled")
    private Boolean enabled;

    @XNode("title")
    private String title;
    
    @XNode("classifier")
    private String classifier;

    @XNodeList(value = "parameters/parameter", type = ArrayList.class, componentType = Parameter.class)
    private List<Parameter> parameters;
    
    @XObject("parameter")
    public static class Parameter {

        @XNode("@name")
        protected String key;
        
        @XContent
        protected String value;

    }

    private Map<String, String> parametersAsHashMapCache;

    public String getName() {
        return name;
    }
    
    public boolean isEnabled() {
        return (enabled != null) ? enabled : true;
    }
    
    public String getTitle() {
        return (title != null) ? title : name;
    }

    public String getClassifier() {
        return classifier;
    }
    
    public Map<String, String> getParameters() {
        if (parametersAsHashMapCache == null) {
            parametersAsHashMapCache = new HashMap<String, String>();
            for (Parameter parameter : parameters) {
                parametersAsHashMapCache.put(parameter.key, parameter.value.trim());
            }
        }
        return parametersAsHashMapCache;
    }
    
    public void mergeWith(IntelligentSystemTreeDescriptor descriptor) {
        if (descriptor.enabled != null) {
            this.enabled = descriptor.enabled;
        }
        if (descriptor.title != null) {
            this.title = descriptor.title;
        }
        if (descriptor.classifier != null) {
            this.classifier = descriptor.classifier;
        }
        if (descriptor.parameters != null) {
            // New descriptor parameters will override the previous ones during the HashMap creation
            this.parameters.addAll(descriptor.parameters);
            this.parametersAsHashMapCache = null;
        }
    }
}
