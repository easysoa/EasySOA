/**
 * EasySOA Proxy
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

package org.easysoa.proxy.core.api.template;

/*import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;*/
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Deprecated
/**
 * Not used at the moment
 * @author jguillemotte
 *
 */
// TODO : Delete this class if not used
public class Template {

	// TODO : This class make the same job that TemplateFieldSuggestion class
	// Refactor these classes to keep only one class
	
	// List of template fields
	//private List<TemplateField> templateFields;

	/**
	 * Constructor
	 */
	/*
	public Template() {
		this.templateFields = new ArrayList<TemplateField>();
	}*/
	
	/**
	 * Add a templateField in the list
	 * @param templateField
	 * @throws IllegalArgumentException If templateField is null
	 */
	/*
	public void add(TemplateField templateField) throws IllegalArgumentException {
	if(templateField == null){
			throw new IllegalArgumentException("templateField must not be null !");
		}
		templateFields.add(templateField);
	}*/
	
	/**
	 * 
	 * @return
	 */
    /*@XmlElement(name="TemplateField")
    @XmlElementWrapper(name="templateFields")
	public List<TemplateField> getTemplateFields() {
		return templateFields;
	}*/

	/**
	 * 
	 * @param templateFields
	 */
	/*public void setTemplateFields(List<TemplateField> templateFields) {
		this.templateFields = templateFields;
	}*/
	
}
