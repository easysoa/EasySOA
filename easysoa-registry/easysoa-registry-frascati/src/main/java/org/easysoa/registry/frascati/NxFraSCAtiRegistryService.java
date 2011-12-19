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

/**
 * EasySOA: OW2 FraSCAti in Nuxeo
 * Copyright (C) 2011 INRIA, University of Lille 1
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * Contact: frascati@ow2.org
 *
 * Author: Philippe Merle
 *
 * Contributor(s):
 *
 */

package org.easysoa.registry.frascati;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.app.AppComponent;
import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.frascati.FraSCAtiScaImporter;
import org.easysoa.sca.visitors.LocalBindingVisitorFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.frascati.api.FraSCAtiCompositeItf;
import org.nuxeo.frascati.api.FraSCAtiServiceItf;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.bridge.Application;
import org.nuxeo.runtime.model.Adaptable;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.ComponentName;
import org.nuxeo.runtime.model.Extension;

/**
 * 
 * @author pmerle, mdutoo
 * 
 *         TODO solve maven deps : <groupId>org.eclipse.jdt</groupId>
 *         <artifactId>core</artifactId> <version>3.3.0.771</version> <!-- TODO
 *         Eclipse m2e says : Overriding managed version 3.1.1-NXP-4284 for core
 *         ?!! -->
 * 
 */
public class NxFraSCAtiRegistryService extends FraSCAtiRegistryServiceBase implements org.nuxeo.runtime.model.Component, Adaptable {
	
	// Service component
	public static final ComponentName NAME = new ComponentName("org.easysoa.registry.frascati.FraSCAtiServiceComponent");

	// Logger
    private static Log log = LogFactory.getLog(NxFraSCAtiRegistryService.class);
	
	// Nuxeo Core session
	private CoreSession documentManager;
	
	// List of Easy SOA Apps
	private List<EasySOAApp> apps;
	
	/**
	 * 
	 */
	public NxFraSCAtiRegistryService()  {
		super();
	}
	
	/*public void setApps(List<EasySOAApp> apps){
		this.apps = apps;
	}*/
	
	/**
	 * TODO LATER Move in FraSCAtiAppManager, use EasySOAApp as parameter, remember them to allow to list & stop them
	 * TODO LATER possibly make it async, wrap CoreSession in an EasySOAIdentity which will have other things (ex. user/pass, jaas...) on client side
	 * @param scaAppUrl
	 * @param documentManager
	 * @return
	 * @throws Exception
	 */
	public FraSCAtiCompositeItf[] startScaApp(URL scaAppUrl, CoreSession documentManager) throws Exception {
		// TODO : change the processing context to discovery processing context
		//ParsingProcessingContext processingContext = this.newParsingProcessingContext(scaAppUrl);
		this.setDocumentManager(documentManager);
		DiscoveryProcessingContext processingContext = this.newDiscoveryProcessingContext(scaAppUrl);
		return this.frascati.processContribution(scaAppUrl.toString(), processingContext);
	}
	
	/**
	 * 
	 * @param documentManager
	 * @return
	 * @throws Exception
	 */
	public FraSCAtiRuntimeScaImporterItf newLocalRuntimeScaImporter(CoreSession documentManager) throws Exception {
		LocalBindingVisitorFactory nxBindingVisitorFactory = new LocalBindingVisitorFactory(documentManager);
		FraSCAtiScaImporter fraSCAtiScaImporter = new FraSCAtiScaImporter(nxBindingVisitorFactory, null);
		return fraSCAtiScaImporter;
	}
	
	/**
	 * 
	 * @param documentManager
	 * @param compositeFile
	 * @return
	 * @throws Exception
	 */
	public IScaImporter newLocalScaImporter(CoreSession documentManager, File compositeFile) throws Exception {
		LocalBindingVisitorFactory nxBindingVisitorFactory = new LocalBindingVisitorFactory(documentManager);
		FraSCAtiScaImporter fraSCAtiScaImporter = new FraSCAtiScaImporter(nxBindingVisitorFactory, compositeFile);
		return fraSCAtiScaImporter;
	}
	
