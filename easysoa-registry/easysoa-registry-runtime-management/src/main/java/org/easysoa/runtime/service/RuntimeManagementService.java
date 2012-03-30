package org.easysoa.runtime.service;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.easysoa.runtime.api.DeployableProvider;
import org.easysoa.runtime.maven.MavenRepository;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;


/**
 * 
 * @author mkalam-alami
 *
 */
public class RuntimeManagementService extends DefaultComponent {

	Map<String, DeployableProvider<?>> deployableProviders = new HashMap<String, DeployableProvider<?>>();
	
	// TODO Transform into extension point(s)
	@Override
	public void activate(ComponentContext context) throws Exception {
		
		/*
		 <deployableProvider>
		   <type>mavenRepository</type>
		   <params>
		     <url>http://search.maven.org/remotecontent?filepath=</url>
		   </params>
		 </deployableProvider>
		 */
		URL mavenRepositoryUrl = new URL("http://search.maven.org/remotecontent?filepath=");
		DeployableProvider<?> mavenRepository = new MavenRepository(mavenRepositoryUrl);
		deployableProviders.put("Maven Repository", mavenRepository);
		
	}
	
	public Set<String> geAllDeployableProvidersNames() {
		return deployableProviders.keySet();
	}
	
	public DeployableProvider<?> getDeployableProvider(String name) {
		return deployableProviders.get(name);
	}
	
}
