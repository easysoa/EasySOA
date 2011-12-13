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
import java.util.List;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.ExchangeRecordStore;
import org.easysoa.records.ExchangeRecordStoreManager;

import com.openwide.easysoa.run.Run;

/**
 * Take a ExchangeRecordStoreArray and store the Exchange records as files in the file system
 * @author jguillemotte
 *
 */
public class ExchangeRecordFileStore implements ExchangeRecordStoreManager {

	// TODO : The Exchange records must be stored in a human readable format : no serialization but JSON instead.
	// Maybe is a good idea to work with HAR format with custom extensions to store ID's, request times ....
	// See if it is necessary to store Exchange records sets at once in the same file (HAR can do that) instead of storing one record in one file.
	
	// If we use HAR, we need to made a class to map a servletRequest/Response to HAR Message Object. HAR log are stored with JSON format.
	// When reading a stored HAR file, parsing from JSON is automatic, just have to map HAR message object informations to servlet object.

	// Logger
	private static Logger logger = Logger.getLogger(ExchangeRecordFileStore.class.getName());	
	
	public final static String FILE_EXTENSION = ".json";
	//public final static String FILE_PREFIX = "_";
	
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
	 * @param path Path where the Exchange record will be stored
	 */
	public ExchangeRecordFileStore(String path) {
		this.path = path;
	}

	/* (non-Javadoc)
	 * @see org.easysoa.sca.records.ExchangeRecordStore#list()
	 */	
	@Override
	public List<ExchangeRecord> getExchangeRecordlist(String exchangeRecordStoreName) {
		// loads all files in path with extension & lists them
    	//File folder = new File(path + FILE_PREFIX + exchangeRecordStoreName + "/");
		File folder = new File(path + exchangeRecordStoreName + "/");
    	File[] listOfFiles = folder.listFiles();
    	logger.debug("listOfFiles.size = " + listOfFiles.length);
    	ArrayList<ExchangeRecord> recordList = new ArrayList<ExchangeRecord>();
    	for (File file : listOfFiles) {
            if (file.isFile()) {
            	logger.debug("file name : " + file.getName());
                if (file.getName().endsWith(FILE_EXTENSION)) {
                	String id = file.getName().substring(0, file.getName().lastIndexOf("."));
                	logger.debug("record id : " + id);
                	try {
						recordList.add(load(exchangeRecordStoreName, id));
					} catch (Exception ex) {
						logger.debug(ex);
					}
    	        }
    	    }
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
            //if (file.isDirectory() && file.getName().startsWith(FILE_PREFIX)) {
    		if (file.isDirectory()) {
            	storeList.add(new ExchangeRecordStore(file.getName()));
            }
    	}
		return storeList;
	}	
	
	/* (non-Javadoc)
	 * @see org.easysoa.sca.records.ExchangeRecordStore#save(org.easysoa.sca.records.ExchangeRecord)
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
		File recordFile = new File(recordPath + exchangeRecord.getExchangeID() + FILE_EXTENSION);
		FileWriter fw = new FileWriter(recordFile); 
	    try{
	    	JSONObject jObject = JSONObject.fromObject(exchangeRecord);	    	
	    	fw.write(jObject.toString());
	    }
	    finally{
	    	fw.close();
	    }
		return exchangeRecord.getExchangeID();		
	}
	
	/* (non-Javadoc)
	 * @see org.easysoa.sca.records.ExchangeRecordStore#save(com.openwide.easysoa.run.Run)
	 */
	@Override
	public String save(Run run) throws Exception{
		// Create the run folder
		//File runFolder = new File(path + "/" + FILE_PREFIX + run.getName());
		File runFolder = new File(path + "/" + run.getName());
		runFolder.mkdir();
		for(ExchangeRecord record : run.getExchangeRecordList()){
			save(record, path + "/" + run.getName() + "/");
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.easysoa.sca.records.ExchangeRecordStore#load(java.lang.String)
	 */
	@Override
	public ExchangeRecord load(String storeName, String recordID) throws Exception {
		ExchangeRecord record = null;
		File recordFile = new File(path + storeName + "/" + recordID + FILE_EXTENSION);
		FileReader fr = new FileReader(recordFile);
		CharBuffer buffer = CharBuffer.allocate(256);
		StringBuffer jsonStringBuffer = new StringBuffer();
		try {
			while(fr.ready()){
				fr.read(buffer);
				jsonStringBuffer.append(buffer.rewind());
			}
			jsonStringBuffer.trimToSize();
			JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(jsonStringBuffer.toString()); 
			record = (ExchangeRecord) JSONObject.toBean(jsonObject, ExchangeRecord.class);
		}
		finally {
			fr.close();
		}
		return record;
	}
	
	/**
	 * Return the path where the exchange records are stored
	 * @return A <code>String</code> representing the path
	 */
	public String getStorePath(){
		return this.path;
	}
	
	/**
	 * Set the path where the exchange record will be stored
	 * @param path
	 */
	public void setStorePath(String path){
		this.path = path;
	}

}
