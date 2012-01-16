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

package org.easysoa.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.ByteArrayBlob;

/**
 * Basic file downloading tool, given an HTTP(S) url.
 * @author mkalam-alami
 *
 */
public class HttpDownloaderImpl implements HttpDownloader {

    private HttpClient client = new HttpClient();
    private UsernamePasswordCredentials credentials = null;
    private URL url;
    private File file = null;
    private byte[] bytes = null;

    public HttpDownloaderImpl(String url) throws MalformedURLException {
        this.url = new URL(url);
    }
    
    public HttpDownloaderImpl(URL url) {
        this.url = url;
    }
    
    /**
     * XXX Untested
     */
    public HttpDownloader setHttpsCredentials(String username, String password) {
        credentials = new UsernamePasswordCredentials(username, password);
        return this;
    }

    @Override
	public boolean isURLAvailable() {
        GetMethod getMethod = null;
        try {
        	getMethod = new GetMethod(url.toString());
        	return client.executeMethod(getMethod) == 200;
    	}
    	catch (Exception e) {
    		return false;
    	}
    	finally {
    	    if (getMethod != null) {
    	        getMethod.releaseConnection();
    	    }
    	}
    }
    
	@Override
	public HttpDownloader download() throws Exception {
    	GetMethod getMethod = new GetMethod(url.toString());
    	try {
    	    if (credentials != null) {
                client.getState().setProxyCredentials(
                        new AuthScope(url.getHost(), url.getPort()),
                        credentials);
    	    }
        	int responseCode = client.executeMethod(getMethod);
        	if (responseCode == 200) {
        		this.bytes = getMethod.getResponseBody();
        	}
    	}
    	finally {
    	    getMethod.releaseConnection();
    	}
        return this;
    }
	
	@Override
	public boolean isDownloaded() {
        return this.bytes != null;
    }
	
    @Override
	public void delete() {
        if (this.file != null) {
            this.file.delete();
        }
        this.bytes = null;
    }

	@Override
	public byte[] getBytes() {
		return this.bytes;
	}

	@Override
	public Blob getBlob() {
        return this.bytes != null ? new ByteArrayBlob(this.bytes) : null;
    }

	@Override
	public File getFile() throws IOException {
		if (this.file == null && this.bytes != null) {
			FileOutputStream fos = null;
			try {
				// Export bytes to file
		        this.file = File.createTempFile("tmp", "tmp");
		        fos = new FileOutputStream(this.file);
				fos.write(this.bytes);
				fos.flush();
			}
			catch (Exception e) {
				// On failure, delete the file
				if (this.file != null) {
					this.file.delete();
				}
				this.file = null;
			}
	        finally {
	        	if (fos != null) {
	        		fos.close();
	        	}
	        }
		}
        return this.file;
    }
    
}