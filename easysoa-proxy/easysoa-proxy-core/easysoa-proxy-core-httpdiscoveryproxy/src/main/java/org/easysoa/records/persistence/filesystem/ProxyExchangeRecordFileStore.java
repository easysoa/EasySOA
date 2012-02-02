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
package org.easysoa.records.persistence.filesystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.template.TemplateField;
import org.easysoa.template.TemplateFieldSuggestions;
import org.easysoa.template.TemplateRecord;
import com.openwide.easysoa.proxy.PropertyManager;
import com.openwide.easysoa.run.Run;

/**
 * Take a ExchangeRecordStoreArray and store the Exchange records as files in
 * the file system
 * 
 * @author jguillemotte
 */
public class ProxyExchangeRecordFileStore extends ExchangeRecordFileStore {

	// Logger
	private static Logger logger = Logger.getLogger(ProxyExchangeRecordFileStore.class.getName());

	public final static String TEMPLATE_FILE_EXTENSION = ".vm";
	public final static String SUGGESTIONS_FILE_EXTENSION = ".fld";
	public final static String REQ_TEMPLATE_FILE_PREFIX = "reqTemplateRecord_";
	public final static String RES_TEMPLATE_FILE_PREFIX = "resTemplateRecord_";
	public final static String SUGGESTION_FILE_PREFIX = "fieldSuggestions_";

	/**
	 * Path is set with the value of 'path.record.store' property
	 */
	public ProxyExchangeRecordFileStore() {
		super(PropertyManager.getProperty("path.record.store"));
		logger.debug("exchangeRecordStore default path = " + this.path);
	}

	/**
	 * Constructor
	 * @param path Path where the Exchange record will be stored
	 */
	public ProxyExchangeRecordFileStore(String path) {
	    super(path);
	}

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.easysoa.sca.records.ExchangeRecordStore#save(com.openwide.easysoa.run.Run)
     */
    public void save(Run run) throws Exception {
        // Create the run folder
        logger.debug("Path to store run = " + path + run.getName());
        createStore(run.getName());
        for (ExchangeRecord record : run.getExchangeRecordList()) {
            save(record, path + run.getName() + "/");
        }
    }	
	
	/**
	 * Save field suggestions
	 * @param templateFieldSuggestions
	 * @param recordID
	 * @throws IOException
	 */
	public void save(TemplateFieldSuggestions templateFieldSuggestions, String recordID) throws IOException {
		File runFolder = new File(path);
		runFolder.mkdir();
		String fieldSuggestFileName = SUGGESTION_FILE_PREFIX + recordID + SUGGESTIONS_FILE_EXTENSION;
		File fieldSuggestFile = new File(path + fieldSuggestFileName);
		FileWriter fieldSuggestFw = new FileWriter(fieldSuggestFile);
		try{
			JSONObject fieldSuggestJSON = JSONObject.fromObject(templateFieldSuggestions);
			fieldSuggestFw.write(fieldSuggestJSON.toString());
		}
		finally{
			fieldSuggestFw.close();
		}
	}
	
    /**
     * Save a template record
     * @param templateRecord The template record to save
     * @throws IOException If a problem occurs
     */
    public Map<String, String> save(TemplateRecord templateRecord) throws IOException {
        HashMap<String, String> templateFileMap = new HashMap<String, String>();
        File runFolder = new File(path);
        runFolder.mkdir();
        String reqTemplateFileName = REQ_TEMPLATE_FILE_PREFIX + templateRecord.getrecordID() + TEMPLATE_FILE_EXTENSION;
        String resTemplateFileName = RES_TEMPLATE_FILE_PREFIX + templateRecord.getrecordID() + TEMPLATE_FILE_EXTENSION;
        File reqTemplateRecordFile = new File(path + reqTemplateFileName);
        File resTemplateRecordFile = new File(path + resTemplateFileName);
        FileWriter reqTemplateFw = new FileWriter(reqTemplateRecordFile);
        FileWriter resTemplateFw = new FileWriter(resTemplateRecordFile);
        try{
            reqTemplateFw.write(templateRecord.getRequestTemplate());
            templateFileMap.put("reqTemplate", reqTemplateFileName);
            resTemplateFw.write(templateRecord.getResponsetemplate());
            templateFileMap.put("resTemplate", resTemplateFileName);            
        }
        finally{
            reqTemplateFw.close();
            resTemplateFw.close();
        }
        return templateFileMap;
    }	
	
	//TODO rename the class templateFieldSuggestion to TemplateField....
	public TemplateFieldSuggestions getTemplateFieldSuggestions(String storeName, String fileName) throws Exception {
		logger.debug("loading template :" + fileName);
		@SuppressWarnings("rawtypes")
        HashMap<String, Class> classMap = new HashMap<String, Class>();
		classMap.put("templateFields", TemplateField.class);
		return (TemplateFieldSuggestions) JSONObject.toBean(readJSONFile(path + storeName + "/" + fileName + SUGGESTIONS_FILE_EXTENSION), TemplateFieldSuggestions.class, classMap);
	}
	
	/**
	 * Returns the name list of the recorded templates
	 * @return A <code>List</code> containing the name of the template files
	 */
	public List<String> getTemplateList(String storeName){
		ArrayList<String> templateFileList = new ArrayList<String>();
		// TODO : remove hard coded path
		File folder = new File(PropertyManager.getProperty("path.template.store") + storeName);
		File[] listOfFiles = folder.listFiles();
		if(listOfFiles != null){
			for (File file : listOfFiles) {
				if (file.isFile() && file.getName().endsWith(TEMPLATE_FILE_EXTENSION)) {
					logger.debug("file name : " + file.getName());
					templateFileList.add(file.getName());		
				}
			}
		}
		return templateFileList;
	}

}
