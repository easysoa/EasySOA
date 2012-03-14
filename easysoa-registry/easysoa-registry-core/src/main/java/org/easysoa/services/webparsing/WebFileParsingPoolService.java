package org.easysoa.services.webparsing;

import java.net.URL;
import java.util.Map;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 * @author mkalam-alami
 *
 */
public interface WebFileParsingPoolService {

    /**
     * Adds the given URL to an asynchronous download & parsing pool.
     *
     * @param url The URL to download. Must not be null.
     * @param targetModel The target model on which to store extracted information. If null, will be guessed.
     * @param storageProp A property on which to store the downloaded blob (ex: file:content). If null, the blob won't be saved.
     * @param options Any options to pass to the file parsers.
     */
    void append(URL url, DocumentModel targetModel, String storageProp, Map<String, String> options) throws IllegalArgumentException;

}