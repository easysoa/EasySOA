package org.easysoa.registry;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * 
 * @author mkalam-alami
 *
 */
@XObject("inheritedFacet")
public class InheritedFacetDescriptor {

    @XNode("@name")
    public String name;

}