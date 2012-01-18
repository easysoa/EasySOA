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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.log4j.Logger;
import org.easysoa.records.Exchange;
import org.easysoa.records.Exchange.ExchangeType;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.ExchangeRecordStore;
import org.easysoa.records.ExchangeRecordStoreManager;
import org.easysoa.template.TemplateField;
import org.easysoa.template.TemplateFieldSuggestions;
import org.easysoa.template.TemplateRecord;
import com.openwide.easysoa.message.CustomField;
import com.openwide.easysoa.message.CustomFields;
import com.openwide.easysoa.message.Header;
import com.openwide.easysoa.message.Headers;
import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.MessageContent;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.message.PostData;
import com.openwide.easysoa.message.QueryParam;
import com.openwide.easysoa.message.QueryString;
import com.openwide.easysoa.run.Run;

/**
 * Take a ExchangeRecordStoreArray and store the Exchange records as files in
 * the file system
 * 
 * @author jguillemotte
 * 
 */
public class ExchangeRecordFileStore implements ExchangeRecordStoreManager {

	// TODO : The Exchange records must be stored in a human readable format :
	// no serialization but JSON instead.
	// Maybe is a good idea to work with HAR format with custom extensions to
	// store ID's, request times ....
	// See if it is necessary to store Exchange records sets at once in the same
	// file (HAR can do that) instead of storing one record in one file.

	// If we use HAR, we need to made a class to map a servletRequest/Response
	// to HAR Message Object. HAR log are stored with JSON format.
	// When reading a stored HAR file, parsing from JSON is automatic, just have
	// to map HAR message object informations to servlet object.

	// Logger
	private static Logger logger = Logger.getLogger(ExchangeRecordFileStore.class.getName());

	public final static String EXCHANGE_FILE_EXTENSION = ".json";
	public final static String TEMPLATE_FILE_EXTENSION = ".vm";
	public final static String SUGGESTIONS_FILE_EXTENSION = ".fld";
	public final static String EXCHANGE_FILE_PREFIX = "excRec_";
	public final static String IN_MESSAGE_FILE_PREFIX = "inMess_";
	public final static String OUT_MESSAGE_FILE_PREFIX = "outMess_";
	public final static String REQ_TEMPLATE_FILE_PREFIX = "reqTemplateRecord_";
	public final static String RES_TEMPLATE_FILE_PREFIX = "resTemplateRecord_";
	public final static String SUGGESTION_FILE_PREFIX = "fieldSuggestions_";

	// TODO : Modify the way the path is stored, used ....
	// Base path = target/
	// customPath = name of ExchangeRecordStore
	private String path;

	/**
	 * Default path is target
	 */
	public ExchangeRecordFileStore() {
		// TODO remove the 'target/' default path
		this.path = "target/";
	}

