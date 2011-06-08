package com.openwide.easysoa.monitoring.apidetector;

import java.util.ArrayList;

/**
 * URL mock storage to test the detection of API/Services
 * 
 * @author jguillemotte
 *
 */
public class UrlMock {

	/**
	 * 
	 */
	private ArrayList<String> oldTestSet;
	
	private ArrayList<String> newTestSet;
	
	private ArrayList<String> twitterTest;

	private String prevDvdId = "";
	private String prevBookId = "";
	
	/**
	 * Init the mocker
	 */
	public UrlMock(){
		oldTestSet = new ArrayList<String>();
		oldTestSet.add("http://www.freebooks.org/library/getBook/15214-2584-44554");
		oldTestSet.add("http://www.freebooks.org/library/getBook/15487-9985-87751");
		oldTestSet.add("http://www.freebooks.org/library/getBook/12997-5799-73346");
		oldTestSet.add("http://www.freebooks.org/library/getDvd/99547-8854-26635");
		oldTestSet.add("http://www.freebooks.org/library/getDvd/99547-8854-39584");
		oldTestSet.add("http://www.tests.org/maven/PerformTest/ZEDF84558");
		oldTestSet.add("http://www.tests.org/maven/PerformTest/ZZED7584");
		oldTestSet.add("http://www.tests.org/maven/PerformTest/854DDS");
		oldTestSet.add("http://www.tests.org/maven/PerformTest/DEEED584");
		oldTestSet.add("http://www.tests.org/maven/PerformTest/ZZS7584");
		
		//
		newTestSet = new ArrayList<String>();
		newTestSet.add("http://www.imedia.com/shop/getBookList/page1");
		newTestSet.add("http://www.imedia.com/shop/getBookList/page2");
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/addBookToBasket/{prevBookId}"));
		newTestSet.add("http://www.imedia.com/shop/getBookList/page3");
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		newTestSet.add("http://www.imedia.com/shop/getBookList/page4");
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getBook/{bookId}"));
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/addBookToBasket/{prevBookId}"));
		newTestSet.add("http://www.imedia.com/shop/getDvdList/page1");
		newTestSet.add("http://www.imedia.com/shop/getDvdList/page2");
		newTestSet.add("http://www.imedia.com/shop/getDvdList/page2");
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getDvd/{dvdId}"));
		newTestSet.add("http://www.imedia.com/shop/getDvdList/page4");
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getDvd/{dvdId}"));
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getDvd/{dvdId}"));
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getDvd/{dvdId}"));
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/getDvd/{dvdId}"));
		newTestSet.add(generateRandomIDs("http://www.imedia.com/shop/addDvdToBasket/{prevDvdId}"));
	
	
		
		twitterTest = new ArrayList<String>();
		twitterTest.add("http://api.twitter.com/1/users/show/FR3Aquitaine.xml");
		twitterTest.add("http://api.twitter.com/1/users/show/EasySoaTest.xml");
		twitterTest.add("http://api.twitter.com/1/users/show/FR3Aquitaine.json");
		twitterTest.add("http://api.twitter.com/1/users/show/FR3Bourgone.xml");
		twitterTest.add("http://api.twitter.com/1/users/show/FR3Bourgogne.json");
		twitterTest.add("http://api.twitter.com/1/users/show/truckblogfr.xml");
		twitterTest.add("http://api.twitter.com/1/users/show/OliverTweett.xml");
		twitterTest.add("http://api.twitter.com/1/users/show/Developpez.xml");
		twitterTest.add("http://api.twitter.com/1/users/show/europe_camions.xml");
		twitterTest.add("http://api.twitter.com/1/users/show/OliverTweett.json");
		twitterTest.add("http://api.twitter.com/1/users/show/FR3Aquitaine.xml");
		twitterTest.add("http://api.twitter.com/1/statuses/friends/oliverTweett.xml?cursor=-1");
		twitterTest.add("http://api.twitter.com/1/statuses/friends/FR3Aquitaine.xml?cursor=-1");
		twitterTest.add("http://api.twitter.com/1/statuses/friends/europe_camions.xml?cursor=-1");
		twitterTest.add("http://api.twitter.com/1/statuses/friends/Developpez.xml?cursor=-1");
		twitterTest.add("http://api.twitter.com/1/statuses/friends/FR3Bourgone.xml?cursor=-1");
		twitterTest.add("http://api.twitter.com/1/statuses/friends/EasySoaTest.xml?cursor=-1");
		twitterTest.add("http://api.twitter.com/1/statuses/followers/europe_camions.xml?cursor=-1");
		twitterTest.add("http://api.twitter.com/1/statuses/followers/Developpez.xml?cursor=-1");
		twitterTest.add("http://api.twitter.com/1/statuses/followers/FR3Aquitaine.xml?cursor=-1");
		twitterTest.add("http://api.twitter.com/1/statuses/followers/oliverTweett.xml?cursor=-1");
		twitterTest.add("http://api.twitter.com/1/statuses/followers/Developpez.xml?cursor=-1");
		twitterTest.add("http://api.twitter.com/1/statuses/followers/FR3Bourgone.xml?cursor=-1");
		twitterTest.add("http://api.twitter.com/1/statuses/followers/EasySoaTest.xml?cursor=-1");
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<String> geturlData(){
		return newTestSet;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getTwitterUrlData(){
		return twitterTest;
	}
	
	/*
	 * 
	 */
	private String generateRandomIDs(String template){
		if(template.contains("{bookId}")){
			prevBookId = "" + Math.round(Math.random() * 1000);
			//System.out.println("+++++ BookId = " + prevBookId);
			return template.replace("{bookId}", prevBookId);
		} else if(template.contains("{dvdId}")){
			prevDvdId = "" + Math.round(Math.random() * 1000);
			//System.out.println("+++++ DvdId = " + prevDvdId);
			return template.replace("{dvdId}", prevDvdId);
		} else if(template.contains("{prevBookId}")){
			return template.replace("{prevBookId}", prevBookId);
		} else {
			return template.replace("{prevDvdId}", prevDvdId);
		}
	}
	
}
