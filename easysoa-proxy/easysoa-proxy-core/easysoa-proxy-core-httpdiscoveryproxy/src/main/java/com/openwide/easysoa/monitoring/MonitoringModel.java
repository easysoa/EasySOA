/**
 * EasySOA Proxy
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

package com.openwide.easysoa.monitoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.openwide.easysoa.monitoring.soa.Api;
import com.openwide.easysoa.monitoring.soa.Appli;
import com.openwide.easysoa.monitoring.soa.Node;
import com.openwide.easysoa.monitoring.soa.Service;
import com.openwide.easysoa.nuxeo.registration.NuxeoRegistrationService;

public class MonitoringModel {

	public static final String SERVICE_TYPE = "Service";
	public static final String API_TYPE = "Api";
	public static final String APPLI_TYPE = "Appli";
	
	private HashMap<String, String> soaModelUrlToTypeMap; // scope : global
	private List<Node> soaNodes;
	
	/**
	 * Logger
	 */
	private Logger logger = Logger.getLogger(MonitoringModel.class.getName());	
	
	/**
	 * Constructor
	 */
	public MonitoringModel() {
		init();
	}
	
	/**
	 * Initialize with empty objects
	 */
	public void init() {
		soaModelUrlToTypeMap = new HashMap<String, String>();
		soaNodes = new ArrayList<Node>();		
	}

	/**
	 * Fill the object with data from Nuxeo
	 */
	public void fetchFromNuxeo() {
		try {
            soaNodes = new NuxeoRegistrationService().getAllSoaNodes();
            logger.debug("soaNodes size : " + soaNodes.size());
            for (Node soaNode : soaNodes) {
                soaModelUrlToTypeMap.put(soaNode.getUrl(), getNodeType(soaNode));
            }
        } catch (IOException e) {
            logger.error("Failed to fetch data from Nuxeo", e);
        }
	}
	
	/**
	 * @param soaNode
	 * @return The type as a <code>String</code> of the soaNode. If the type is unknown returns null
	 */
	private String getNodeType(Node soaNode) {
		if (soaNode instanceof Service) {
			return SERVICE_TYPE;
		}
		else if (soaNode instanceof Api) {
			return API_TYPE;
		}
		else if (soaNode instanceof Appli) {
			return APPLI_TYPE;
		}
		else {
			return null;
		}
	}

	/**
	 * @return the soaModelUrlToTypeMap
	 */
	public HashMap<String, String> getSoaModelUrlToTypeMap() {
		return soaModelUrlToTypeMap;
	}
	/**
	 * @return the soaNodes
	 */
	public List<Node> getSoaNodes() {
		return soaNodes;
	}

}
