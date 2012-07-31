/**
 * EasySOA Proxy
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
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.test.util;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * URL mock storage to test the detection of API/Services
 * 
 * @author jguillemotte
 *
 */
public class UrlMock {

	// Add a test set for REST requests with parameters (path, query, content ...) :
	// 
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(UrlMock.class.getName());	
	
	//
	private ArrayList<String> freebooksTestSet;
	//
	private ArrayList<String> iMediaTestSet;
	//
	private ArrayList<String> twitterTestSet;
	//
	private ArrayList<String> citiesTestSet;
	
	/**
	 * Variables used by the iMedia data set at init
	 */
	private String prevDvdId = "";
	private String prevBookId = "";
	
	/**
	 * Init the mocker
	 */
	public UrlMock(){
		// Old data test set
		freebooksTestSet = new ArrayList<String>();
		freebooksTestSet.add("http://www.freebooks.org/library/getBook/15214-2584-44554");
		freebooksTestSet.add("http://www.freebooks.org/library/getBook/15487-9985-87751");
		freebooksTestSet.add("http://www.freebooks.org/library/getBook/12997-5799-73346");
		freebooksTestSet.add("http://www.freebooks.org/library/getDvd/99547-8854-26635");
		freebooksTestSet.add("http://www.freebooks.org/library/getDvd/99547-8854-39584");
		freebooksTestSet.add("http://www.tests.org/maven/PerformTest/ZEDF84558");
		freebooksTestSet.add("http://www.tests.org/maven/PerformTest/ZZED7584");
		freebooksTestSet.add("http://www.tests.org/maven/PerformTest/854DDS");
		freebooksTestSet.add("http://www.tests.org/maven/PerformTest/DEEED584");
		freebooksTestSet.add("http://www.tests.org/maven/PerformTest/ZZS7584");
		
		// imedia (fictive) data test set
		iMediaTestSet = new ArrayList<String>();
		iMediaTestSet.add("http://www.imedia.com/shop/getBookList/page1");
		iMediaTestSet.add("http://www.imedia.com/shop/getBookList/page2");
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/addBookToBasket/{prevBookId}"));
		iMediaTestSet.add("http://www.imedia.com/shop/getBookList/page3");
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		iMediaTestSet.add("http://www.imedia.com/shop/getBookList/page4");
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/addBookToBasket/{prevBookId}"));
		iMediaTestSet.add("http://www.imedia.com/shop/getDvdList/page1");
		iMediaTestSet.add("http://www.imedia.com/shop/getDvdList/page2");
		iMediaTestSet.add("http://www.imedia.com/shop/getDvdList/page2");
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getDvd/{dvdId}"));
		iMediaTestSet.add("http://www.imedia.com/shop/getDvdList/page4");
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getDvd/{dvdId}"));
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getDvd/{dvdId}"));
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getDvd/{dvdId}"));
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getDvd/{dvdId}"));
		iMediaTestSet.add(generateRandomIDs("http://www.imedia.com/shop/addDvdToBasket/{prevDvdId}"));
	}

	/**
	 * To get the freebooks URL data set
	 * @return The freebooks URL data set
	 */
	public ArrayList<String> getFreebooksUrlData(){
		return freebooksTestSet;
	}

	/**
	 * To get the iMedia URL data set
	 * @return The iMedia URL data set
	 */
	public ArrayList<String> getIMediaUrlData(){
		return iMediaTestSet;
	}
	
