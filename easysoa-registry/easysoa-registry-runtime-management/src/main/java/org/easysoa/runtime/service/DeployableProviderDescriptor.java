package org.easysoa.runtime.service;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * 
 * @author mkalam-alami
 *
 */
@XObject("deployableProvider")
public class DeployableProviderDescriptor {

	/*
	 Example:
	 <deployableProvider>
	   <name>Global Maven Repository</name>
	   <type>mavenRepository</type>
	   <properties>
	     <property name="url">http://search.maven.org/remotecontent?filepath=</property>
	   </properties>
	 </deployableProvider>
	 */
	
	@XNode("name")
	protected String name;

	@XNode("type")
	protected String type;

    @XNodeMap(value = "properties/property", key = "@name", type = HashMap.class, componentType = String.class)
    protected Map<String, String> properties;
	
}
