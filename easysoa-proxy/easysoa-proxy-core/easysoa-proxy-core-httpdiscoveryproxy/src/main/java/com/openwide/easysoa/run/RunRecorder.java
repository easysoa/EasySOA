package com.openwide.easysoa.run;

import org.apache.log4j.Logger;
import com.openwide.easysoa.monitoring.Message;

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
