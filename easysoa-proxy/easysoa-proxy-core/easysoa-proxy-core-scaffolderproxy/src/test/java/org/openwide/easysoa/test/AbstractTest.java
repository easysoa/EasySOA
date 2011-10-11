package org.openwide.easysoa.test;

import java.util.ArrayList;

import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

public abstract class AbstractTest {

	/** The FraSCAti platform */
    protected static FraSCAti frascati;
    
    protected static ArrayList<Component> componentList;
	
    /** Set system properties for FraSCAti */
	static {
		System.setProperty("org.ow2.frascati.bootstrap", "org.ow2.frascati.bootstrap.FraSCAti");
	}    
    
	/**
	 * Start FraSCAti
	 * @throws FrascatiException 
	 */
	protected static void startFraSCAti() throws FrascatiException{
		componentList =  new ArrayList<Component>();
		frascati = FraSCAti.newFraSCAti();
	}
	
	/**
	 * 
	 * @throws FrascatiException
	 */
	protected static void stopFraSCAti() throws FrascatiException{
		if(componentList != null){
			for(Component component : componentList){
				frascati.close(component);
			}
		}
	}
	
	/**
	 * Start Velocity Test
	 * @throws FrascatiException
	 */
	protected static void startScaffoldingProxyComposite() throws FrascatiException{
		componentList.add(frascati.processComposite("src/test/resources/scaffoldingProxy.composite", new ProcessingContextImpl()));
	}

	/**
	 * Start Soap service mock
	 * @throws FrascatiException
	 */
	protected static void startSoapServiceMockComposite() throws FrascatiException{
		componentList.add(frascati.processComposite("src/test/resources/soapServiceMock.composite", new ProcessingContextImpl()));
	}
	
}
