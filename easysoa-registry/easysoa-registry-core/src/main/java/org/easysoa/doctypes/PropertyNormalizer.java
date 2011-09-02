package org.easysoa.doctypes;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Tools to allow for an uniform format of EasySOA specific properties.
 * @author mdutoo, mkalam-alami
 *
 */
public class PropertyNormalizer extends EasySOADoctype {

	private static final String DEFAULT_NORMALIZE_ERROR = "Failed to normalize property";

    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(PropertyNormalizer.class);
    
	/**
	 * Normalizes the given URL :
	 * Ensures that all pathElements are separated by a single slash,
	 * and checks that the URL is parsable and contain the protocol part.
	 * @param stringUrl
	 * @param errMsg
	 * @return The normalized URL
     * @throws MalformedURLException
	 */

    public static final String normalizeUrl(String stringUrl, String errMsg) throws MalformedURLException {
        if (stringUrl == null) {
            throw new MalformedURLException(errMsg + ": null");
        }
        if (stringUrl.indexOf("://") != -1) {
            URL url = new URL(stringUrl);
            stringUrl = url.toString();
            return normalizeUrlPath(stringUrl, errMsg);
        }
        return concatUrlPath(stringUrl.split("/")); // if URL OK, remove the end '/' if any
    }

    /**
     * Normalizes the given URL :
     * Ensures that all pathElements are separated by a single slash,
     * and checks that the URL is parsable and contain the protocol part.
     * @param stringUrl
     * @return The normalized URL
     * @throws MalformedURLException
     */
    public static final String normalizeUrl(String stringUrl) throws MalformedURLException {
        return normalizeUrl(stringUrl, DEFAULT_NORMALIZE_ERROR);
    }

	/**
	 * Normalizes the given URL path : ensures all pathElements are separated by a single /
	 * @param stringUrl
	 * @param errMsg
	 * @return
	 * @throws MalformedURLException
	 */
	public static final String normalizeUrlPath(String stringUrl, String errMsg) throws MalformedURLException {
		if (stringUrl == null) {
			throw new MalformedURLException(errMsg + ": null");
		}
		if (stringUrl.contains("localhost")) {
			stringUrl = stringUrl.replaceFirst("localhost", "127.0.0.1");
		}
		return concatUrlPath(stringUrl.split("/"));
	}

	public static final String normalizeUrlPath(String stringUrl) throws MalformedURLException {
		return normalizeUrlPath(stringUrl, DEFAULT_NORMALIZE_ERROR);
	}

	/**
	 * NB. no normalization done
	 * @param url
	 * @param urlPath
	 * @return
	 */
	public static final String concatUrlPath(String... urlPath) {
		StringBuffer sbuf = new StringBuffer();
		for (String urlPathElement : urlPath) {
			sbuf.append(urlPathElement);
			sbuf.append('/');
		}
		if (sbuf.length() != 0) {
			sbuf.deleteCharAt(sbuf.length() - 1);
		}
		return sbuf.toString();
	}


}