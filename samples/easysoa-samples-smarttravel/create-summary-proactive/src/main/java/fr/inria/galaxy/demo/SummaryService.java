package fr.inria.galaxy.demo;

import javax.jws.WebService;

/**
 * @author Tuvalu
 * Public demonstration service interface used for the galaxy JavaONE travel scenario demo
 */
@WebService
public interface SummaryService {


/**
 * Computes a summary based on collected "smart" travel data
 * @param city destination city chosen for the trip
 * @param country the destination country chosen for the trip  
 * @param meteo the weather report
 * @param exchangeRate the exchange rate obtained for the given destination (from USD) 
 * @param chosenPhrase the phrase that was chosen based on the meteo and exchange rate
 * @param translatedPhrase the phrase translated in the language used in the country of destination 
 * @return any completion code or meaningful errors
 */
public String createSummary(String city, String country, String meteo, double exchangeRate, String chosenPhrase, String translatedPhrase);
}
