package com.openwide.sca.intents;

import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.ow2.frascati.tinfi.api.IntentHandler;
import org.ow2.frascati.tinfi.api.IntentJoinPoint;

@Scope("COMPOSITE")
@Service(IntentHandler.class)
public class LogIntent implements IntentHandler {

	  // --------------------------------------------------------------------------
	  // Implementation of the IntentHandler interface
	  // --------------------------------------------------------------------------
	  /**
	   * @see org.ow2.frascati.tinfi.control.intent.IntentHandler#invoke(IntentJoinPoint)
	   */
	  public Object invoke(IntentJoinPoint ijp) throws Throwable {
	    Object ret;
	    //
	    // PUT HERE CODE TO RUN BEFORE THE JOINPOINT PROCESSING
	    //
	    System.out.println("[LOG INTENT] PUT LOG INTENT CODE TO RUN BEFORE THE JOINPOINT PROCESSING");
	    Object[] args = ijp.getArguments();
	    for(Object obj : args){
	    	System.out.println("[LOG INTENT]  Arg Object type : " + obj.getClass().getName() + ", Object value : " + obj.toString());
	    }
	    ret = ijp.proceed();
	    //
	    // PUT HERE CODE TO RUN AFTER THE JOINPOINT PROCESSING
	    //
	    System.out.println("[LOG INTENT] PUT LOG INTENT CODE TO RUN AFTER THE JOINPOINT PROCESSING");
	    return ret;
	  }
	
}