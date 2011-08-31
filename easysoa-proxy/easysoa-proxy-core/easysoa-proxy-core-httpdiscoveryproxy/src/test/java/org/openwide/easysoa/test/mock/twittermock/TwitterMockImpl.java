package org.openwide.easysoa.test.mock.twittermock;

import org.openwide.easysoa.test.Utilities;

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
	
}
