package org.easysoa.registry.rest.marshalling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.utils.ListUtils;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class SoaNodeInformation implements SoaNode {

    protected SoaNodeId id;

    @JsonTypeInfo(use = Id.MINIMAL_CLASS, include = As.WRAPPER_OBJECT, property = "type")
    protected Map<String, Serializable> properties;
    
    protected List<SoaNodeId> parentDocuments;
    
    protected SoaNodeInformation() {
        
    }
    
    public SoaNodeInformation(SoaNodeId id, Map<String, Serializable> properties, List<SoaNodeId> parentDocuments) {
        this.id = id;
        this.properties = (properties == null) ? new HashMap<String, Serializable>() : properties;
        this.parentDocuments = (parentDocuments == null) ? new LinkedList<SoaNodeId>() : parentDocuments;
    }

    @Override
    public SoaNodeId getSoaNodeId() {
        return id;
    }
    
    public void setSoaNodeId(SoaNodeId id) {
        this.id = id;
    }

    @Override
    public String getSoaName() {
        return id.getName();
    }
    
    public Map<String, Serializable> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Serializable> properties) {
        this.properties = properties;
    }
    
    public Object getProperty(String xpath) throws Exception {
        return this.properties.get(xpath);
    }

    public void setProperty(String xpath, Serializable value) throws Exception {
        this.properties.put(xpath, value);
    }

    public List<SoaNodeId> getParentDocuments() {
        return parentDocuments;
    }
    
    public void setParentDocuments(List<SoaNodeId> correlatedDocuments) {
        this.parentDocuments = correlatedDocuments;
    }

    public void addParentDocument(SoaNodeId correlatedDocument) {
        this.parentDocuments.add(correlatedDocument);
    }

    public String getTitle() {
        return (String) properties.get(SoaNode.XPATH_TITLE);
    }
    
    public void setTitle(String title) {
        properties.put(SoaNode.XPATH_TITLE, title);
    }
    
    @JsonIgnore
    public boolean isPlaceholder() throws Exception {
        return (Boolean) properties.get(SoaNode.XPATH_TITLE);
    }

    @JsonIgnore
    public void setIsPlaceholder(boolean isPlaceholder) throws Exception {
        properties.put(SoaNode.XPATH_TITLE, isPlaceholder);
    }

    public String toString() {
        return this.id.toString();
    }
    
    protected void setDoctype(String doctype) {
        this.id.setType(doctype);
    }

    public List<SoaNodeId> getParentIds() throws Exception {
		Serializable[] parentsIdsArray = (Serializable[]) properties.get(XPATH_PARENTSIDS);
		List<String> parentsIdsStringList = ListUtils.toStringList(parentsIdsArray);
		List<SoaNodeId> parentsIds = new ArrayList<SoaNodeId>();
		for (String parentIdString : parentsIdsStringList) {
			SoaNodeId parentId = SoaNodeId.fromString(parentIdString);
			if (parentId != null) {
				parentsIds.add(parentId);
			}
		}
		return parentsIds;
    }
    
	public void setParentIds(List<SoaNodeId> parentIds) throws Exception {
		List<String> parentsIdsStringList = new ArrayList<String>();
		for (SoaNodeId parentId : parentIds) {
			parentsIdsStringList.add(parentId.toString());
		}
		properties.put(XPATH_PARENTSIDS, (Serializable) parentsIdsStringList);
	}
    
}
