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

package org.easysoa.app;

import java.io.InvalidClassException;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.registry.frascati.EasySOAApp;
import org.easysoa.registry.frascati.FraSCAtiApp;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.ComponentName;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * 
 * @author jguillemotte
 */
@Deprecated
public class AppComponent extends DefaultComponent {

    public static final ComponentName NAME = new ComponentName(ComponentName.DEFAULT_TYPE, "org.easysoa.core.service.AppComponent");

    private static Log log = LogFactory.getLog(AppComponent.class);

    private List<EasySOAApp> apps = new LinkedList<EasySOAApp>();

    public List<EasySOAApp> getApps() {
        return apps;
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) throws Exception {
        log.debug("registering contribution ..." + extensionPoint);
        try {
            synchronized (this.apps) {
                if (extensionPoint.equals("apps")) {
                	AppDescriptor appDescriptor = (AppDescriptor) contribution;
                    checkAndSetApp(appDescriptor, contributor);
                }
            }
        } catch (Exception ex) {
            log.error("registrerContribution error : " + ex.getMessage());
            throw new Exception(renderContributionError(contributor, ""), ex);
        }
    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        synchronized (this.apps) {
            if (extensionPoint.equals("apps")) {
            	AppDescriptor appDescriptor = (AppDescriptor) contribution;
                if (appDescriptor.enabled) {
                    try {
                        String appPath = appDescriptor.appPath.trim();
                        for (EasySOAApp currentApp : apps) {
                        	if (currentApp.getAppId().endsWith(appPath)) {
                                apps.remove(currentApp);
                            }
                        }
                    } catch (Exception ex) {
                        log.warn("Failed to unregister contribution: " + ex.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Check that the impl has the required constructor
     * 
     * @param scaImporterDescriptor
     * @param contributor
     * @throws Exception
     */
    public void checkAndSetApp(AppDescriptor appDescriptor, ComponentInstance contributor) throws Exception {
        if (appDescriptor.enabled) {
        	if(!appDescriptor.appPath.endsWith(".composite")){
        		FraSCAtiApp app = new FraSCAtiApp();
        		app.setAppPath(appDescriptor.appPath);
        		this.apps.add(app);
            } else {
                throw new InvalidClassException("EasySOAApp path '" + appDescriptor.appPath + "' is not a composite file");
            }
        }
    }

    /**
     * Creates a SCA Importer
     * 
     * @param bindingVisitorFactory The binding factory to use
     * @param compositeFile The composite file to parse/discover
     * @return A <code>IScaImporter</code>
     */
    /*public EasySOAApp createEasySOAApp(BindingVisitorFactory bindingVisitorFactory, File compositeFile) {
        try {
            log.debug("apps.size() = " + apps.size());
            Class<?> appClass = this.apps.get(apps.size() - 1);
            return (IScaImporter) appClass.getConstructor(new Class[] { BindingVisitorFactory.class, File.class })
                    .newInstance(new Object[] { bindingVisitorFactory, compositeFile });
        } catch (Exception ex) {
            log.error("An error occurs during the creation of EasySOAApp", ex);
            // TODO log "error creating sca import, bad config, see init logs"
        }
        return null;
    }*/

    /**
     * Render a contributor error
     * 
     * @param contributor a <code>ComponentInstance</code>
     * @param message The error message to add at the end of the contribution error
     * @return A contribution error message
     */
    private String renderContributionError(ComponentInstance contributor, String message) {
        return "Invalid contribution from '" + contributor.getName() + "': " + message;
    }

}
