package org.easysoa.registry.types.adapters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.utils.ListUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;

/**
 * 
 * @author mkalam-alami
 * 
 */
public class SoaNodeAdapter extends AbstractDocumentAdapter implements SoaNode {

    protected SoaNodeId soaNodeId;

    public SoaNodeAdapter(DocumentModel documentModel) throws InvalidDoctypeException,
            PropertyException, ClientException {
        super(documentModel);
        this.soaNodeId = new SoaNodeId(documentModel.getType(),
                (String) documentModel.getPropertyValue(SoaNode.XPATH_SOANAME));
    }
    
	public String getDoctype() {
		return documentModel.getType();
	}

    public SoaNodeId getSoaNodeId() {
        return this.soaNodeId;
    }

    public String getSoaName() {
        return this.soaNodeId.getName();
    }
    
    public boolean isPlaceholder() throws Exception {
        return (Boolean) documentModel.getPropertyValue(XPATH_ISPLACEHOLDER);
    }
    
    public void setIsPlaceholder(boolean isPlaceholder) throws Exception {
        documentModel.setPropertyValue(XPATH_ISPLACEHOLDER, isPlaceholder);
    }
    
    public List<SoaNodeId> getParentIds() throws Exception {
		Serializable[] parentsIdsArray = (Serializable[]) documentModel.getPropertyValue(XPATH_PARENTSIDS);
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

	public void setParentIds(List<SoaNodeId> parentIds) throws PropertyException, ClientException {
		List<String> parentsIdsStringList = new ArrayList<String>();
		for (SoaNodeId parentId : parentIds) {
			parentsIdsStringList.add(parentId.toString());
		}
		documentModel.setPropertyValue(XPATH_PARENTSIDS, (Serializable) parentsIdsStringList);
	}
    
}
