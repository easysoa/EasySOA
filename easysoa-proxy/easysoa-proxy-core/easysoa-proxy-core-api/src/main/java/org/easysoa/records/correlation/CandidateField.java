/**
 * EasySOA Proxy core
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

package org.easysoa.records.correlation;

// TODO : merge with TemplateField and add informations about param (form, path, query ...position..)
// to retrieve it more easily in the template builder.
// Seem better to replace this class by templateField

/**
 * This object is used to prepare the input / ouput fields before to pass them to the correlation engine
 * @author jguillemotte
 *
 */
public class CandidateField {

    /*public enum CandidateFieldKind {
        QUERY_PARAM,
        PATH_PARAM,
        CONTENT_PARAM
    }*/
    
    // Field name
    private String name;
    // Field (default) value
    private String value;
    // What is type : String, int ...
    private String type;  
    // ?? kind : id ?
    private String kind;
    // ?? annot : found in GET, count... ??
    private String path;    
    //private String valueGetExpression;
    //private String valueSetExpression;
    
    /**
     * Constructor
     * @param path
     * @param value
     */
    public CandidateField(String path, String value) {
        this.kind = "json"; // "path", "content" ?? Or param type like form, query or path  ????
        this.path = path;
        this.name = this.path.substring(this.path.lastIndexOf('/') + 1);
        this.value = value;
    }
    
    /**
     * 
     * @param path
     * @param value
     * @param type
     */
    public CandidateField(String path, String value, String type) {
        this(path, value);
        this.type = type;
    }
    
    /**
     * 
     * @param kind
     */
    public void setKind(String kind){
    	this.kind = kind;
    }
    
    /**
     * 
     * @param message
     * @return
     */
    /*public String getValue(Object message) {
        return null;
    }*/
    
    //public void setValue(String value, Object message) {
    //    
    //}
    /*public void templatize(Object message) {
        
    }*/

    /**
     * 
     * @return
     */
    public String getKind() {
        return kind;
    }

    /**
     * 
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * 
     * @return
     */
    public String getType() {
        return type;
    }
    
    /**
     * 
     */
    @Override
    public String toString() {
        return "[" + this.getPath() + "=" + this.getValue() + "]";
    }
}
