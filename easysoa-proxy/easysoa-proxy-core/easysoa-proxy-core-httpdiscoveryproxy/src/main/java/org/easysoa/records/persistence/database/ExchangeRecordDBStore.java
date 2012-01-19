package org.easysoa.records.persistence.database;

import java.util.List;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.ExchangeRecordStore;
import org.easysoa.records.ExchangeRecordStoreManager;
import org.easysoa.template.TemplateFieldSuggestions;

import com.openwide.easysoa.run.Run;

/**
 * Class to store Exchange records in a database
 * To be used in future releases 
 * 
 * @author jguillemotte
 *
 */
public class ExchangeRecordDBStore implements ExchangeRecordStoreManager {

	@Override
	public String save(ExchangeRecord exchangeRecord) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Run run) throws Exception {
		// TODO Auto-generated method stub
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
	public TemplateFieldSuggestions getTemplateFieldSuggestions(String storeName, String templateName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createStore(String storeName) {
		// TODO Auto-generated method stub
	}

}
