/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.core.api.event;

import java.util.List;
import org.easysoa.message.InMessage;

/**
 * For check if the inMessage match with all the conditions
 * @author fntangke
 */
public class ConditionsMatcher {

	/**
	 * Constructor
	 */
	public ConditionsMatcher() {
	}

	/**
	 * @param compiledConditionsList
	 * @param inMessage
	 * @return true if he inMessage matches with compiledCondition
	 */
	public boolean matchesAll(List<Condition> conditionsList,
			InMessage inMessage) {
		// TODO update this method
		for (Condition compiledCondition : conditionsList) {
			if (!compiledCondition.matches(inMessage)) {
				return false;
			}
		}
		return true;
	}
}
