/**
 * EasySOA Proxy
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
 * Contact : easysoa-dev@groups.google.com
 */

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