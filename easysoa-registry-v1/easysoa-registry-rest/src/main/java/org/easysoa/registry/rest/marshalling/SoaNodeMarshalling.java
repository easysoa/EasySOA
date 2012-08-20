package org.easysoa.registry.rest.marshalling;

import java.util.List;

public interface SoaNodeMarshalling {

    String marshall(SoaNodeInformation soaNodeInfo);

    String marshall(List<SoaNodeInformation> soaNodeListInfo);

    String marshallError(String message, Exception e);
    
    SoaNodeInformation unmarshall(String string);
    
}
