package org.easysoa.runtime.service;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.runtime.api.DeployableProvider;
import org.easysoa.runtime.api.RuntimeServer;
import org.easysoa.runtime.copypaste.CopyPasteServer;
import org.easysoa.runtime.maven.MavenRepository;
import org.easysoa.runtime.utils.FakeStartableCopyPasteServer;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;


/**
 * 
 * @author mkalam-alami
 *
 */
public class RuntimeManagementService extends DefaultComponent {
    
	private static final String DEPLOYABLE_PROVIDERS_EXTENSIONPOINT = "deployableProviders";
	
	private static final String RUNTIME_SERVERS_EXTENSIONPOINT = "runtimeServers";

    private static final Log log = LogFactory.getLog(RuntimeManagementService.class);
    
	private Map<String, DeployableProvider<?>> deployableProviders = new HashMap<String, DeployableProvider<?>>();
	
	private Map<String, RuntimeServer<?, ?>> runtimeServers = new HashMap<String, RuntimeServer<?, ?>>();

	@Override
	public void registerContribution(Object contribution,
			String extensionPoint, ComponentInstance contributor)
			throws Exception {
		
		if (DEPLOYABLE_PROVIDERS_EXTENSIONPOINT.equals(extensionPoint)) {
			DeployableProviderDescriptor deployableProviderDescriptor = (DeployableProviderDescriptor) contribution;
			DeployableProvider<?> newProvider = null;
			
			// TODO Move to extension point "deployableProviderTypes"
			if ("mavenRepository".equals(deployableProviderDescriptor.type)) {
				URL mavenRepositoryUrl = new URL(deployableProviderDescriptor.properties.get("url"));
				newProvider = new MavenRepository(mavenRepositoryUrl);
			}
			else {
				log.error("Unknown type '" + deployableProviderDescriptor.type + "'");
			}
			
			if (newProvider != null) {
				deployableProviders.put(deployableProviderDescriptor.name, newProvider);
			}
		}
		
		else if (RUNTIME_SERVERS_EXTENSIONPOINT.equals(extensionPoint)) {
			RuntimeServerDescriptor runtimeServerDescriptor = (RuntimeServerDescriptor) contribution;
			RuntimeServer<?, ?> newRuntimeServer = null;

			// TODO Move to extension point "runtimeServerTypes"
			if ("copyPasteServer".equals(runtimeServerDescriptor.type)) {
				newRuntimeServer = new CopyPasteServer(runtimeServerDescriptor.properties.get("path"));
			}
			else if ("fakeStartableCopyPasteServer".equals(runtimeServerDescriptor.type)) {
				newRuntimeServer = new FakeStartableCopyPasteServer(runtimeServerDescriptor.properties.get("path"));
			}
			else {
				log.error("Unknown type '" + runtimeServerDescriptor.type + "'");
			}
			
			if (newRuntimeServer != null) {
				runtimeServers.put(runtimeServerDescriptor.name, newRuntimeServer);
			}
		}
	}
	
	public Set<String> getAllDeployableProvidersNames() {
		return deployableProviders.keySet();
	}
	
	public DeployableProvider<?> getDeployableProvider(String name) {
		return deployableProviders.get(name);
	}
	
	public Set<String> getAllRuntimeServersNames() {
		return runtimeServers.keySet();
	}

	public RuntimeServer<?, ?> getRuntimeServer(String name) {
		return runtimeServers.get(name);
	}
	
}
