package org.easysoa.registry.types.java.utils;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.java.JavaServiceImplementation;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;


/**
 * 
 * @author mkalam-alami
 *
 */
public class JavaDoctypesHelper {
    
    public static DocumentModelList getMatchingServiceImpls(CoreSession documentManager, String serviceImplInterface) throws ClientException {
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM " + JavaServiceImplementation.DOCTYPE + " "
        		+ "WHERE " + JavaServiceImplementation.XPATH_IMPLEMENTEDINTERFACE + " = ?"
                + DocumentService.DELETED_DOCUMENTS_QUERY_FILTER,
                new Object[] { serviceImplInterface },
                true, true);
        return documentManager.query(query);
    }
    
}
