package org.easysoa.rest.servicefinder;

import org.nuxeo.common.xmap.annotation.XContent;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * 
 * @author mkalam-alami
 *
 */
@XObject("serviceFinder")
public class ServiceFinderDescriptor {

    @XContent
    protected String implementation;

    @XNode("@enabled")
    protected boolean enabled = true;

}
