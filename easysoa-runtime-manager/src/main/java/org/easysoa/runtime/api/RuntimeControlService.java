package org.easysoa.runtime.api;

/**
 * Allows to start/stop the server.
 * 
 * @author mkalam-alami
 *
 */
public interface RuntimeControlService {

	boolean start();

	boolean stop();

}
