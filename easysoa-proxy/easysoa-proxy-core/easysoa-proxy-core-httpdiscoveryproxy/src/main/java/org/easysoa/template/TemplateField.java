/**
 * 
 */
package org.easysoa.template;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jguillemotte
 *
 */
@XmlRootElement
public class TemplateField {
	
	private String fieldName;
	private String fieldType;
	private String defaultValue;
	// Type of parameter : formParam, queryParam, pathParam for rest ... or wsdlParam for ws services
	// In case of pathParam, we need to have the param position in the URL => See HTTP discovery proxy system to discover and registering app, services, api's
	private TemplateFieldType paramType;
	// Template to define the parameter position in url path (eg : /X/X/{param}/X), the first '/' represent the root of the path 
	private int pathParamPosition;
	
	public enum TemplateFieldType {
		CONTENT_PARAM,
		QUERY_PARAM,
		PATH_PARAM,
		WSDL_PARAM
	}
	
	/**
	 * 
	 */
	public TemplateField(){
		fieldName = "";
		fieldType = "";
		defaultValue = "";
		pathParamPosition = 0;
	}

	/**
	 * 
	 * @return
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * 
	 * @param fieldName
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * 
	 * @return
	 */
	public String getFieldType() {
		return fieldType;
	}

	/**
	 * 
	 * @param fieldType
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * 
	 * @return
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * 
	 * @param defaultValue
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * 
	 * @return
	 */
	public TemplateFieldType getParamType() {
		return paramType;
	}

	/**
	 * 
	 * @param paramType
	 */
	public void setParamType(TemplateFieldType paramType) {
		this.paramType = paramType;
	}

	/**
	 * 
	 * @return
	 */
	public int getPathParamPosition() {
		return pathParamPosition;
	}

	/**
	 * Number to define the parameter position in url path (eg : for http://localhost:8088/1/users/show/FR3Aquitaine.xml, the param user correspond to number 4 (FR3Aquitaine.xml)), the first '/' represent the root of the path. 
	 * @param pathParamPosition
	 */
	public void setPathParamPosition(int pathParamPosition) {
		this.pathParamPosition = pathParamPosition;
	}
	
}
