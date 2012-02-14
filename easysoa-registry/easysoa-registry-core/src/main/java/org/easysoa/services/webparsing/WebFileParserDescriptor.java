package org.easysoa.services.webparsing;

import org.nuxeo.common.xmap.annotation.XContent;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * A web file parser descriptor.
 * All implementations should implement the {@link WebFileParser} interface.
 * 
 * @author mkalam-alami
 *
 */
@XObject("parser")
public class WebFileParserDescriptor {
    
    @XContent
    protected String implementation;

    @XNode("@enabled")
    protected boolean enabled = true;
    
}
