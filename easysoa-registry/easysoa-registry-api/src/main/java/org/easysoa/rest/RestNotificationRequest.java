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

package org.easysoa.rest;

import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface RestNotificationRequest {

    /**
     * Sets a form parameter to be sent with the request.
     * @param property
     * @param value
     * @return
     */
    RestNotificationRequest setProperty(String property, String value);

    /**
     * Sets several form parameters to be sent with the request.
     * @param property
     * @param value
     * @return
     */
    RestNotificationRequest setProperties(Map<String, String> entries);
    
    /**
     * Sends the notification.
     * @throws IOException When the request failed.
     * @throws Exception When the request returned an error.
     * @return a JSONObject containing the response 
     * @throws Exception 
     */
    JSONObject send() throws Exception;
    
}