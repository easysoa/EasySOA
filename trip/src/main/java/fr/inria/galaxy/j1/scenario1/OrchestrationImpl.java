package fr.inria.galaxy.j1.scenario1;

import net.webservicex.GlobalWeatherSoap;

import org.osoa.sca.ServiceUnavailableException;
import org.osoa.sca.annotations.Reference;

import com.microsofttranslator.api.v1.soap.LanguageService;

import de.daenet.webservices.currencyserver.CurrencyServerWebServiceSoap;

/**
 * Component used to simulate a BPEL orchestration.
 * 
 * @author Christophe Demarey
 */
public class OrchestrationImpl implements Trip {
	public static final String SEA = "SEA";
	public static final String MOUNTAIN = "MOUNTAIN";
	public static final String ART = "ART";

	/** The reference to the meteo service */
	@Reference(name = "Check_Meteo_genService_ref")
	protected GlobalWeatherSoap meteoService;

	/** The reference to the exchange service */
	@Reference(name = "Get_Exchange_Rate_genService_ref")
	protected CurrencyServerWebServiceSoap exchangeService;

	/** The reference to the translation service */
	@Reference(name = "Translate_Phrase_genService_ref")
	protected LanguageService translationService;

	/** The reference to the Create Summary service */
	@Reference(name = "Create_Summary_genService_ref", required = false)
	protected ISummaryServicePortType summaryService;

	/**
	 * Choose a business sentence according to inputs.
	 * 
	 * @param activity
	 *            The activity between ART, SEE and MOUNTAIN.
	 * @param sunny
	 *            True if the weather is sunny.
	 * @param cheap
	 *            True if the exchange rate is below the threshold.
	 * 
	 * @return a business sentence useful for the traveler.
	 */
	public String getBusinessSentence(String activity, boolean sunny,
			boolean cheap) {
		if (activity.compareToIgnoreCase(ART) == 0) {
			if (cheap)
				return "Aller au restaurant du Musée";
			else
				return "Aller manger au Mc Donald";
		} else if (activity.compareToIgnoreCase(SEA) == 0) {
			if (cheap) {
				if (sunny)
					return "Aller manger des coquillages de mer au restaurant de la plage";
				else
					return "Aller manger des coquillages de mer au restaurant du Casino";
			} else {
				if (sunny)
					return "Aller au bar de la plage et manger des frites";
				else
					return "Aller dans un Bistrot manger des frites";
			}
		} else if (activity.compareToIgnoreCase(MOUNTAIN) == 0) {
			if (cheap) {
				if (sunny)
					return "Aller au restaurant en terrasse d'altitude et manger une fondue";
				else
					return "Manger une fondue dans un chalet.";
			} else {
				if (sunny)
					return "Aller au restaurant en terrasse d'altitude et manger des frites";
				else
					return "Aller manger des frites dans un chalet";
			}
		}

		return "Non trouvé";
	}

	public String process(String activity, String userSentence,
			double rateTreshold) {
		double exchangeRate;
		String city = null;
		String meteo = null;
		String chosenPhrase = null;
		String country = "France";
		String translatedPhrase = "";

		// Test sea, art, mountain
		if (activity.compareTo("SEA") == 0)
			city = "Nice";
		else if (activity.compareTo("ART") == 0)
			city = "Lille";
		else
			city = "Grenoble";

		// get temperature and conditions
		try {
			meteo = meteoService.getWeather(city, country);
		} catch (ServiceUnavailableException e) {
			meteo = e.getMessage();
		}
		System.err.println("Meteo: " + meteo);
		// isExpensive ?
		exchangeRate = exchangeService.getCurrencyValue("3", "EUR", "USD");
		//FIXME Hack because currency service returns always NaN
		exchangeRate = 10.85;
		System.err.println("Exchange rate: " + exchangeRate);
		// choose sentence
		chosenPhrase = getBusinessSentence(activity, meteo.contains("clear"),
				exchangeRate < rateTreshold);
		System.err.println("Chosen phrase: " + chosenPhrase);
		if (chosenPhrase == null || "".equals(chosenPhrase)) {
			chosenPhrase = "My name is Duke! I've just been to JavaOne.";
		}
		/* appID: BD061A8446F9FA67F9CD39B278237C98599FAFEA */
		String appID = "BD061A8446F9FA67F9CD39B278237C98599FAFEA";
		// translate business sentence
		if (userSentence != null)
	    translatedPhrase = userSentence + " => " + translationService.translate(appID, userSentence,
	        "EN", "FR");
		chosenPhrase += " => " + translationService.translate(appID, chosenPhrase,
				"FR", "EN");
		System.err.println("Translated phrase: " + translatedPhrase);
		// compute summary
		/*String summary = summaryService.summarize(city, country, meteo,
				exchangeRate, chosenPhrase, translatedPhrase);
		System.err.println("[OrchestrationImpl] Summary: \n" + summary);*/
		//return summary;
		return "" + exchangeRate;

	}
}
