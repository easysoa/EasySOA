package org.nuxeo.frascati.test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.easysoa.frascati.processor.intent.ParserIntentObserver;
import org.eclipse.stp.sca.Composite;

public class ParserIntentObserverTest implements ParserIntentObserver {

	private Logger log = Logger.getLogger(ParserIntentObserverTest.class.getCanonicalName());
	
	List<Composite> composites = null;

	/**
	 * Construtor
	 */
	public ParserIntentObserverTest(){
		composites = new ArrayList<Composite>();
	}
	
	/* (non-Javadoc)
	 * @see org.easysoa.frascati.processor.intent.ParserIntentObserver#newComposite(org.eclipse.stp.sca.Composite)
	 */
	public void newComposite(Composite composite) {			
		composites.add(composite);
		log.log(Level.INFO,"new composite has been loaded :" + composite.getName());
	}
	
	public int size(){
		return composites.size();
	}
	
	public Composite last(){
		return composites.get(composites.size()-1);
	}

}
