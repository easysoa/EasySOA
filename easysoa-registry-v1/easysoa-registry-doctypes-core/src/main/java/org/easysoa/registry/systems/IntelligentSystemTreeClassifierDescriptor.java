package org.easysoa.registry.systems;

import org.nuxeo.common.xmap.annotation.XContent;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("classifier")
public class IntelligentSystemTreeClassifierDescriptor {

    @XNode("@name")
    protected String name;
    
    @XContent
    protected String className;
    
}
