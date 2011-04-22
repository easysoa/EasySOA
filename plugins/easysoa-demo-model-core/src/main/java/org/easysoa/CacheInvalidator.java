package org.easysoa;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;

import org.jboss.logging.Logger;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webapp.helpers.EventNames;

/**
 * Handles cache and clears it for relations to refresh.
 * @author mkalam-alami
 *
 */
@Name("cacheInvalidator")
@Scope(CONVERSATION)
public class CacheInvalidator implements Serializable {

	private static final Logger log = Logger.getLogger(CacheInvalidator.class);
    private static final long serialVersionUID = 1L;

    @In(create = true, required = true)
    protected transient CoreSession documentManager;

    /**
     * {@link EventNames#DOCUMENT_CHILDREN_CHANGED}
     * {@link EventNames#DOCUMENT_CHANGED}
     */
    @Observer(value = { EventNames.DOCUMENT_CHILDREN_CHANGED,
            EventNames.DOCUMENT_CHANGED })
    public void invalidateCache() {
    	log.info("je suis là ù!!!!");
    }

	
}
