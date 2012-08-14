package org.easysoa.registry;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * 
 * @author mkalam-alami
 *
 */
@XObject("soaNodeType")
public class SoaNodeTypeDescriptor {

    @XNode("@name")
    public String name;
    
    @XNodeList(value = "subtype", type = ArrayList.class, componentType=String.class, trim = true)
    public List<String> subtypes;

    
}