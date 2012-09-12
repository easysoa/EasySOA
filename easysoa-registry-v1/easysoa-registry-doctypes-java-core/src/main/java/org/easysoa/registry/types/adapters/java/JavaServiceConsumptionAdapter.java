package org.easysoa.registry.types.adapters.java;

import java.util.List;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.types.adapters.SoaNodeAdapter;
import org.easysoa.registry.types.java.JavaServiceConsumption;
import org.easysoa.registry.types.java.JavaServiceImplementation;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;
import org.nuxeo.runtime.api.Framework;


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
        return JavaServiceConsumption.DOCTYPE;
    }

    @Override
    public String getConsumedInterface() throws Exception {
        return (String) documentModel.getPropertyValue(XPATH_CONSUMEDINTERFACE);
    }

    @Override
    public List<SoaNodeId> getConsumableServiceImpls() throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        CoreSession documentManager = documentModel.getCoreSession();
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM " + JavaServiceImplementation.DOCTYPE + " "
                + "WHERE " + JavaServiceImplementation.XPATH_IMPLEMENTEDINTERFACE + " = ?"
                + DocumentService.DELETED_DOCUMENTS_QUERY_FILTER,
                new Object[] { getConsumedInterface() },
                true, true);
        DocumentModelList consumableServiceImplModels = documentManager.query(query);
        return documentService.createSoaNodeIds(consumableServiceImplModels.toArray(new DocumentModel[]{}));
    }
    
}
