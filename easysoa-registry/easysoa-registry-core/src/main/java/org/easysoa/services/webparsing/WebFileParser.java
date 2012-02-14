package org.easysoa.services.webparsing;

import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface WebFileParser {
    
    /**
     * Parse the given blob to store information in the given model.
     * 
     * @param session A valid core session.
     * @param data The file data to parse.
     * @param model A model on which to store information. Can be null: in that case, the parser
     * can guess which document model(s) to edit by using the extracted information. Any other document
     * than the one passed in parameter must be saved manually by the parser. 
     * @param options Any options given by the parsing pool consumer.
     */
    void parse(CoreSession session, Blob data, DocumentModel model, Map<String, String> options);

}
