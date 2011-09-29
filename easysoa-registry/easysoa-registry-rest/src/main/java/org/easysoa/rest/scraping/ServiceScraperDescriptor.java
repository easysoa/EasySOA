package org.easysoa.rest.scraping;

import org.nuxeo.common.xmap.annotation.XContent;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * 
 * @author mkalam-alami
 *
 */
@XObject("serviceScraper")
public class ServiceScraperDescriptor {

    @XContent
    protected String implementation;

    @XNode("@enabled")
    protected boolean enabled = true;

}
