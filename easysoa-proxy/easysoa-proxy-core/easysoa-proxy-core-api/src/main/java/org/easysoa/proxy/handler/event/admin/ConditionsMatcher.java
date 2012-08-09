/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.handler.event.admin;

import java.util.List;
import java.util.Map;
import org.easysoa.message.InMessage;

/**
 * For check if the inMessage match with all the conditions
 *
 * @author fntangke
 */
public class ConditionsMatcher {

    /**
     * Constructor
     */
    public ConditionsMatcher() {
    }

    /**
     * 
     * @param compiledConditionsList
     * @param inMessage
     * @return true if he inMessage matches with compiledCondition
     */
    public boolean matchesAll(List<CompiledCondition> compiledConditionsList, InMessage inMessage) {
        //TODO  update this method
        for(CompiledCondition compiledCondition : compiledConditionsList){
            if(!compiledCondition.matches(inMessage)) {
                return false;
            }
        }
        return true;
    }
    
}
