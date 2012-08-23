package org.easysoa.registry;

import java.util.Collection;
import java.util.List;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface SoaMetamodelService {

    Collection<String> getChildren(String type);
    
    /**
     * Computes the minimal path needed to link two document types.
     * The returned list is empty if <i>toType</i> can be directly stored under <i>fromType</i>.
     * 
     * @param fromType The parent type
     * @param toType The child type
     * @return The path (without <i>fromType</i>, or null if it's not possible
     * to store <i>toType</i> under <i>fromType</i>)
     */
    List<String> getPath(String fromType, String toType);
    
}