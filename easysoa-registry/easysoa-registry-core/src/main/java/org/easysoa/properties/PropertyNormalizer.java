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

package org.easysoa.properties;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.EasySOADoctype;

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
        return concatUrlPath(stringUrl.trim().split("/")); // if URL OK, remove the end '/' if any
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
        return concatUrlPath(stringUrl.trim().split("/"));
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