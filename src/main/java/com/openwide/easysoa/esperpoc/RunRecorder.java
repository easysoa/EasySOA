package com.openwide.easysoa.esperpoc;

import org.apache.log4j.Logger;
import com.openwide.easysoa.monitoring.Message;

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
		RunManager.getInstance().getCurrentRun().addMessage(message);
	}
	
}
