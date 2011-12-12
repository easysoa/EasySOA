/**
 * EasySOA - Nuxeo FraSCAti
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
package org.nuxeo.frascati;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.nuxeo.frascati.api.FraSCAtiCompositeItf;
import org.nuxeo.frascati.factory.ClassLoaderSingleton;
import org.ow2.frascati.util.reflect.ReflectionHelper;

/**
 * Implementation of the FraSCAtiCompositeItf interface
 * FraSCAtiComposite class allow to interact with a FraSCAti composite instance
 */
public class FraSCAtiComposite implements FraSCAtiCompositeItf{

	Logger log = Logger.getLogger(FraSCAtiComposite.class.getCanonicalName());
	private List<String> services;
	private final ReflectionHelper component;
	private final ReflectionHelper lifeCycleController;
	private final ReflectionHelper contentController;
	
	private String name;

	/**
	 * Constructor
	 * 
	 * Create a new FraSCAtiComposite container for the FraSCAti composite instance
	 * passed as parameter 
	 * 
	 * @param component
	 * 		the FraSCAti composite instance
	 */
	public FraSCAtiComposite(Object component) {
		
		this.component  = new ReflectionHelper(ClassLoaderSingleton.classLoader(),
			"org.objectweb.fractal.api.Component");	
		
		services = new ArrayList<String>();
		
		this.component.set(component);
		
		ReflectionHelper interfasse = new ReflectionHelper(ClassLoaderSingleton.classLoader(),
				"org.objectweb.fractal.api.Interface");
		
		this.lifeCycleController= new ReflectionHelper(ClassLoaderSingleton.classLoader(),
				"org.objectweb.fractal.api.control.LifeCycleController");
		
		ReflectionHelper nameController =  new ReflectionHelper(ClassLoaderSingleton.classLoader(),
				"org.objectweb.fractal.api.control.NameController");
		
		this.contentController = new ReflectionHelper(ClassLoaderSingleton.classLoader(),
				"org.objectweb.fractal.api.control.ContentController");
		
		if(component != null){
			Object[] interfaces = (Object[]) this.component.invoke("getFcInterfaces");
			
			if(interfaces != null){
				for(Object interf : interfaces){
					interfasse.set(interf);
					String itfName = (String) interfasse.invoke("getFcItfName");
					if("lifecycle-controller".equals(itfName)){
						this.lifeCycleController.set(interf);
					} else if("name-controller".equals(itfName)){
						nameController.set(interf);
						this.name = (String) nameController.invoke("getFcName");
					} else if("content-controller".equals(itfName)){
						this.contentController.set(interf);
					} 
					services.add(itfName);
				}
			}
			log.log(Level.CONFIG,name + " component created");		
		} else {
			log.log(Level.WARNING," FraSCAtiComposite created with null embedded component");	
		}
	}


	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiCompositeItf#getComponent()
	 */
	@Override
	public Object getComponent() {
		return component.get();
	}
	
	
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiCompositeItf#getState()
	 */
	@Override
	public String getState() {
		if(lifeCycleController.get()!=null){
			return (String) lifeCycleController.invoke("getFcState");
		} else {
			log.log(Level.CONFIG,"no LifeCycleController found for " + name + " component ");
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiCompositeItf#start()
	 */
	@Override 
	public void start(){
		if(lifeCycleController.get()!=null){
			log.log(Level.CONFIG,"start the " + name + " component ");
			lifeCycleController.invoke("startFc");
		} else {
			log.log(Level.CONFIG,"no LifeCycleController found for the " + name + " component ");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiCompositeItf#stop()
	 */
	@Override 
	public void stop(){
		if(lifeCycleController.get()!=null){
			log.log(Level.CONFIG,"stop the " + name + " composite ");
			lifeCycleController.invoke("stopFc");
		} else {
			log.log(Level.CONFIG,"no LifeCycleController found for the " + name + " component ");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiCompositeItf#getName()
	 */
	@Override
	public String getName(){
		return name;
	}
	
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiCompositeItf#services()
	 */
	@Override 
	public List<String> services(){
		return this.services;
	}
		
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiCompositeItf#getSubComponents()
	 */
	@Override
	public FraSCAtiCompositeItf[] getSubComponents() {
		if(contentController.get() == null){
			log.log(Level.CONFIG,"no ContentController found for the " + name + " component ");
			return null;
		}
		Object[] components = (Object[]) contentController.invoke("getFcSubComponents");
		FraSCAtiCompositeItf[] fcomponents  = null;
		
		if(components!=null){
			fcomponents = new FraSCAtiCompositeItf[components.length];
			int n = 0;
			for(;n<components.length;n++){			
				FraSCAtiComposite fcomponent = new FraSCAtiComposite(components[n]);
				fcomponents[n] = fcomponent;
			}		
		}
		return fcomponents;
	}
	
}
