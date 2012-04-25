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

package org.easysoa.template;

import java.util.List;
import java.util.Map;
import org.easysoa.records.ExchangeRecord;
import com.openwide.easysoa.message.OutMessage;

public interface TemplateEngine {
    
    /**
     * Suggest the fields and generate a fld file
     * @param exchangeRecord The exchange record
     * @return <code>TemplateFieldSuggestions</code>
     */
    public abstract TemplateFieldSuggestions suggestFields(ExchangeRecord exchangeRecord, String storeName, boolean generateFile) throws Exception;

    /**
     * Generate the templates for an exchange record (and create a template file)
     * @param exchangeRecord
     * @throws Exception 
     */
    public abstract ExchangeRecord generateTemplate(TemplateFieldSuggestions fieldSuggestions, ExchangeRecord exchangeRecord, String storeName, boolean generateFile) throws Exception;

    /**
     * generate the template for a list of exchange records
     * @param exchangeRecordList
     */
    /*public void generateTemplate( List<ExchangeRecord> exchangeRecordList, boolean generateFile){
        for(ExchangeRecord record : exchangeRecordList){
            generateTemplate(record, generateFile);
        }
    }*/

    /**
     * Call the template renderer and replay the templatized record
     * @param storeName The store name
     * @param recordID The record id
     * @param fieldValues The custom user field values
     * @param simulation true if we are in simulation mode, false otherwise
     * @return the replayed response
     */
    public abstract OutMessage renderTemplateAndReplay(String storeName, ExchangeRecord record, Map<String, List<String>> fieldValues, boolean simulation) throws Exception;

}