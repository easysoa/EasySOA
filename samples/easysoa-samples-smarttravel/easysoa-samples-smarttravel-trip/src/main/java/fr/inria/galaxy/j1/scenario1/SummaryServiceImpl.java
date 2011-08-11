package fr.inria.galaxy.j1.scenario1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class SummaryServiceImpl implements ISummaryService {

	private static final String FILE_PREFIX = "CreateSummary_";
	private static final String NEW_LINE = System.getProperty("line.separator");

	public String summarize(String city, String country, String meteo,
			double exchangeRate, String chosenPhrase, String translatedPhrase) {
		saveResult(city, country, meteo, exchangeRate, chosenPhrase,
				translatedPhrase);

		boolean sunOK = meteo.contains("sun") | meteo.contains("clear");

		// Ne connaissant pas le seuil du taux de change pour être considéré
		// comme OK.
		// On deduit si le taux de change est OK en fonction des phrases de
		// réponses
		String[] cheapSentences = { 
				"Go to the altitude terrace restaurant and eating a fondue", 
				"Eat a fondue in a chalet.",
				"Go eat sea shells at the beach restaurant", 
				"Go eat sea shells at the Casino restaurant", 
				"Go to the restaurant at the Museum",
				"Go eat at MC Donald",
				
				"Aller au restaurant du Musée",
				"Aller manger au Mc Donald",
				"Aller manger des coquillages de mer au restaurant de la plage",
				"Aller manger des coquillages de mer au restaurant du Casino",
				"Aller au restaurant en terrasse d'altitude et manger une fondue",
				"Manger une fondue dans un chalet."		
		};
		boolean exchangeRateOK = false;
		for (String s : cheapSentences) {
			if(s.equalsIgnoreCase(chosenPhrase)){
				exchangeRateOK = true;
				break;
			}
		}


		
		StringBuffer result = new StringBuffer();
		result.append("<div id='result'>");
		result.append("<p id='city'>City:<b>" + city + "</b></p><br/>");

		result.append("<div id='meteo'><h2>meteo</h2>");
		result.append("<img height='80' width='80' src='img/" + (sunOK ? "/sunny.png" : "/notsunny.png") + "'/><br>");
		result.append(sunOK ? "<span style='color:green'>Sunny!</span>" : "<span style='color:red'>Not sunny :(</span>");
		result.append("</div>");

		result.append(" <div id='exchangeRate'><h2>exchange rate</h2>");
		result.append("<img height='80' width='80' src='img/" + (exchangeRateOK ? "/cheap.png" : "/expansive.png") + "'/><br>");
		result.append((exchangeRateOK ? "<span style='color:green'>Good!</span>" : "<span style='color:red'>Bad :( </span> ") + " [" + exchangeRate + "]");
		result.append("</div>");

		result.append("<br/><br/><br/><br/><div id='proposition'><h2>Our proposition</h2>");
		result.append("Adviced activity:<b>" + chosenPhrase + "</b><br/>");
		result.append("Your sentence translated in french:<b>" + translatedPhrase + "</b><br/>");
		result.append("<br/></div><br/>");

		result.append("</div><br/>");

		return result.toString();
	}

	private void saveResult(String city, String country, String meteo,
			double exchangeRate, String chosenPhrase, String translatedPhrase) {
		File f = new File(FILE_PREFIX + new Date().getTime());
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(f)));
			out.append("City: " + city + NEW_LINE);
			out.append("Country: " + country + NEW_LINE);
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
