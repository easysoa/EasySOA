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

package org.easysoa.rest.servicefinder;

import java.io.InvalidClassException;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.ComponentName;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * 
 * @author mkalam-alami
 * 
 */
public class ServiceFinderComponent extends DefaultComponent {

    public static final ComponentName NAME = new ComponentName(
            ComponentName.DEFAULT_TYPE, "org.easysoa.rest.service.ServiceFinderComponent");
    public static final String EXTENSTION_POINT_STRATEGIES = "strategies";

    private static Log log = LogFactory.getLog(ServiceFinderComponent.class);
    
    private List<ServiceFinderStrategy> strategies = new LinkedList<ServiceFinderStrategy>();

    public List<ServiceFinderStrategy> getStrategies() {
        return strategies;
    }
    
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) throws Exception {
        try {
            synchronized (strategies) {
            if (extensionPoint.equals(EXTENSTION_POINT_STRATEGIES)) {
                ServiceFinderStrategyDescriptor finderDescriptor = (ServiceFinderStrategyDescriptor) contribution;
                if (finderDescriptor.enabled) {
                    Class<?> finderClass = Class.forName(finderDescriptor.implementation.trim());
                    if (ServiceFinderStrategy.class.isAssignableFrom(finderClass)) {
                        strategies.add((ServiceFinderStrategy) finderClass.newInstance());
                    } else {
                        throw new InvalidClassException(renderContributionError(
                                contributor, "class " + finderDescriptor.implementation
                                        + " is not an instance of " + ServiceFinderStrategy.class.getName()));
                    }
                }
            }
            }
        }
        catch (Exception e) {
            throw new Exception(renderContributionError(contributor, ""), e);
        }
    }

    public void unregisterContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) throws Exception {
        synchronized (strategies) {
        if (extensionPoint.equals(EXTENSTION_POINT_STRATEGIES)) {
            ServiceFinderStrategyDescriptor finderDescriptor = (ServiceFinderStrategyDescriptor) contribution;
            if (finderDescriptor.enabled) {
                Class<?> finderClass = Class.forName(finderDescriptor.implementation.trim());
                try {
                    for (ServiceFinderStrategy finder : strategies) {
                        if (finder.getClass().equals(finderClass)) {
                            strategies.remove(finder);
                        }
                    }
                }
                catch (ConcurrentModificationException e) {
                    // FIXME Fix this exception being thrown in tests.
                    log.warn("Failed to unregister contribution: "+e.getMessage());
                }
            }
        }
        }
    }
    
    private String renderContributionError(ComponentInstance contributor,
            String message) {
        return "Invalid contribution from '" + contributor.getName() + "': " + message;
    }
}