	/**
	 * Constructor
	 * 
	 * @param path
	 *            Path where the Exchange record will be stored
	 */
	public ExchangeRecordFileStore(String path) {
		this.path = path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.easysoa.sca.records.ExchangeRecordStore#list()
	 */
	@Override
	public List<ExchangeRecord> getExchangeRecordlist(String exchangeRecordStoreName) {
		// loads all files in path with extension & lists them
		logger.debug("exchangeRecordStoreName  = " + exchangeRecordStoreName);
		File folder = new File(path + exchangeRecordStoreName + "/");
		File[] listOfFiles = folder.listFiles();
		ArrayList<ExchangeRecord> recordList = new ArrayList<ExchangeRecord>();
		if(listOfFiles != null){
			logger.debug("listOfFiles.size = " + listOfFiles.length);
			for (File file : listOfFiles) {
				if (file.isFile() && file.getName().startsWith(EXCHANGE_FILE_PREFIX)) {
					logger.debug("file name : " + file.getName());
					if (file.getName().endsWith(EXCHANGE_FILE_EXTENSION)) {
						String id = file.getName().substring(file.getName().lastIndexOf("_")+1, file.getName().lastIndexOf("."));
						logger.debug("record id : " + id);
						try {
							recordList.add(load(exchangeRecordStoreName, id));
						} catch (Exception ex) {
							logger.debug(ex);
						}
					}
				}
			}
		} else {
			logger.debug("listOfFiles is null, no records to return !");
		}
		return recordList;
	}

	@Override
	public List<ExchangeRecordStore> getExchangeRecordStorelist() {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		logger.debug("listOfFiles.size = " + listOfFiles.length);
		ArrayList<ExchangeRecordStore> storeList = new ArrayList<ExchangeRecordStore>();
		for (File file : listOfFiles) {
			if (file.isDirectory()) {
				storeList.add(new ExchangeRecordStore(file.getName()));
			}
		}
		return storeList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.easysoa.sca.records.ExchangeRecordStore#save(org.easysoa.sca.records
	 * .ExchangeRecord)
	 */
	@Override
	public String save(ExchangeRecord exchangeRecord) throws Exception {
		return save(exchangeRecord, path);
	}

	/**
	 * 
	 * @param exchangeRecord
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private String save(ExchangeRecord exchangeRecord, String recordPath) throws IOException{
		File inMEssageFile = new File(recordPath + IN_MESSAGE_FILE_PREFIX + exchangeRecord.getExchange().getExchangeID() + EXCHANGE_FILE_EXTENSION);
		File outMessageFile = new File(recordPath + OUT_MESSAGE_FILE_PREFIX + exchangeRecord.getExchange().getExchangeID() + EXCHANGE_FILE_EXTENSION);
		File exchangeFile = new File(recordPath + EXCHANGE_FILE_PREFIX + exchangeRecord.getExchange().getExchangeID() + EXCHANGE_FILE_EXTENSION);
		FileWriter inMessFw = new FileWriter(inMEssageFile);
		FileWriter outMessFw = new FileWriter(outMessageFile);
		FileWriter exchangeFw = new FileWriter(exchangeFile);
	    try{
	    	inMessFw.write(JSONObject.fromObject(exchangeRecord.getInMessage()).toString());
	    	outMessFw.write(JSONObject.fromObject(exchangeRecord.getOutMessage()).toString());
	    	exchangeFw.write(JSONObject.fromObject(exchangeRecord.getExchange()).toString());
	    }
	    finally{
	    	inMessFw.close();
	    	outMessFw.close();
	    	exchangeFw.close();
	    }
		return exchangeRecord.getExchange().getExchangeID();		
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.easysoa.sca.records.ExchangeRecordStore#save(com.openwide.easysoa.run.Run)
	 */
	@Override
	public String save(Run run) throws Exception {
		// Create the run folder
		File runFolder = new File(path + "/" + run.getName());
		runFolder.mkdir();
		for (ExchangeRecord record : run.getExchangeRecordList()) {
			save(record, path + "/" + run.getName() + "/");
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.easysoa.sca.records.ExchangeRecordStore#load(java.lang.String)
	 */
	@Override
	public ExchangeRecord load(String exchangeStoreName, String recordID) throws Exception {
		ExchangeRecord record = new ExchangeRecord();
		HashMap<String, Class> classMap = new HashMap<String, Class>();
		classMap.put("exchangeType", ExchangeType.class);
		classMap.put("queryString", QueryString.class);
		classMap.put("queryParams", QueryParam.class);
		classMap.put("postData", PostData.class);
		classMap.put("headers", Headers.class);
		classMap.put("headerList", Header.class);
		classMap.put("customFields", CustomFields.class);
		classMap.put("customFieldList", CustomField.class);
		classMap.put("messageContent", MessageContent.class);
		record.setExchange((Exchange) JSONObject.toBean((JSONObject) readJSONFile(path + exchangeStoreName + "/" + EXCHANGE_FILE_PREFIX + recordID + EXCHANGE_FILE_EXTENSION), Exchange.class, classMap));
		record.setInMessage((InMessage) JSONObject.toBean((JSONObject) readJSONFile(path + exchangeStoreName + "/" + IN_MESSAGE_FILE_PREFIX + recordID + EXCHANGE_FILE_EXTENSION), InMessage.class, classMap));
		record.setOutMessage((OutMessage) JSONObject.toBean((JSONObject) readJSONFile(path + exchangeStoreName + "/" + OUT_MESSAGE_FILE_PREFIX + recordID + EXCHANGE_FILE_EXTENSION), OutMessage.class, classMap));
		return record;
	}

	/**
	 * Read a JSON file and returns a <code>JSONObject</code>
	 * @param filePath The exchange store name where to read files
	 * @return A <code>JSONObject</code>
	 * @throws Exception If a problem occurs
	 */
	private JSONObject readJSONFile(String filePath) throws Exception {
		File file = new File(filePath);
		FileReader fr = new FileReader(file);
		JSONObject returnObject;
		try {
			CharBuffer buffer = CharBuffer.allocate(256);
			StringBuffer jsonStringBuffer = new StringBuffer();
			while (fr.ready()) {
				fr.read(buffer);
				jsonStringBuffer.append(buffer.rewind());
			}
			jsonStringBuffer.trimToSize();
			returnObject = (JSONObject) JSONSerializer.toJSON(jsonStringBuffer.toString());	
		}
		finally {
			if(fr != null){
				fr.close();
			}
		}
		return returnObject;
	}
	
	/**
	 * Return the path where the exchange records are stored
	 * 
	 * @return A <code>String</code> representing the path
	 */
	public String getStorePath() {
		return this.path;
	}

	/**
	 * Set the path where the exchange record will be stored
	 * 
	 * @param path
	 */
	public void setStorePath(String path) {
		this.path = path;
	}

	@Override
	public TemplateFieldSuggestions getTemplateFieldSuggestions(String fileName) throws Exception {
		// TODO remove this hard coded path
		logger.debug("loading template :" + fileName);
		//System.out.println("Template to use : " + System.getProperty("user.dir") + "/src/test/resources/templates/" + templateFieldSuggestionsName + ".fld");
		HashMap<String, Class> classMap = new HashMap<String, Class>();
		classMap.put("templateFields", TemplateField.class);
		return (TemplateFieldSuggestions) JSONObject.toBean(readJSONFile(System.getProperty("user.dir") +  "/src/test/resources/templates/" + fileName + SUGGESTIONS_FILE_EXTENSION), TemplateFieldSuggestions.class, classMap);
	}
	
	/**
	 * Returns the name list of the recorded templates
	 * @return A <code>List</code> containing the name of the template files
	 */
	public List<String> getTemplateList(){
		ArrayList<String> templateFileList = new ArrayList<String>();
		// TODO : remove this hard coded path
		File folder = new File("target/classes/webContent/templates/");
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
