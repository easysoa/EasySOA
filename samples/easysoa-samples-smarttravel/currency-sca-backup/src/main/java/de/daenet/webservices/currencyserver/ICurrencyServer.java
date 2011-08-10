package de.daenet.webservices.currencyserver;

public interface ICurrencyServer {

	double getDollarValue(String provider, String currency);

	String getProviderTimestamp(String providerId, String provider);

	String getProviderList();

	DataSet getDataSet(String provider);

	String getXmlStream(String provider);

	double getCurrencyValue(String provider, String srcCurrency, String dstCurrency);

	String getProviderDescription(String provider);
}
