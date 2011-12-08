package org.easysoa.validation;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * 
 * @author mkalam-alami
 *
 */
@XObject("validator")
public class ServiceValidatorDescriptor {

    @XNode("@enabled")
    protected boolean enabled = true;

    @XNode("@name")
    protected String name;
    
    @XNode("@label")
    protected String label;
    
    @XNode("@class")
    protected String className;
    
}
