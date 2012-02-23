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
import org.easysoa.logs.Report;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.assertions.AssertionReport;
import org.easysoa.records.assertions.AssertionSuggestions;
import org.easysoa.template.TemplateField;
import org.easysoa.template.TemplateFieldSuggestions;
import org.easysoa.template.VelocityTemplate;
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

	// File extensions
	public final static String TEMPLATE_FILE_EXTENSION = ".vm";
	public final static String SUGGESTIONS_FILE_EXTENSION = ".fld";
	public final static String ASSERTIONS_FILE_EXTENSION = ".asr";
	// File name prefix
	public final static String REQ_TEMPLATE_FILE_PREFIX = "reqTemplateRecord_";
	public final static String RES_TEMPLATE_FILE_PREFIX = "resTemplateRecord_";
	public final static String SUGGESTION_FILE_PREFIX = "fieldSuggestions_";
	public final static String ASSERTIONS_FILE_PREFIX = "assertionSuggestions_";

	// Template path for fld and asr file storage
	private String templatePath;
	//private String storePath;
	
	private String reportPath;
	
	/**
	 * Path is set with the value of 'path.record.store' property
	 */
	public ProxyExchangeRecordFileStore() {
		super(PropertyManager.getProperty("path.record.store"));
	    //this.storePath = PropertyManager.getProperty("path.record.store");
		this.templatePath = PropertyManager.getProperty("path.template.store");
		this.reportPath = PropertyManager.getProperty("path.reports");
		
		logger.debug("Using property 'path.record.store' for record store path = " + this.path);
		logger.debug("Using property 'path.template.store' for template store path = " + this.templatePath);
		logger.debug("Using property 'path.reports' for reports path = " + this.reportPath);
	}

	/**
	 * Constructor
	 * @param path Path where the Exchange record will be stored
	 */
	/*public ProxyExchangeRecordFileStore(String path) {
	    super(path);
	}*/

	/**
	 * Replace the original method.
	 * In this method 2 stores are created at the same time : Record store for original exchange records
	 * and template store for template files, assertions files and customized exchange records
	 */
    @Override
    public void createStore(String storeName) {
        logger.debug("Creating record store folder : " + path + storeName);
        File storeFolder = new File(path + storeName);
        storeFolder.mkdirs();
        logger.debug("Creating template store folder : " + templatePath + storeName);
        File templateFolder = new File(templatePath + storeName);
        templateFolder.mkdirs();
    }	

    /**
     * Copy the record files form the original store to the template store
     * @param recordID
     * @param storeName
     */
    public void makeCopyOfExchangeRecord(String recordID, String storeName){
        
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
        // Create the store folder
        createStore(run.getName());
        // Create in the same time the template folder
        
        for (ExchangeRecord record : run.getExchangeRecordList()) {
            save(record, path + run.getName() + "/");
        }
    }	
	
	/**
	 * Save field suggestions in a fld file
	 * @param templateFieldSuggestions
	 * @param recordID
	 * @throws IOException
	 */
	public void saveFieldSuggestions(TemplateFieldSuggestions templateFieldSuggestions, String storeName, String recordID) throws IOException {
		//File runFolder = new File(templatePath);
		//runFolder.mkdir();
		String fieldSuggestFileName = SUGGESTION_FILE_PREFIX + recordID + SUGGESTIONS_FILE_EXTENSION;
		File fieldSuggestFile = new File(templatePath + storeName + "/" + fieldSuggestFileName);
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
	 * Save a customized ExchangeRecord in the template store
	 * @param record
	 * @param storeName
	 * @throws Exception 
	 */
	public void saveCustomRecord(ExchangeRecord record, String storeName) throws Exception{
	    this.save(record, templatePath + storeName + "/");
	}
	
    /**
     * Save a template record
     * @param templateRecord The template record to save
     * @throws IOException If a problem occurs
     */
    public Map<String, String> saveTemplate(VelocityTemplate templateRecord, String storeName) throws IOException {
        HashMap<String, String> templateFileMap = new HashMap<String, String>();
        /*File runFolder = new File(path);
        runFolder.mkdir();*/
        String reqTemplateFileName = REQ_TEMPLATE_FILE_PREFIX + templateRecord.getrecordID() + TEMPLATE_FILE_EXTENSION;
        String resTemplateFileName = RES_TEMPLATE_FILE_PREFIX + templateRecord.getrecordID() + TEMPLATE_FILE_EXTENSION;
        File reqTemplateRecordFile = new File(templatePath + "/" + storeName + "/" + reqTemplateFileName);
        File resTemplateRecordFile = new File(templatePath + "/" + storeName + "/" + resTemplateFileName);
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
	public TemplateFieldSuggestions getTemplateFieldSuggestions(String storeName, String recordID) throws Exception {
		logger.debug("loading field suggestions for record ID : " + recordID);
		@SuppressWarnings("rawtypes")
        HashMap<String, Class> classMap = new HashMap<String, Class>();
		classMap.put("templateFields", TemplateField.class);
		return (TemplateFieldSuggestions) JSONObject.toBean(readJSONFile(templatePath + storeName + "/" + SUGGESTION_FILE_PREFIX + recordID + SUGGESTIONS_FILE_EXTENSION), TemplateFieldSuggestions.class, classMap);
	}

	/**
	 * Returns the name list of the recorded templates
	 * @param storeName Name of the store where the template are stored 
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

	/**
	 * Load assertion suggestions from JSON ASR file
	 * @param storeName Name of the store where the assertion suggestions are stored
	 * @return The Assertion suggestions
	 * @throws Exception If a problem occurs
	 */
	public AssertionSuggestions getAssertionSuggestions(String storeName, String recordID) throws Exception {
        logger.debug("loading assertion suggestions :" + ASSERTIONS_FILE_PREFIX + recordID + ASSERTIONS_FILE_EXTENSION);
        @SuppressWarnings("rawtypes")
        HashMap<String, Class> classMap = new HashMap<String, Class>();
        //classMap.put("templateFields", TemplateField.class);
        return (AssertionSuggestions) JSONObject.toBean(readJSONFile(templatePath + storeName + "/" + ASSERTIONS_FILE_PREFIX + recordID + ASSERTIONS_FILE_EXTENSION), AssertionSuggestions.class, classMap);	    
	}
	
	/**
	 * Save assertion suggestions
	 * @param assertionSuggestions The assertion suggestion to save
	 * @param recordID The record ID
	 * @param storeName The store name where the asr file will be saved
	 * @throws Exception If a problem occurs 
	 */
	public void saveAssertionSuggestions(AssertionSuggestions assertionSuggestions, String recordID, String storeName) throws Exception {
        String assertionSuggestFileName = ASSERTIONS_FILE_PREFIX + recordID + ASSERTIONS_FILE_EXTENSION;
        File assertionSuggestFile = new File(templatePath + storeName + "/" + assertionSuggestFileName);
        FileWriter assertionSuggestFw = new FileWriter(assertionSuggestFile);
        try{
            JSONObject fieldSuggestJSON = JSONObject.fromObject(assertionSuggestions);
            assertionSuggestFw.write(fieldSuggestJSON.toString());
        }
        finally{
            assertionSuggestFw.close();
        }	    
	}

	/**
	 * Save an assertion report, using the report name to generate two files corresponding to xml and txt versions
	 * @param assertionReport The assertion report to save
	 * @throws Exception If a problem occurs 
	 */
    public void saveReport(Report report) throws Exception {
        File xmlAssertionReportFile = new File(report.getReportName() + ".xml");
        FileWriter assertionReportFw = new FileWriter(xmlAssertionReportFile);
        try{
            assertionReportFw.write(report.generateXMLReport());
        }
        finally{
            assertionReportFw.close();
        }
    }

}
