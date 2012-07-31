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

package org.easysoa.run;

import org.apache.log4j.Logger;
import org.easysoa.monitoring.Message;

/**
 * Run recorder, only to record messages in the current run
 * @deprecated Use the method record directly from the RunManagerImpl class
 * @author jguillemotte
 *
 */
@Deprecated
public class RunRecorder {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(RunRecorder.class.getName());	
	
	/**
	 * Records a message in the current Run
	 * @param message The message to record
	 */
	public void record(Message message){
		// Get the current run and add a message
		logger.debug("Recording message : " + message);
		try{
			//RunManagerImpl.getInstance().getCurrentRun().addMessage(message);
		}
		catch(Exception ex){
			logger.error("Unable to record message !", ex);
		}
	}
	
}
