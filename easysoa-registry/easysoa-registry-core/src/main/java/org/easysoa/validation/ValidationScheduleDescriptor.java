package org.easysoa.validation;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * 
 * @author mkalam-alami
 *
 */
@XObject("schedule")
public class ValidationScheduleDescriptor {
	
	// <schedule runName="myRun" targetEnvironment="master" cronExpression="0 0 3 1 * ?">
	
	@XNode("@runName")
	protected String runName;

	@XNode("@targetEnvironment")
	protected String targetEnvironment;
	
	@XNode("@cronExpression")
	protected String cronExpression;
	
}
