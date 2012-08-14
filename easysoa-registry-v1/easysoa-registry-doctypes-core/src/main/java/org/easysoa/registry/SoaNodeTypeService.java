package org.easysoa.registry;

import java.util.Collection;
import java.util.List;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface SoaNodeTypeService {

    Collection<String> getChildren(String type);
    
    List<String> getPath(String fromType, String toType);
    
}