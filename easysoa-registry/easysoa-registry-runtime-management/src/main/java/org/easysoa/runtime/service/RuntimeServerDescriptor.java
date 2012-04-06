package org.easysoa.runtime.service;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;


@XObject("runtimeServer")
public class RuntimeServerDescriptor {

	/*
	 Example:
	 <runtimeServer>
	   <name>Test Server</name>
	   <type>copyPasteServer</type>
	   <properties>
	     <property name="path">./nxserver/nuxeo.war/testServer</property>
	   </properties>
	 </runtimeServer>
	 */
	
	@XNode("name")
	protected String name;

	@XNode("type")
	protected String type;

    @XNodeMap(value = "properties/property", key = "@name", type = HashMap.class, componentType = String.class)
    protected Map<String, String> properties;
	
}
