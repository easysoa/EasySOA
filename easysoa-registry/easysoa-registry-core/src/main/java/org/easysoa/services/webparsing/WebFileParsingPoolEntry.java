package org.easysoa.services.webparsing;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author mkalam-alami
 * 
 */
public class WebFileParsingPoolEntry {

    private URL url;
    private DocumentModel targetModel;
    private String storageProp;
    private Map<String, String> options;
    
    public WebFileParsingPoolEntry(URL url, DocumentModel targetModel,
            String storageProp, Map<String, String> options) {
        this.url = url;
        this.targetModel = targetModel;
        this.storageProp = storageProp;
        this.options = options;
        
        if (options == null) {
            options = new HashMap<String, String>();
        }
    }

    public URL getUrl() {
        return url;
    }

    public DocumentModel getTargetModel() {
        return targetModel;
    }

    public String getStorageProp() {
        return storageProp;
    }

    public Map<String, String> getOptions() {
        return options;
    }

}