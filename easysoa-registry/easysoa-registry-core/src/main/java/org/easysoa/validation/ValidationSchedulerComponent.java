package org.easysoa.validation;

import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.platform.scheduler.core.service.ScheduleImpl;
import org.nuxeo.ecm.platform.scheduler.core.service.SchedulerRegistryService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * 
 * @author mkalam-alami
 *
 */
@XObject("schedule")
public class ValidationSchedulerComponent extends DefaultComponent {
	
	public static final String EXTENSION_POINT_SCHEDULES = "schedules"; 
	
	public static final String SCHEDULE_EVENT = "onScheduledValidationRequired"; 
	
	private SchedulerRegistryService schedulerRegistryService;

	public ValidationSchedulerComponent() throws Exception {
		schedulerRegistryService = Framework.getService(SchedulerRegistryService.class);
	}
	
	@Override
	public void registerContribution(Object contribution,
			String extensionPoint, ComponentInstance contributor)
			throws Exception {
		if (EXTENSION_POINT_SCHEDULES.equals(extensionPoint)) {
			ValidationScheduleDescriptor descriptor = (ValidationScheduleDescriptor) contribution;
            synchronized (schedulerRegistryService) {
				ScheduleImpl schedule = new ScheduleImpl();
				schedule.id = descriptor.runName + "-"
						+ descriptor.targetEnvironment + "-"
						+ descriptor.cronExpression.hashCode();
				schedule.username = "Administrator";
				schedule.cronExpression = descriptor.cronExpression;
				schedule.eventCategory = descriptor.targetEnvironment; // Half-hack to pass the environment as an event parameter
				schedule.eventId = SCHEDULE_EVENT;
				schedulerRegistryService.registerSchedule(schedule);
            }
        }
	}
	
	@Override
	public void unregisterContribution(Object contribution,
			String extensionPoint, ComponentInstance contributor)
			throws Exception {
	}
	
}
