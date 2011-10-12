/**
 * EasySOA Samples - Smart Travel
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@groups.google.com
 */

package fr.inria.galaxy.j1.scenario1;


/**
 * @author Tuvalu Public demonstration service interface used for the galaxy
 *         JavaONE travel scenario demo
 */
public interface ISummaryService {

	/**
	 * Computes a summary based on collected "smart" travel data
	 * 
	 * @param city
	 *            destination city chosen for the trip
	 * @param country
	 *            the destination country chosen for the trip
	 * @param meteo
	 *            the weather report
	 * @param exchangeRate
	 *            the exchange rate obtained for the given destination (from
	 *            USD)
	 * @param chosenPhrase
	 *            the phrase that was chosen based on the meteo and exchange
	 *            rate
	 * @param translatedPhrase
	 *            the phrase translated in the language used in the country of
	 *            destination
	 * @return any completion code or meaningful errors
	 */
	public String summarize(String city, String country, String meteo,
			double exchangeRate, String chosenPhrase, String translatedPhrase);
}
