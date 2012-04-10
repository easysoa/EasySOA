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

package org.easysoa.runtime.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.runtime.api.Deployable;
import org.easysoa.runtime.api.DeployableController;
import org.easysoa.runtime.api.DeployableProvider;
import org.easysoa.runtime.api.RuntimeControlService;
import org.easysoa.runtime.api.RuntimeServer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 * 
 */
@Name("runtimeManagement")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class RuntimeManagementBean {

	private static Log log = LogFactory.getLog(RuntimeManagementBean.class);

	@In(create = true, required = false)
	CoreSession documentManager;

	@In(create = true)
	NavigationContext navigationContext;

	RuntimeManagementService runtimeMgmtService = null;
	
	@Create
	public void init() throws Exception {
		if (documentManager == null) {
			documentManager = navigationContext.getOrCreateDocumentManager();
		}
	}

	public void deploy() throws Exception {
		// Init
		// TODO Caching?
		DocumentModel appliImplModel = navigationContext.getCurrentDocument();
		DeployableController deployableController = createDeployableController(appliImplModel);
		
		// Fetch deployables information
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> deployables = (List<Map<String, Object>>) 
				appliImplModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_DEPLOYABLES);
		
		// Deploy everything 
		// TODO Allow to deploy with dependencies recursively? 
		for (Map<String, Object> deployable : deployables) {
			// XXX Only supports String IDs
			String deployableId = (String) deployable.get(AppliImpl.SUBPROP_DEPLOYABLEID);
			if (deployableId != null) {
				Deployable<?> deployedDeployable = deployableController.deploy(deployableId);
				if (deployedDeployable == null) {
					log.warn("Failure while deploying ID " + deployable.get(AppliImpl.SUBPROP_DEPLOYABLEID));
				}
				else {
					log.info("Deployed " + deployable.get(AppliImpl.SUBPROP_DEPLOYABLEID) + " successfully");
				}
			}
			else {
				log.warn("Skipping deployable " + deployable.get(AppliImpl.SUBPROP_DEPLOYABLENAME) + 
						" while deploying app. " + appliImplModel.getTitle() + ": no ID found");
			}
		}
		
	}

	public void start() throws Exception {
		// Init
		DocumentModel appliImplModel = navigationContext.getCurrentDocument();
		DeployableController deployableController = createDeployableController(appliImplModel);
		
		// Start
		boolean success = deployableController.startServer();
		if (success) {
			log.info("Started server " + deployableController.getRuntime().getName() + " successfully");
		}
		else {
			log.warn("Failed to start server " + deployableController.getRuntime().getName());
		}
		
		// Refresh server displayed state
		refreshPage();
	}

	public void stop() throws Exception {
		// Init
		DocumentModel appliImplModel = navigationContext.getCurrentDocument();
		DeployableController deployableController = createDeployableController(appliImplModel);
		
		// Start
		boolean success = deployableController.stopServer();
		if (success) {
			log.info("Stopped server " + deployableController.getRuntime().getName() + " successfully");
		}
		else {
			log.warn("Failed to stop server " + deployableController.getRuntime().getName());
		}
		
		// Refresh server displayed state
		refreshPage();
	}

	public String getState() throws Exception {
		// Init
		DocumentModel appliImplModel = navigationContext.getCurrentDocument();
		DeployableController deployableController = createDeployableController(appliImplModel);
		
		RuntimeControlService controlService = deployableController.getRuntime().getControlService();
		if (controlService != null) {
			return controlService.getState().toString();
		}
		else {
			return "Unknown state";
		}
	}

	public String getRuntimeName() throws ClientException {
		return (String) navigationContext.getCurrentDocument().getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_RUNTIMESERVER);
	}

	public boolean isEnoughDeployableInformationProvided() throws ClientException {
		return isEnoughDeployableInformationProvided(navigationContext.getCurrentDocument());
	}
	
	public Set<String> getSelectItems(String type) throws Exception {
		Set<String> result = new HashSet<String>();
		result.add("");
		if ("deployableProvider".equals(type)) {
			result.addAll(getRuntimeManagementService().getAllDeployableProvidersNames());
		}
		else if ("runtimeServer".equals(type)) {
			result.addAll(getRuntimeManagementService().getAllRuntimeServersNames());
		}
		return result;
	}
	
	private boolean isEnoughDeployableInformationProvided(DocumentModel appliImplModel) throws ClientException {
		return appliImplModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_RUNTIMESERVER) != null
			&& appliImplModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_DEPLOYABLEPROVIDER) != null;
	}

	private void refreshPage() throws ClientException {
		navigationContext.navigateToRef(navigationContext.getCurrentDocument().getRef());
	}

	private DeployableController createDeployableController(DocumentModel appliImplModel) throws Exception {
		// Fetch information
		String runtimeServerName = (String) appliImplModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_RUNTIMESERVER);
		String deployableProviderName = (String) appliImplModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_DEPLOYABLEPROVIDER);
		
		if (runtimeServerName != null && deployableProviderName != null) { 
			RuntimeManagementService runtimeManagementService = getRuntimeManagementService();
			RuntimeServer<?, ?> runtimeServer = runtimeManagementService.getRuntimeServer(runtimeServerName);
			DeployableProvider<?> deployableProvider = runtimeManagementService.getDeployableProvider(deployableProviderName);
			
			if (runtimeServer != null && runtimeServer != null) {
				// Create deployable controller
				DeployableController deployableController = new DeployableController(runtimeServer);
				deployableController.addDeployableProvider(deployableProvider);
				return deployableController;
			}
			else  {
				log.warn("Invalid runtime/provider specified for app. " + appliImplModel.getTitle());
			}
		}
		else {
			log.warn("No runtime/provider specified for app. " + appliImplModel.getTitle());
		}

		return null;
	}
	
	private RuntimeManagementService getRuntimeManagementService() throws Exception {
		if (runtimeMgmtService == null) {
			runtimeMgmtService = Framework.getService(RuntimeManagementService.class);
		}
		return runtimeMgmtService;
	}
}
