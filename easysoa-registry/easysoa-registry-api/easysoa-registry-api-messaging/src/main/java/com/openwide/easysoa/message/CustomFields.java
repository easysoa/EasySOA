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

package com.openwide.easysoa.message;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom fields collection. A custom field is a key/value association.
 * @author jguillemotte
 *
 */
public class CustomFields {

	private Map<String, CustomField> customFieldList;

	/**
	 * Creates a new <code>CustomFields</code> object
	 */
	public CustomFields() {
		this.customFieldList = new HashMap<String, CustomField>();
	}

	/**
	 * Returns the customFields map.
	 * @return Returns the customFields.
	 */
	public Map<String, CustomField> getCustomFieldList() {
		return customFieldList;
	}

	/**
	 * Sets the customFields value.
	 * @param customFields The customFields to set.
	 */
	public void setCustomFieldList(Map<String, CustomField> customFields) {
		this.customFieldList = customFields;
	}

	/**
	 * Adds a custom field to CustomFields object
	 * @param name Name for custom field
	 * @param value Value to set for the custom field
	 */
	public void addCustomField(String name, String value) {
		this.customFieldList.put(name, new CustomField(name, value));
	}

	/**
	 * Gets custom field value by name
	 * 
	 * @param name Name of custom field to get
	 * @return the associated value or null if not present
	 */
	public CustomField getCustomField(String name) {
		return this.customFieldList.get(name);
	}

}
