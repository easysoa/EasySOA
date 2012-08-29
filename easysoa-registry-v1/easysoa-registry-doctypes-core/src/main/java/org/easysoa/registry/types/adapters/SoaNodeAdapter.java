package org.easysoa.registry.types.adapters;

import org.easysoa.registry.InvalidDoctypeException;
import org.easysoa.registry.SoaNodeId;
import org.easysoa.registry.types.SoaNode;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;

/**
 * 
 * @author mkalam-alami
 * 
 */
public abstract class SoaNodeAdapter extends AbstractDocumentAdapter implements SoaNode {

    protected SoaNodeId soaNodeId;

    public SoaNodeAdapter(DocumentModel documentModel) throws InvalidDoctypeException,
            PropertyException, ClientException {
        super(documentModel);
        this.soaNodeId = new SoaNodeId(documentModel.getType(),
                (String) documentModel.getPropertyValue(SoaNode.XPATH_SOANAME));
    }

    public SoaNodeId getSoaNodeId() {
        return this.soaNodeId;
    }

    public String getSoaName() {
        return this.soaNodeId.getName();
    }
}
