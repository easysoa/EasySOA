package org.openwide.easysoa.test;

import static org.junit.Assert.assertEquals;

import java.util.UUID;
import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.ExchangeRecordStore;
import org.easysoa.records.ExchangeRecordStoreArray;
import org.easysoa.records.ExchangeRecordStoreFactory;
import org.junit.Test;

import com.openwide.easysoa.message.InMessage;

public class ExchangeRecordStoreTest {

	// Logger
	private static Logger logger = Logger.getLogger(ExchangeRecordStoreTest.class.getName());

	@Test
    public void saveAndReadRecordsTest() throws Exception {
    	ExchangeRecordStore erfs = ExchangeRecordStoreFactory.createExchangeRecordStore();
    	ExchangeRecord exchangeRecord;
    	ExchangeRecordStoreArray recordList = new ExchangeRecordStoreArray("TEST Env");
    	String id = UUID.randomUUID().toString();
    	recordList.add(new ExchangeRecord(id, new InMessage("POST", "http://test.easysoa.org")));
    	// Only the first one for the tests
    	for(ExchangeRecord record : recordList){
    		try{
    			id = erfs.save(record);
    			logger.debug("record ID = " + id);
    			exchangeRecord = erfs.load(id);
    			logger.debug("Request content from saved record : " + exchangeRecord.getExchangeID());
    			assertEquals(id, exchangeRecord.getExchangeID());
    		}
    		catch(Exception ex){
    			ex.printStackTrace();
    		}
    	}
    }
	
}
