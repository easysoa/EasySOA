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

package org.easysoa.registry.frascati;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.stp.sca.Composite;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.assembly.factory.api.ProcessingContext;
import org.ow2.frascati.assembly.factory.api.ProcessingMode;
import org.ow2.frascati.util.FrascatiException;

public class DiscoveryProcessingContext implements ProcessingContext {

	protected ProcessingContext delegate;
	//private FraSCAtiServiceItf fraSCAtiService;
	private FraSCAtiRuntimeScaImporterItf runtimeScaImporter;
	
	protected List<String> warningMessages = new ArrayList<String>();
	protected List<String> errorMessages = new ArrayList<String>();

	static final Log log = LogFactory.getLog(DiscoveryProcessingContext.class);
	
	//public DiscoveryProcessingContext(ProcessingContext delegate) {
	public DiscoveryProcessingContext(FraSCAtiServiceItf fraSCAtiService, FraSCAtiRuntimeScaImporterItf runtimeScaImporter, URL... urls) throws FrascatiException{
		//this.delegate = delegate;
		this.delegate = fraSCAtiService.getFraSCAti().getCompositeManager().newProcessingContext(urls);
		//this.fraSCAtiService = fraSCAtiService; // TODO get from runtimeScaImporter ??
		this.runtimeScaImporter = runtimeScaImporter;
	}

	//////////////////////////////////////////////
	// additional methods
	
	public List<String> getWarningMessages() {
		return warningMessages;
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	//////////////////////////////////////////////
	// delegate enhanced methods	
	
	public ClassLoader getClassLoader() {
		return delegate.getClassLoader();
	}

	public ProcessingMode getProcessingMode() {
		return delegate.getProcessingMode();
	}

	public <T> Class<T> loadClass(String className) throws ClassNotFoundException {
		try {
			return delegate.loadClass(className);
		} catch (Exception e) {
	          this.warning("Java class (interface.java, implementation.java...) '" + className + "' not found");
	          return null;
		}		
	}

	public void setProcessingMode(ProcessingMode processingMode) {
		delegate.setProcessingMode(processingMode);
	}

	public URL getResource(String name) {
		return delegate.getResource(name);
	}

	public Composite getRootComposite() {
		return delegate.getRootComposite();
	}

	public void setRootComposite(Composite composite) {
		delegate.setRootComposite(composite);
	}

	public <T> void putData(Object key, Class<T> type, T data) {
		delegate.putData(key, type, data);
	}

	public <T> T getData(Object key, Class<T> type) {
		log.debug("getData method ....");
		log.debug("Object = " + key);
		log.debug("Class = " + type);
		if (key instanceof Composite && type.equals(Component.class)) {
			//XXX hack to call EasySOA service discovery
			//TODO later in AssemblyFactoryManager ex. in delegate CompositeProcessor...
			Composite composite = (Composite) key;
			this.discover(composite);
		}
		return delegate.getData(key, type);
	}

	// called only with instantiate processing mode
	protected void discover(Composite composite) {
		log.debug("discover method...." + composite);
		// TODO FraSCAtiSCAImporter(...Base/Api...).visitComposite(composite)
		try {
			// pass the importer as a parameters when the processing context is created ?
			runtimeScaImporter.visitComposite(composite);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// then reimpl ScaVisitors on top of a nuxeo-free EasySOA API (instead of Nuxeo), by calling
		// either some of the existing RestNotificationRequestImpl
		// or LATER an API common to client and server like IDiscoveryRest :
		
		// Getting notification API
        // INotificationRest notificationRest = EasySoaApiFactory.getInstance().getNotificationRest();
		
		// Getting discovery API
        // IDiscoveryRest discoveryRest = EasySoaApiFactory.getInstance().getDiscoveryRest();

		// Sending a notification
        // notificationRest.
        
		// TODO when necessary, put nuxeo-free code in easysoa-registry-api and this one in nxserver/lib (instead of lib/)
	}

	public void warning(String message) {
		delegate.warning(message);
	}

	public int getWarnings() {
		return delegate.getWarnings();
	}

	public void error(String message) {
		delegate.error(message);
	}

	public int getErrors() {
		return delegate.getErrors();
	}

	public String getLocationURI(EObject eObject) {
		return delegate.getLocationURI(eObject);
	}
	
}
