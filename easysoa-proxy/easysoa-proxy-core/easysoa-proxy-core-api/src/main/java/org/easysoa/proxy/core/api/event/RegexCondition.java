package org.easysoa.proxy.core.api.event;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import org.easysoa.message.InMessage;
import org.easysoa.records.ExchangeRecord;

/**
 * @author fntangke
 */

@XmlRootElement
public class RegexCondition implements Condition {

	private String regex;

	public RegexCondition() {
		this.regex = "";
	}

	public RegexCondition(String regex) {
		this.regex = regex;
	}

	/**
	 * Regex getter
	 * 
	 * @return
	 */
	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	@Override
	public boolean matches() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean matches(ExchangeRecord exchangeRecord) {
		// TODO Auto-generated method stub
		return false;
	}
}
