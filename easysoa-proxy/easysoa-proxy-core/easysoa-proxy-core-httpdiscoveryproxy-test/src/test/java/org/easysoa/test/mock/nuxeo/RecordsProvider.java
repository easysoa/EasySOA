package org.easysoa.test.mock.nuxeo;

import java.util.List;

import org.easysoa.records.ExchangeRecord;

/**
 * 
 * TODO COPIED FROM easysoa-registry-api-frascati, REFACTOR & SHARE IT
 * 
 * provides access to a list of (recorded) exchange records
 * 
 * @author fntangke
 *
 */
public interface RecordsProvider {
	
	public List<ExchangeRecord> getRecords();
	
	
}
