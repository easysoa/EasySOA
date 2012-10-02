package org.easysoa.registry.rest.client.types;

import java.io.Serializable;
import java.util.List;

import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.utils.ListUtils;

public class DeliverableInformation extends SoaNodeInformation implements Deliverable {

    public DeliverableInformation(String name) {
        super(new SoaNodeId(Deliverable.DOCTYPE, name), null, null);
    }

    public String getNature() throws Exception {
        return (String) properties.get(Deliverable.XPATH_NATURE);
    }

    public void setNature(String nature) throws Exception {
        properties.put(Deliverable.XPATH_NATURE, nature);
        
    }

    public String getApplication() throws Exception {
        return (String) properties.get(Deliverable.XPATH_APPLICATION);
    }

    public void setApplication(String application) throws Exception {
        properties.put(Deliverable.XPATH_APPLICATION, application);
    }

    @Override
    public String getVersion() throws Exception {
        return (String) properties.get(Deliverable.XPATH_SOAVERSION);
    }

    @Override
    public void setVersion(String version) throws Exception {
        properties.put(Deliverable.XPATH_SOAVERSION, version);
        
    }

    @Override
    public List<String> getDependencies() throws Exception {
        Serializable[] dependenciesArray = (Serializable[]) properties.get(XPATH_DEPENDENCIES);
        return ListUtils.toStringList(dependenciesArray);
    }

    @Override
    public void setDependencies(List<String> dependencies) throws Exception {
        properties.put(XPATH_DEPENDENCIES, (Serializable) dependencies);
    }

}