	/**
	 * To get the Twitter URL data set
	 * @return The Twitter URL data set
	 */
	public ArrayList<String> getTwitterUrlData(String baseUrl){
		if(baseUrl == null || "".equals(baseUrl)){
			logger.warn("Invalid baseUrl, using default baseUrl : api.twitter.com");
			baseUrl = "api.twitter.com";
		}
		// Twitter data test set
		twitterTestSet = new ArrayList<String>();
		twitterTestSet.add("http://" + baseUrl + "/1/users/show/FR3Aquitaine.xml");
		twitterTestSet.add("http://" + baseUrl + "/1/users/show/EasySoaTest.xml");
		twitterTestSet.add("http://" + baseUrl + "/1/users/show/FR3Aquitaine.json?id=95379276");
		//twitterTestSet.add("http://" + baseUrl + "/1/users/show/FR3franchecomte.xml");
		//twitterTestSet.add("http://" + baseUrl + "/1/users/show/FR3franchecomte.json?id=95379236");
		//twitterTestSet.add("http://" + baseUrl + "/1/users/show/truckblogfr.xml?id=95379295");
		//twitterTestSet.add("http://" + baseUrl + "/1/users/show/OliverTweett.xml?id=95379285");
		twitterTestSet.add("http://" + baseUrl + "/1/users/show/Developpez.xml?id=95379216");
		//twitterTestSet.add("http://" + baseUrl + "/1/users/show/europe_camions.xml");
		//twitterTestSet.add("http://" + baseUrl + "/1/users/show/OliverTweett.json?id=95379452");
		twitterTestSet.add("http://" + baseUrl + "/1/users/show/FR3Aquitaine.xml");
		//twitterTestSet.add("http://" + baseUrl + "/1/statuses/friends/oliverTweett.xml?cursor=-1");
		//twitterTestSet.add("http://" + baseUrl + "/1/statuses/friends/FR3Aquitaine.xml?cursor=-1");
		//twitterTestSet.add("http://" + baseUrl + "/1/statuses/friends/europe_camions.xml?cursor=-1");
		twitterTestSet.add("http://" + baseUrl + "/1/statuses/friends/Developpez.xml?cursor=-1");
		//twitterTestSet.add("http://" + baseUrl + "/1/statuses/friends/FR3franchecomte.xml?cursor=-1");
		twitterTestSet.add("http://" + baseUrl + "/1/statuses/friends/EasySoaTest.xml?cursor=-1");
		//twitterTestSet.add("http://" + baseUrl + "/1/statuses/followers/europe_camions.xml?cursor=-1");
		//twitterTestSet.add("http://" + baseUrl + "/1/statuses/followers/Developpez.xml?cursor=-1");
		//twitterTestSet.add("http://" + baseUrl + "/1/statuses/followers/FR3Aquitaine.xml?cursor=-1");
		//twitterTestSet.add("http://" + baseUrl + "/1/statuses/followers/oliverTweett.xml?cursor=-1");
		//twitterTestSet.add("http://" + baseUrl + "/1/statuses/followers/Developpez.xml?cursor=-1");
		//twitterTestSet.add("http://" + baseUrl + "/1/statuses/followers/FR3franchecomte.xml?cursor=-1");
		//twitterTestSet.add("http://" + baseUrl + "/1/statuses/followers/EasySoaTest.xml?cursor=-1");
		return twitterTestSet;
	}

	/**
	 * A cities test set for Meteo Mock
	 * @return An <code>ArrayList</code> of city names
	 */
	public ArrayList<String> getMeteoMockCities(){
		// Cities test set for Meteo Mock
		citiesTestSet = new ArrayList<String>();
		citiesTestSet.add("Lyon");
		citiesTestSet.add("Grenoble");
		citiesTestSet.add("Lille");
		return citiesTestSet;
	}
	
	/**
	 * Replace template with random numbers
	 * @param template The String with the template to replace
	 * @return A String with a replaced value
	 */
	private String generateRandomIDs(String template){
		if(template.contains("{bookId}")){
			prevBookId = "" + Math.round(Math.random() * 1000);
			return template.replace("{bookId}", prevBookId);
		} else if(template.contains("{dvdId}")){
			prevDvdId = "" + Math.round(Math.random() * 1000);
			return template.replace("{dvdId}", prevDvdId);
		} else if(template.contains("{prevBookId}")){
			return template.replace("{prevBookId}", prevBookId);
		} else {
			return template.replace("{prevDvdId}", prevDvdId);
		}
	}
	
}
