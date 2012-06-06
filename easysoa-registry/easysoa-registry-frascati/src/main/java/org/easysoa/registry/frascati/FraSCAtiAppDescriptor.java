package org.easysoa.registry.frascati;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("app")
public class FraSCAtiAppDescriptor {

    @XNode("@name")
	public String name;
    
    @XNode("jarPath")
	public String jarPath;
    
    @XNode("libsPath")
    public String libsPath;

    @XNode("compositeName")
	public String compositeName; 
	
    @Override
    public boolean equals(Object obj) {
    	return hashCode() == obj.hashCode();
    }
    
    @Override
    public int hashCode() {
    	return (name + jarPath + compositeName).hashCode();
    }
    
}
