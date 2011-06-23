package de.daenet.webservices.currencyserver;

import java.util.Random;

import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

@Scope("COMPOSITE")
@Service(interfaces={de.daenet.webservices.currencyserver.ICurrencyServer.class})
public final class CurrencyServerImpl implements ICurrencyServer {
	// ////////////////////////////////////////////////////
	// Fields
	// ////////////////////////////////////////////////////

	private static ICurrencyServer instance = new CurrencyServerImpl();
	private Random rnd;

	private enum Currency {
		EUR, USD, UNKNOWN
	};

	private static final double EURO_USD_AVERAGE_RATE = 1.4764;
	private static final double USD_EURO_AVERAGE_RATE = 0.677323219;
	private static final double RATE_MAX_VARIATION = 0.3;

	// ////////////////////////////////////////////////////
	// Constructor
	// ////////////////////////////////////////////////////

	private CurrencyServerImpl() {
		rnd = new Random();
	}

	public static ICurrencyServer getInstance() {
		return instance;
	}

	// ////////////////////////////////////////////////////
	// CurrencyServer methods
	// ////////////////////////////////////////////////////

	public double getCurrencyValue(String provider, String srcCurrency, String dstCurrency) {
		Currency src = strToCurrency(srcCurrency);
		Currency dst = strToCurrency(dstCurrency);

		if (Currency.UNKNOWN == src || Currency.UNKNOWN == dst) {
			return 0;
		}

		if (src.equals(dst)) {
			return 1;
		}

		if (Currency.EUR == src && Currency.USD == dst) {
			return addRandomNoise(EURO_USD_AVERAGE_RATE, RATE_MAX_VARIATION);
		}

		if (Currency.USD == src && Currency.EUR == dst) {
			return addRandomNoise(USD_EURO_AVERAGE_RATE, RATE_MAX_VARIATION);
		}

		// should never happen
		return 0;
	}

	public DataSet getDataSet(String provider) {
		// TODO Auto-generated method stub
		return null;
	}

	public double getDollarValue(String provider, String currency) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getProviderDescription(String provider) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProviderList() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProviderTimestamp(String providerId, String provider) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getXmlStream(String provider) {
		// TODO Auto-generated method stub
		return null;
	}

	// ////////////////////////////////////////////////////
	// Private methods
	// ////////////////////////////////////////////////////

	private Currency strToCurrency(String str) {
		Currency c;
		try {
			c = Currency.valueOf(str);
		} catch (Exception e) {
			c = Currency.UNKNOWN;
		}

		return c;
	}

	private double addRandomNoise(double centerValue, double maxNoise) {
		double noise = rnd.nextDouble() * maxNoise;
		boolean negatifNoise = rnd.nextBoolean();

		if (negatifNoise)
			noise = -noise;

		return centerValue + noise;
	}

}
