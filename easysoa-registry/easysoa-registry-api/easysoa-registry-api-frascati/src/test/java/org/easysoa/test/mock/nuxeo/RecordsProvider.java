package org.easysoa.test.mock.nuxeo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easysoa.records.ExchangeRecord;

public interface RecordsProvider {
	
	public List<ExchangeRecord> getRecords();
	
	
}
