/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.core.api.event;

import javax.xml.bind.annotation.XmlRootElement;

import org.easysoa.message.InMessage;

/**
 *
 * @author fntangke
 */
@XmlRootElement
public interface Condition {
    
    public abstract boolean matches();
    /**
     * 
     * @param inMessage
     * @return true if the condition is OK 
     */
    public abstract boolean matches(InMessage inMessage);
}
