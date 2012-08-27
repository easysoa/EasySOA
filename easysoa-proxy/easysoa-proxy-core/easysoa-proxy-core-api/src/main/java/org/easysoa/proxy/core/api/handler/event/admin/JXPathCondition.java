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
public class JXPathCondition implements CompiledCondition {

    @Override
    public boolean matches() {
        return false;
    }

    /**
     *
     * @param inMessage
     * @return true if the inMessage matches with the JXPathCOndition else false
     */
    @Override
    public boolean matches(InMessage inMessage) {
        return false;
    }
}
