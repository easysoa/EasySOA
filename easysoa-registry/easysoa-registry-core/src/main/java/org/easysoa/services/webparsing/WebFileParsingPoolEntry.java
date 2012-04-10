/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

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
        
        if (this.options == null) {
            this.options = new HashMap<String, String>();
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