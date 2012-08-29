package org.easysoa.proxy.core.api.event;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.annotation.XmlRootElement;
import org.easysoa.message.InMessage;

public class RegexCompiledCondition extends RegexCondition {

	private Pattern p;

	public RegexCompiledCondition() {
	}

	/**
	 * Constructor
	 * @param regexCondition
	 */
	public RegexCompiledCondition(RegexCondition regexCondition) {
		this.p = Pattern.compile(regexCondition.getRegex());
	}

	/** 
	 * @return
	 */
	public Pattern getP() {
		return p;
	}
	
	/** 
	 * @param p
	 */
	public void setP(Pattern p) {
		this.p = p;
	}

	@Override
	public boolean matches(InMessage inMessage) {
		Matcher m = this.p.matcher(inMessage.buildCompleteUrl());
		return m.matches();
	}

	@Override
	public boolean matches() {
		// TODO Auto-generated method stub
		return false;
	}
}
