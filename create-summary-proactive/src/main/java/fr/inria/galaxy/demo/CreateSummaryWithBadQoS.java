package fr.inria.galaxy.demo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Random;

import javax.jws.WebService;

@WebService(endpointInterface = "fr.inria.galaxy.demo.SummaryService", serviceName = "SummaryService")
public class CreateSummaryWithBadQoS implements SummaryService {
	
	private static final int WAIT_TIME_IN_SECONDS = 3;
	private static int launchNumber = 0;

	private static final String FILE_PREFIX = "CreateSummary_";
	private static final String NEW_LINE = System.getProperty("line.separator");

	public String createSummary(String city, String country, String meteo, double exchangeRate, String chosenPhrase, String translatedPhrase) {
		
		// Introduce degradation of service response time... 
		int waitTime = (new Random()).nextInt(50);
		launchNumber = ++launchNumber % 10;
		if (4 >= launchNumber)
			waitTime += WAIT_TIME_IN_SECONDS * 1000;
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			// Nothing to do
		}
		
		saveResult(city, country,  meteo, exchangeRate, chosenPhrase, translatedPhrase);

		StringBuffer result = new StringBuffer();
		result.append("<div id='result'>");
		result.append("<b>City:</b>" + city + "<br/>");
		result.append("<b>Meteo:</b>" + meteo + "</br>");
		result.append("<b>Exchange rate:</b>" + exchangeRate + "</br>");
		result.append("<b>Chosen phrase:</b>" + chosenPhrase + "</br>");
		result.append("<b>Translated phrase:</b>" + translatedPhrase + "</br>");
		result.append("</div>");

		return result.toString();
	}

	private void saveResult(String city, String country, String meteo, double exchangeRate, String chosenPhrase, String translatedPhrase) {
		File f = new File(FILE_PREFIX + new Date().getTime());
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
			out.append("City: " + city + NEW_LINE);
			out.append("Country: "+ country + NEW_LINE);
			out.append("Meteo: " + meteo + NEW_LINE);
			out.append("Exchange rate: " + exchangeRate + NEW_LINE);
			out.append("Chosen phrase: " + chosenPhrase + NEW_LINE);
			out.append("Translated phrase: " + chosenPhrase + NEW_LINE);

			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
