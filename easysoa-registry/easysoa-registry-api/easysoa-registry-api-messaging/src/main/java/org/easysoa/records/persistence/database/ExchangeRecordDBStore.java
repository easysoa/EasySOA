package org.easysoa.records.persistence.database;

import java.util.List;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.ExchangeRecordStore;
import org.easysoa.records.ExchangeRecordStoreManagerItf;

/**
 * Class to store Exchange records in a database
 * To be used in future releases 
 * 
 * @author jguillemotte
 *
 */
public class ExchangeRecordDBStore implements ExchangeRecordStoreManagerItf {

	@Override
	public String save(ExchangeRecord exchangeRecord) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExchangeRecord load(String path, String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ExchangeRecord> getExchangeRecordlist(String exchangeRecordStoreName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ExchangeRecordStore> getExchangeRecordStorelist() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createStore(String storeName) {
		// TODO Auto-generated method stub
	}

}
