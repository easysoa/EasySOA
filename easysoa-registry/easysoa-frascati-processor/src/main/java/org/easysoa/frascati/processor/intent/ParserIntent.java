package org.easysoa.frascati.processor.intent;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.stp.sca.Composite;
import org.osoa.sca.annotations.Scope;
import org.ow2.frascati.assembly.factory.api.ProcessingContext;
import org.ow2.frascati.tinfi.api.IntentHandler;
import org.ow2.frascati.tinfi.api.IntentJoinPoint;

@Scope("COMPOSITE")
public class ParserIntent implements IntentHandler, ParserIntentObservable{

	private List<ParserIntentObserver> observers;
	private static Logger log = Logger.getLogger(ParserIntent.class.getCanonicalName());
	
	public ParserIntent(){
		super();
		observers = new ArrayList<ParserIntentObserver>();
	}
	
	/* (non-Javadoc)
	 * @see org.ow2.frascati.tinfi.api.IntentHandler
	 * #invoke(org.ow2.frascati.tinfi.api.IntentJoinPoint)
	 */
	public Object invoke(IntentJoinPoint ijp) throws Throwable {		
	  Object ret;	  
	  ret = ijp.proceed();	
	  
	  if(observers.size()>0){	
		  
		  Composite composite = (Composite) ret; 	
		  log.log(Level.CONFIG,"Composite loaded : " + composite);
		  
		  if(composite != null){
			  
			  for(ParserIntentObserver observer :observers){
				  observer.newComposite(composite);
			  }
		  }
	  }
	  return ret;
	}

	/* (non-Javadoc)
	 * @see org.easysoa.frascati.processor.intent.ParserIntentObservable
	 * #addParserIntentObserver(org.easysoa.frascati.processor.intent.ParserIntentObserver)
	 */
	public void addParserIntentObserver(ParserIntentObserver observer) {
		observers.add(observer);
		log.log(Level.CONFIG,"new observer added :" + observer);		
	}

}