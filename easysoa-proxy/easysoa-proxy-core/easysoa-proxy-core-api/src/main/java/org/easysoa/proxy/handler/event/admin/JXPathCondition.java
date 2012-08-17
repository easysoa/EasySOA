/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.handler.event.admin;

import org.apache.commons.jxpath.JXPathContext;
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
        //Employee emp = (Employee)context.getValue("/departmentList/employees[name='Johnny']");
        JXPathContext context = JXPathContext.newContext(inMessage);
        InMessage result = (InMessage) context.getValue("[remoteHost='127.0.0.1']");
        if (result == null) {
            return false;
        } else {
            return true;
        }
    }
}
