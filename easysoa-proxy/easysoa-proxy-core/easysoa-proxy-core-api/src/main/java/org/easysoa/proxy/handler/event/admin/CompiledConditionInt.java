package org.easysoa.proxy.handler.event.admin;

import com.openwide.easysoa.message.InMessage;

public interface CompiledConditionInt {
	
	boolean matches(InMessage inMessage);

}
