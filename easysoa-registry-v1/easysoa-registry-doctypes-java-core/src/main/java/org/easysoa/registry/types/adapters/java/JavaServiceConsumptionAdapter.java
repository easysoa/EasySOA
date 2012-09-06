package org.easysoa.registry.types.adapters.java;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.types.adapters.SoaNodeAdapter;
import org.easysoa.registry.types.java.JavaServiceConsumption;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;


/**
 * 
 * @author mkalam-alami
 *
 */
public class JavaServiceConsumptionAdapter extends SoaNodeAdapter implements JavaServiceConsumption {
    
    public JavaServiceConsumptionAdapter(DocumentModel documentModel)
            throws PropertyException, InvalidDoctypeException, ClientException {
        super(documentModel);
    }

    @Override
    public String getDoctype() {
        return DOCTYPE;
    }

    @Override
    public String getConsumedInterface() throws Exception {
        return (String) documentModel.getPropertyValue(XPATH_CONSUMEDINTERFACE);
    }
    
}