	@Override
	public FraSCAtiRuntimeScaImporterItf newRuntimeScaImporter() throws Exception {
		return newLocalRuntimeScaImporter(documentManager);
	}
	
	///////////////////////////
	// DefaultComponent impl

    @Override
    public void activate(ComponentContext context) throws Exception {
		this.frascati = Framework.getLocalService(FraSCAtiServiceItf.class); // TODO don't call it in constructor else too early
    	
		/*AppComponent appComponent = Framework.getService(AppComponent.class); // Too early, AppComponent not yet started
    	this.apps = appComponent.getApps();*/
    	
		log.debug("Starting components");
		// For test only
		// Start the HttpDiscoveryProxy in Nuxeo with embedded FraSCAti
		//log.debug("Trying to load Http discovery proxy !");
		//System.out.println("Trying to load Http discovery proxy (NxFrascatiRegistryService.activate method)!");
		try {
			if(this.frascati != null){
				//this.frascati.processComposite("../../easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy/src/main/resources/httpDiscoveryProxy.composite");
				//easySOAApp.getFrascati().processContribution("../../easysoa-proxy/easysoa-proxy-core/easysoa-proxy-core-httpdiscoveryproxy/target/easysoa-proxy-core-httpdiscoveryproxy-0.4-SNAPSHOT.jar");
				//easySOAApp.getFrascati().processComposite("scaffoldingProxy");
				
				// Start EasySOAApps
				// TODO : Apps variable still null at the moment, How to set ?
				if(apps != null){
					for(EasySOAApp easySOAApp : apps){
						try{
							easySOAApp.start();
						}
						catch(Exception ex){
							log.error("An error occurs during the start of EasySOAApp", ex);
						}
					}
				}

			} else {
				log.debug("Unable to get FraSCAti, null returned !");
				System.out.println("Unable to get FraSCAti, null returned !");
			}
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			log.debug("Error catched when trying to load the EasySOA apps !", ex);
			System.out.println("Error catched when trying to load the EasySOA apps  : " + ex.getMessage());
		}    	
   	
    }

    @Override
    public void deactivate(ComponentContext context) throws Exception {
		
		log.debug("Closing components");
		/*for(FraSCAtiCompositeItf component : components){
			frascati.close(component);
		}*/
		// stop EasySOAApps
		if(apps != null){
			for(EasySOAApp easySOAApp : apps){
				try{
					easySOAApp.stop();
				}
				catch(Exception ex){
					log.error("An error occurs during the stop of EasySOAApp", ex);
				}
			}
		}		
		
    	// stop FraSCAti
		((Application)frascati).destroy();
    }

    @Override
    public void registerExtension(Extension extension) throws Exception {
        Object[] contribs = extension.getContributions();
        if (contribs == null) {
            return;
        }
        for (Object contrib : contribs) {
            registerContribution(contrib, extension.getExtensionPoint(), extension.getComponent());
        }
    }

    @Override
    public void unregisterExtension(Extension extension) throws Exception {
        Object[] contribs = extension.getContributions();
        if (contribs == null) {
            return;
        }
        for (Object contrib : contribs) {
            unregisterContribution(contrib, extension.getExtensionPoint(), extension.getComponent());
        }
    }

    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) throws Exception {
    	log.debug("NxFrascatiRegistryService debug (contribution) : " + contribution);
    	log.debug("NxFrascatiRegistryService debug (extensionPoint) : " + extensionPoint);
    	log.debug("NxFrascatiRegistryService debug (contributor) : " + contributor);
    }

    public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) throws Exception {
    	
    }

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        return adapter.cast(this);
    }

    @Override
    public void applicationStarted(ComponentContext context) throws Exception {
        // do nothing by default
    }

	public CoreSession getDocumentManager() {
		return documentManager;
	}

	public void setDocumentManager(CoreSession documentManager) {
		this.documentManager = documentManager;
	}

}
