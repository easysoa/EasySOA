package org.easysoa.proxy.handler.event.admin;

import org.easysoa.message.InMessage;

public interface CompiledConditionInt {
	
	boolean matches(InMessage inMessage);

}
