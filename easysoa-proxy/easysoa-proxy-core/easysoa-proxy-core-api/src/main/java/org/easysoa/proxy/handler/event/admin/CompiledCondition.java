package org.easysoa.proxy.handler.event.admin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.openwide.easysoa.message.InMessage;

/**
 * 
 * @author fntangke
 * For store the regex to use for check if inMessage url matches with a sweb service to listen
 * 
 */

public class CompiledCondition implements CompiledConditionInt {

	private Pattern p; 
	
	public CompiledCondition(){		
	}
	
	public CompiledCondition(String url){
		this.p = Pattern.compile(url);
	}
		
	/**
	 * @return true or false if the pattern's url matched 
	 */
	
	public boolean matches(InMessage inMessage){
	
		Matcher m = this.p.matcher(inMessage.buildCompleteUrl());
		if (m.groupCount()!=0)
			return true;
		else return false;
	}
	
	public Pattern getP() {
		return p;
	}

	public void setP(Pattern p) {
		this.p = p;
	}

}
