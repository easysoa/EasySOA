package org.easysoa.registry.indicators.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easysoa.registry.DocumentService;
import org.easysoa.registry.types.Service;
import org.easysoa.registry.types.TaggingFolder;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

public class TagsIndicatorProvider implements IndicatorProvider {

    @Override
    public List<String> getRequiredIndicators() {
        return null;
    }

    @Override
    public Map<String, IndicatorValue> computeIndicators(CoreSession session,
            Map<String, IndicatorValue> computedIndicators) throws Exception {
        DocumentService documentService = Framework.getService(DocumentService.class);
        UserManager userManager = Framework.getService(UserManager.class);
        
        // Count users
        int userCount = userManager.getUserIds().size();
        
        // Count services without tagging folders
        int notTaggedServices = 0;
        DocumentModelList serviceModels = session.query(NXQL_SELECT_FROM + Service.DOCTYPE + NXQL_WHERE_NO_PROXY);
        for (DocumentModel serviceModel : serviceModels) {
            DocumentModelList serviceParents = documentService.findAllParents(session, serviceModel);
            notTaggedServices++;
            for (DocumentModel serviceParent : serviceParents) {
                if (TaggingFolder.DOCTYPE.equals(serviceParent.getType())) {
                    notTaggedServices--;
                    break;
                }
            }
        }
        
        // Count tagging folders
        DoctypeCountIndicator taggingFolderCount = new DoctypeCountIndicator(TaggingFolder.DOCTYPE);
        IndicatorValue taggingFolderCountValue = taggingFolderCount.compute(session, computedIndicators);
        
        // Register indicators
        Map<String, IndicatorValue> indicators = new HashMap<String, IndicatorValue>();
        indicators.put("Services without at least one user tag",
                new IndicatorValue(notTaggedServices, 100 * notTaggedServices / serviceModels.size()));
        indicators.put("Average tag count per user",
                new IndicatorValue(taggingFolderCountValue.getCount() / userCount, -1));
        
        return indicators;
    }

}
