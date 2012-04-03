/**
 * EasySOA HTTP Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */
package org.easysoa.template;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jguillemotte
 *
 */
@XmlRootElement
public abstract class AbstractTemplateField {
	
    // TODO : Best solution to adapt this class for simulation ??
    // - Add attributes fields to differentiate input/output fields or for specific input/output data ??
    // - Or create specific InputTemplateField and OutputTemplateField class, extending abstractTemplateField class ???
    
    // Possible to use a common path filed for input and output ??
    // Maybe using a mask
    
    // TODO to have a working simulation engine :
    
    // - Try to use and adapt the template engine : this engine wotks only with input at the moment.
    // think it is possible to make some modifications to have a template engine working also for outputs
    
    // Suggestion engine : same pb, work only for inputs.
    // First step => make modifications to TemplateField and associated classes to have a working model for input/output fields
   
    // Question : how to find the required field value to replace (by template expression or by value) in different data format ?
    // path, query params, form ..., response content (can contains JSON, XML simple text ....)
    // 
    //req(Extraction) Path, 
    //res(ReplaceForTemplatization) Path, 
    /*(req)foundValue, 
    (resFoundValue NOALLCURRENTCORRELATIONSHAVEEQUALVALUE)
    correlationLevel 
    */        
    
    // How to add templatefields in updated fld template files ?
    // Time remaining before the end of the day : 1 hour and 15 minutes !
    
    // Field name
	protected String fieldName;
	// Field type : string, int .... (not very important at the moment, all the data can be treated as string)
	// TODO : Maybe better to change the type to indicate an input our output field ???
	protected String fieldType;
	// Field default value
	protected String defaultValue;
	// Type of parameter : formParam, queryParam, pathParam for rest ... or wsdlParam for ws services
	// In case of pathParam, we need to have the param position in the URL => See HTTP discovery proxy system to discover and registering app, services, api's
	protected TemplateFieldType paramType;
	// Template to define the parameter position in url path (eg : /X/X/{param}/X), the first '/' represent the root of the path 
	private boolean fieldEquality;
	// Correlation level : higher is better
	protected int correlationLevel;

	/*
    (fields { (name/id), req(Extraction)Path, res(ReplaceForTemplatization)Path, (req)foundValue, (resFoundValueNOALLCURRENTCORRELATIONSHAVEEQUALVALUE), correlLevel }, resTemplate)
	 */

    /**
	 * 
	 * @author jguillemotte
	 *
	 */
	public enum TemplateFieldType {
		CONTENT_PARAM,
		QUERY_PARAM,
		PATH_PARAM,
		WSDL_PARAM
	}
	
	/**
	 * Default constructor
	 */
	public AbstractTemplateField(){
		fieldName = "";
		fieldType = "";
		defaultValue = "";
		setFieldEquality(true);
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
     * Returns the field equality
	 * @return true if the field have to be processed by assertion engine, false otherwise
	 */
    public boolean isFieldEquality() {
        return fieldEquality;
    }

    /**
     * Set the field equality
     * Set to true if the field have to be processed by assertion engine, false otherwise 
     * @param fieldEquality
     */
    public void setFieldEquality(boolean fieldEquality) {
        this.fieldEquality = fieldEquality;
    }
	
    /**
     * 
     * @return
     */
    public int getCorrelationLevel() {
        return correlationLevel;
    }

    /**
     * 
     * @param correlationLevel
     */
    public void setCorrelationLevel(int correlationLevel) {
        this.correlationLevel = correlationLevel;
    }    
    
    public abstract int getPathParamPosition();
    public abstract void setPathParamPosition(int pathParamPosition);
}
