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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.stp.sca.Composite;
import org.nuxeo.frascati.NuxeoFraSCAtiException;
import org.nuxeo.frascati.api.AbstractProcessingContext;


public class DiscoveryProcessingContext extends AbstractProcessingContext {

	//private FraSCAtiServiceItf fraSCAtiService;
	private FraSCAtiRuntimeScaImporterItf runtimeScaImporter;
	
	static final Log log = LogFactory.getLog(DiscoveryProcessingContext.class);
	
	public DiscoveryProcessingContext(FraSCAtiRegistryServiceItf fraSCAtiService, 
			FraSCAtiRuntimeScaImporterItf runtimeScaImporter, URL... urls) throws NuxeoFraSCAtiException {
		
		super(fraSCAtiService.getFraSCAti().newProcessingContext(urls));
		this.runtimeScaImporter = runtimeScaImporter;
	}

	/**
	 * 
	 * @param key
	 * @param type
	 * @param data
	 */
	public <T> void putData(Object key, Class<T> type, T data) {
		delegate.invokeInherited("putData",
				"org.ow2.frascati.parser.api.ParsingContext",
				new Class<?>[]{Object.class,Class.class,data.getClass()},
				new Object[]{key, type, data});
	}

	/**
	 * 
	 * @param key
	 * @param type
	 * @return
	 */
	public <T> T getData(Object key, Class<T> type) {
		log.debug("getData method ....");
		log.debug("Object = " + key);
		log.debug("Class = " + type);
		if (key instanceof Composite && "org.objectweb.fractal.api.Component".equals(type.getCanonicalName())) {
			//XXX hack to call EasySOA service discovery
			//TODO later in AssemblyFactoryManager ex. in delegate CompositeProcessor...
			Composite composite = (Composite) key;
			this.discover(composite);
		}
		
		T data = (T) delegate.invokeInherited("getData",
				"org.ow2.frascati.parser.api.ParsingContext",
				new Class<?>[]{Composite.class,Class.class},
				new Object[]{key,type});
		
		return data;
	}

	/**
	 * called only with instantiate processing mode
	 * @param composite
	 */
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

}
