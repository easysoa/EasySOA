package org.easysoa.registry.types.adapters;

import java.io.Serializable;
import java.util.List;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.Deliverable;
import org.easysoa.registry.utils.ListUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;


/**
 * 
 * @author mkalam-alami
 *
 */
public class DeliverableAdapter extends SoaNodeAdapter implements Deliverable {

    public DeliverableAdapter(DocumentModel documentModel)
            throws InvalidDoctypeException, PropertyException, ClientException {
        super(documentModel);
    }
    
    public String getDoctype() {
        return Deliverable.DOCTYPE;
    }

    public String getNature() throws ClientException {
        return (String) documentModel.getPropertyValue(XPATH_NATURE);
    }
    
    public void setNature(String nature) throws ClientException {
        documentModel.setPropertyValue(XPATH_NATURE, nature);
    }

    public String getApplication() throws ClientException {
        return (String) documentModel.getPropertyValue(XPATH_APPLICATION);
    }
    
    public void setApplication(String application) throws ClientException {
        documentModel.setPropertyValue(XPATH_NATURE, application);
    }

    public String getVersion() throws Exception {
        return (String) documentModel.getPropertyValue(XPATH_SOAVERSION);
    }

    @Override
    public void setVersion(String version) throws Exception {
        documentModel.setPropertyValue(XPATH_SOAVERSION, version);
        
    }

    @Override
    public List<String> getDependencies() throws Exception {
        Serializable[] dependenciesArray = (Serializable[]) documentModel.getPropertyValue(XPATH_DEPENDENCIES);
        return ListUtils.toStringList(dependenciesArray);
    }

    @Override
    public void setDependencies(List<String> dependencies) throws Exception {
        documentModel.setPropertyValue(XPATH_DEPENDENCIES, dependencies.toArray());
    }
}
