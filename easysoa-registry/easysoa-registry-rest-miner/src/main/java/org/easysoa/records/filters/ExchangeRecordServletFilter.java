package org.easysoa.records.filters;

import org.nuxeo.ecm.core.api.ClientException;

import com.openwide.easysoa.exchangehandler.HttpExchangeHandler;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface ExchangeRecordServletFilter {
	
	void start(HttpExchangeHandler httpExchangeHandler) throws ClientException;
	
	void stop();

}
