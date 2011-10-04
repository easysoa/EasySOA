package org.easysoa.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

    public boolean isFileAvailable() {
        boolean result = true;
        HttpURLConnection connection = null;
        try {
            connection = ((HttpURLConnection) this.url.openConnection());
        } catch (IOException e) {
            result = false;
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
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