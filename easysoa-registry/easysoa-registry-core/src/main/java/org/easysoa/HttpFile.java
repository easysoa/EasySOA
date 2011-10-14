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
 * Contact : easysoa-dev@groups.google.com
 */

package org.easysoa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.restlet.data.CharacterSet;

/**
 * Basic file downloading tool, given an HTTP url.
 * @author mkalam-alami
 *
 */
public class HttpFile {
    
    private static final int DOWNLOAD_TIMEOUT = 15000;
    
    private URL url;
    private File file = null;

    public HttpFile(URL url) {
        this.url = url;
    }

    public boolean isURLAvailable() {
        try {
            new Socket().connect(new InetSocketAddress(
                    url.getHost(), (url.getPort() != -1) ? url.getPort() : 80));
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }
    
    public HttpFile download() throws MalformedURLException, IOException, URISyntaxException {
        HttpURLConnection connection = ((HttpURLConnection) this.url.openConnection());
        this.file = File.createTempFile("tmp", "tmp");
        connection.setReadTimeout(DOWNLOAD_TIMEOUT);
        InputStream is = connection.getInputStream();
        FileOutputStream fos = new FileOutputStream(this.file);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos,
                CharacterSet.UTF_8.getName()));
        int c;
        try {
            while ((c = is.read()) != -1) {
                bw.write((char) c);
            }
            bw.flush();
        }
        finally {
            bw.close();
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