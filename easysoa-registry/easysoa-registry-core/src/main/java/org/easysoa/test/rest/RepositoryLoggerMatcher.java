package org.easysoa.test.rest;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implement this as a document matcher for repository logging
 * @author mkalam-alami
 *
 */
public interface RepositoryLoggerMatcher {
    
    boolean matches(DocumentModel model);
    
}
