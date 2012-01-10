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

package org.openwide.easysoa.test.mock.twittermock;

import java.util.ArrayList;
import java.util.Random;

import org.openwide.easysoa.test.util.Utilities;

/**
 * Implementation of Twitter mock
 * @author jguillemotte
 *
 */
public class TwitterMockImpl implements TwitterMock {

	@Override
	public String returnUsersShow(String user) {
		if(user.endsWith(".json")){
			return Utilities.readResponseFile("src/test/resources/twitterMockMessages/usersShowDefaultResponse.json");
		} else {
			return Utilities.readResponseFile("src/test/resources/twitterMockMessages/usersShowDefaultResponse.xml");
		}
	}

	@Override
	public String returnStatusesFriends(String user) {
		return Utilities.readResponseFile("src/test/resources/twitterMockMessages/statusesFriendsDefaultResponse.xml");
	}

	@Override
	public String returnStatusesFollowers(String user) {
		return Utilities.readResponseFile("src/test/resources/twitterMockMessages/statusesFollowersDefaultResponse.xml");
	}

	@Override
	public String returnLastTweet(String user) {
		StringBuffer jsonResponseBuffer = new StringBuffer();
		jsonResponseBuffer.append("{\"user\":\"");
		jsonResponseBuffer.append(user);
		jsonResponseBuffer.append("\"");
		jsonResponseBuffer.append(",\"lastTweet\":\"This is the last tweet\"}");
		return jsonResponseBuffer.toString();
	}

	@Override
	public String returnSeveralRecentTweet(String user, int tweetNumbers) {
		Random generator = new Random();
		ArrayList<String> tweets = new ArrayList<String>();
		tweets.add("{\"tweet\": \"The last tweet\"}");
		tweets.add("{\"tweet\": \"Not the first tweet but not the last\"}");
		tweets.add("{\"tweet\": \"Another tweet\"}");
		tweets.add("{\"tweet\": \"Maybe the last tweet\"}");
		StringBuffer jsonResponseBuffer = new StringBuffer();
		jsonResponseBuffer.append("{\"user\":\"");
		jsonResponseBuffer.append(user);
		jsonResponseBuffer.append("\",\"tweets\": [");
		for(int i=0; i<tweetNumbers; i++){
			jsonResponseBuffer.append(tweets.get(generator.nextInt(4)));
			if(i<tweetNumbers){
				jsonResponseBuffer.append(",");	
			}
		}
		jsonResponseBuffer.append("]}");
		return jsonResponseBuffer.toString();
	}

	@Override
	public String postNewTweet(String user, String tweet) {
		return "{\"status\":\"ok\",\"message\":\"New tweet succesfully posted\"}";
	}
	
}
