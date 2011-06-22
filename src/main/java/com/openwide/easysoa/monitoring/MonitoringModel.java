package com.openwide.easysoa.monitoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.openwide.easysoa.esperpoc.NuxeoRegistrationService;
import com.openwide.easysoa.monitoring.soa.Api;
import com.openwide.easysoa.monitoring.soa.Appli;
import com.openwide.easysoa.monitoring.soa.Node;
import com.openwide.easysoa.monitoring.soa.Service;

public class MonitoringModel {

	public static final String SERVICE_TYPE = "Service";
	public static final String API_TYPE = "Api";
	public static final String APPLI_TYPE = "Appli";
	
	private HashMap<String, String> soaModelUrlToTypeMap; // scope : global
	private List<Node> soaNodes;
	
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
		soaNodes = new NuxeoRegistrationService().getAllSoaNodes();
		for (Node soaNode : soaNodes) {
			soaModelUrlToTypeMap.put(soaNode.getUrl(), getNodeType(soaNode));
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
