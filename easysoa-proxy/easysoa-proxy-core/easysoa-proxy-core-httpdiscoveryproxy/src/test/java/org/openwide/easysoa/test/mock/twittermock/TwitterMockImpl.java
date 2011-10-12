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
 * Contact : easysoa-dev@groups.google.com
 */

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
