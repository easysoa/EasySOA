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