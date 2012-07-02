/**
 * EasySOA Registry
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

package org.easysoa.validation;

import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.platform.scheduler.core.interfaces.SchedulerRegistry;
import org.nuxeo.ecm.platform.scheduler.core.service.ScheduleImpl;
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
	
	private SchedulerRegistry schedulerRegistry;

	public ValidationSchedulerComponent() throws Exception {
		schedulerRegistry = Framework.getService(SchedulerRegistry.class);
	}
	
	@Override
	public void registerContribution(Object contribution,
			String extensionPoint, ComponentInstance contributor)
			throws Exception {
		if (EXTENSION_POINT_SCHEDULES.equals(extensionPoint)) {
			ValidationScheduleDescriptor descriptor = (ValidationScheduleDescriptor) contribution;
            synchronized (schedulerRegistry) {
                String eventInfo = descriptor.runName + "-"
                        + descriptor.targetEnvironment + "-"
                        + descriptor.cronExpression.hashCode();
				ScheduleImpl schedule = new ScheduleImpl();
				schedule.id = eventInfo;
				schedule.username = "Administrator";
                schedule.eventCategory = eventInfo;
				schedule.cronExpression = descriptor.cronExpression;
				schedule.eventId = SCHEDULE_EVENT;
				schedulerRegistry.registerSchedule(schedule);
            }
        }
	}
	
	@Override
	public void unregisterContribution(Object contribution,
			String extensionPoint, ComponentInstance contributor)
			throws Exception {
	}
	
}
