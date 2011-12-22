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

package org.easysoa.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;

/**
 * Basic file downloading tool, given an HTTP(S) url.
 * @author mkalam-alami
 *
 */
public class HttpToFile {

    private HttpClient client = new HttpClient();
    private URL url;
    private File file = null;

    public HttpToFile(String url) throws MalformedURLException {
        this.url = new URL(url);
    }
    
    public HttpToFile(URL url) {
        this.url = url;
    }

    public boolean isURLAvailable() {
    	try {
        	GetMethod getMethod = new GetMethod(url.toString());
        	return client.executeMethod(getMethod) == 200;
    	}
    	catch (Exception e) {
    		return false;
    	}
    }
    
    public HttpToFile download() throws MalformedURLException, IOException, URISyntaxException {

        this.file = File.createTempFile("tmp", "tmp");
        FileOutputStream fos = new FileOutputStream(this.file);
    
        try {
	    	GetMethod getMethod = new GetMethod(url.toString());
	    	int responseCode = client.executeMethod(getMethod);
	    	if (responseCode == 200) {
	    		byte[] body = getMethod.getResponseBody();
	    		fos.write(body);
	    		fos.flush();
	    	}
        }
        finally {
        	fos.close();
        }
	
        return this;
    }

    public void delete() {
        if (isDownloaded()) {
            this.file.delete();
        }
    }

    public File getFile() {
        return this.file;
    }

    public Blob getBlob() {
        return isDownloaded() ? new FileBlob(this.file) : null;
    }

    public boolean isDownloaded() {
        return (this.file != null) && (this.file.exists());
    }
    
}