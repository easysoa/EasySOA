/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.core.api.handler.event.admin;

import org.easysoa.message.InMessage;

/**
 *
 * @author fntangke
 */
public interface CompiledCondition {
    
    boolean matches();
    /**
     * 
     * @param inMessage
     * @return true if the condition is OK 
     */
    boolean matches(InMessage inMessage);
}
