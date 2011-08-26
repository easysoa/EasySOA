package org.openwide.easysoa.test.mock.twittermock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Implementation of Twitter mock
 * @author jguillemotte
 *
 */
public class TwitterMockImpl implements TwitterMock {

	@Override
	public String returnUsersShow(String user) {
		if(user.endsWith(".json")){
			return readResponseFile("src/test/resources/twitterMockMessages/usersShowDefaultResponse.json");
		} else {
			return readResponseFile("src/test/resources/twitterMockMessages/usersShowDefaultResponse.xml");
		}
	}

	@Override
	public String returnStatusesFriends(String user) {
		return readResponseFile("src/test/resources/twitterMockMessages/statusesFriendsDefaultResponse.xml");
	}

	@Override
	public String returnStatusesFollowers(String user) {
		return readResponseFile("src/test/resources/twitterMockMessages/statusesFollowersDefaultResponse.xml");
	}

	/**
	 * Read a response file and returns the content 
	 * @return The content of the response file, an error message otherwise
	 */
	private String readResponseFile(String responseFileUri){
		try {
			File responseFile;
			responseFile = new File(responseFileUri);
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(responseFile)));
			StringBuffer response = new StringBuffer();
			while(reader.ready()){
				response.append(reader.readLine());
			}
			return response.toString();	
		}
		catch(Exception ex){
			ex.printStackTrace();
			return "Unable to read default response file ...";
		}
	}
	
}
