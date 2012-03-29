package org.easysoa.runtime.api;


/**
 * Allows to start/stop the server.
 * 
 * @author mkalam-alami
 *
 */
public interface RuntimeControlService {

	enum RuntimeState {
		STARTING, STARTED,
		DEPLOYING_DEPLOYABLE, STARTING_DEPLOYABLE,
		STOPPING_DEPLOYABLE, UNDEPLOYING_DEPLOYABLE,
		STOPPING, STOPPED
	}
	
	RuntimeState getState();
	
	boolean start();

	boolean stop();
	
}
