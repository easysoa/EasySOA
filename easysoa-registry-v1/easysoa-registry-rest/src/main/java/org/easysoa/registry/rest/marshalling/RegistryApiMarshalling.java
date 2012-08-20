package org.easysoa.registry.rest.marshalling;

import java.util.List;

public interface RegistryApiMarshalling {

    String marshall(SoaNodeInformation soaNodeInfo);

    String marshall(List<SoaNodeInformation> soaNodeListInfo);

    String marshallSuccess();
    
    String marshallError(String message, Exception e);
    
    SoaNodeInformation unmarshall(String data);
    
    List<String> unmarshallPathList(String data);
    
}
