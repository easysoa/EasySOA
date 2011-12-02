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

/**
 * @author jguillemotte
 */
public interface ExchangeRecordStore {

	/**
	 * Save an exchange record
	 * @param exchangeRecord The <code>ExchangeRecord</code> to save
	 * @return The id of the stored <code>ExchangeRecord</code>
	 * @throws Exception if a problem occurs
	 */
	public String save(ExchangeRecord exchangeRecord) throws Exception;
	
	/**
	 * Load an exchange record
	 * @param id The id of the exchange record to load
	 * @return The corresponding <code>ExchangeRecord</code> or null if the record is not found
	 * @throws Exception if a problem occurs
	 */
	public ExchangeRecord load(String id) throws Exception;
	
	/**
	 * Returns a list of Exchange records stored in file system or in database
	 * @return A list of <code>ExchangeRecord</code>
	 */
	public List<ExchangeRecord> list();	
	
}
