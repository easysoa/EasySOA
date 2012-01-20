/**
 * 
 */
package org.easysoa.template;

import org.easysoa.records.ExchangeRecord;

import net.sf.json.JSONObject;

/**
 * @author jguillemotte
 *
 */
public class TemplateRecord {

	public final static String VELOCIMACRO_REQUEST_PREFIX = "#macro ( renderReq $arg0 $arg1 $arg2 )";
	// TODO CHange the response prefix
	public final static String VELOCIMACRO_RESPONSE_PREFIX = "#macro ( renderRes $arg0 $arg1 $arg2 )";
	public final static String VELOCIMACRO_SUFFIX = "#end";

	private ExchangeRecord customRecord;
	
	public TemplateRecord(ExchangeRecord customRecord){
		this.customRecord = customRecord;
	}
	
	public String getRequestTemplate(){
		JSONObject jsonRecord = JSONObject.fromObject(customRecord.getInMessage());
		return VELOCIMACRO_REQUEST_PREFIX + "\n" + jsonRecord.toString().replace("\\\"", "\"") + "\n" + VELOCIMACRO_SUFFIX; 
	}
	
	public String getrecordID(){
		return this.customRecord.getExchange().getExchangeID();
	}
	
	public String getResponsetemplate(){
		JSONObject jsonRecord = JSONObject.fromObject(customRecord.getOutMessage());
		return VELOCIMACRO_RESPONSE_PREFIX + "\n" + jsonRecord.toString().replace("\\\"", "\"") + "\n" + VELOCIMACRO_SUFFIX;
	}
}
