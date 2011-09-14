package org.easysoa.listeners;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.restlet.data.CharacterSet;

/**
 * Encapsulation for downloading a file from an HTTP url
 * @author mkalam-alami
 *
 */
public class HttpFile {
	
	private static final int DOWNLOAD_TIMEOUT = 15000;
	
	private String url;
	private File file = null;

	public HttpFile(String url) {
		this.url = url;
	}

	public HttpFile download() throws MalformedURLException, IOException,
			URISyntaxException {
		
	    HttpURLConnection connection = ((HttpURLConnection) new URI(this.url).toURL().openConnection());
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