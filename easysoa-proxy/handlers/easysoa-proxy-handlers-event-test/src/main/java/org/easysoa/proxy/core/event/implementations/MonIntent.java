/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.core.event.implementations;

import org.objectweb.fractal.adl.interfaces.Interface;
import org.osoa.sca.annotations.Scope;
import org.ow2.frascati.tinfi.api.IntentHandler;
import org.ow2.frascati.tinfi.api.IntentJoinPoint;

/**
 *
 * @author fntangke
 */

@Scope("COMPOSITE")
public class MonIntent implements IntentHandler{

	private IntentJoinPoint ijp;
    
    @Override
    public Object invoke(IntentJoinPoint ijpp) throws Throwable {
        Object ret; 
        this.ijp = ijpp;
        
        // PUT HERE CODE TO RUN BEFORE THE JOINPOINT PROCESSING
        System.out.println("\nMon Intent Handler est lancé");
        //
        ret = ijpp.proceed();
        
        // PUT HERE CODE TO RUN AFTER THE JOINPOINT PROCESSING

        System.out.println("\nInfos de Marc :");
        System.out.println("\nRetour : ".concat(String.valueOf(ret)));
        this.ijpinterface();
        this.ijpmethod();
        this.ijpargument();
        
        System.out.println("\nMon Intent Handler est Fini");
        return ret;
    }
    
    
    public void ijpargument(){
    	Object largs[] = this.ijp.getArguments();
    	int l = largs.length;
    	System.out.println("\nListe des Arguments : ");
    	for(int i=0; i<l;i++){
        	System.out.println("\n argument ".concat(String.valueOf(i+1).concat(" : ")).concat(largs[i].toString()));
    	}
    }
    public void ijpinterface(){
    	String in = ijp.getInterface().toString();
    	System.out.println("\nInterface (Reference ou service: )".concat(in));
    }
    
    public void ijpmethod(){
    	String in = ijp.getMethod().toString();
    	System.out.println("\nMethod : ".concat(in));
    }
    public void ijpComponent(){
    	String in = this.ijp.getComponent().toString();
    	System.out.println("\nComponent : ".concat(in));
    }
    
}
