/**
 * EasySOA Registry
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
package org.easysoa.records;

import java.util.List;

import org.easysoa.template.TemplateFieldSuggestions;

import com.openwide.easysoa.run.Run;

/**
 * @author jguillemotte
 */
public interface ExchangeRecordStoreManager {

	/**
	 * Save an exchange record
	 * @param exchangeRecord The <code>ExchangeRecord</code> to save
	 * @return The id of the stored <code>ExchangeRecord</code>
	 * @throws Exception if a problem occurs
	 */
	public String save(ExchangeRecord exchangeRecord) throws Exception;
	
	/**
	 * Save a Run
	 * @param run the <code>Run</code> to save
	 * @return 
	 * @throws Exception If a problems occurs
	 */
	public String save(Run run) throws Exception;
	
	/**
	 * Load an exchange record
	 * @param id The id of the exchange record to load
	 * @return The corresponding <code>ExchangeRecord</code> or null if the record is not found
	 * @throws Exception if a problem occurs
	 */
	public ExchangeRecord load(String path, String id) throws Exception;
	
	/**
	 * Returns a list of Exchange records stored in file system or in database
	 * @param exchangeRecordStoreName The name of the store where the records have to be listed
	 * @return A list of <code>ExchangeRecord</code>
	 */
	public List<ExchangeRecord> getExchangeRecordlist(String exchangeRecordStoreName);
	/**
	 * Returns a list of Exchange records store
	 * @return A list of <code>ExchangeRecordStore</code>
	 */
	public List<ExchangeRecordStore> getExchangeRecordStorelist();	
	
	/**
	 * Returns a list of <code>TemplateField</code> specified in the template
	 * @param templateName The name of the template to use
	 * @return A <code>TemplateFieldSuggestion</code>
	 */
	public TemplateFieldSuggestions getTemplateFieldSuggestions(String templateName) throws Exception;
	
}
