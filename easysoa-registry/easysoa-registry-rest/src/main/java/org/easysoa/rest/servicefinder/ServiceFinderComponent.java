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
            ComponentName.DEFAULT_TYPE, ServiceFinderComponent.class.getName());
    public static final String EXTENSTION_POINT_STRATEGIES = "strategies";

    private static Log log = LogFactory.getLog(ServiceFinderComponent.class);
    
    private List<ServiceFinderStrategy> finders = new LinkedList<ServiceFinderStrategy>();

    public List<ServiceFinderStrategy> getServiceFinders() {
        return finders;
    }
    
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) throws Exception {
        try {
            synchronized (finders) {
            if (extensionPoint.equals(EXTENSTION_POINT_STRATEGIES)) {
                ServiceFinderStrategyDescriptor finderDescriptor = (ServiceFinderStrategyDescriptor) contribution;
                if (finderDescriptor.enabled) {
                    Class<?> finderClass = Class.forName(finderDescriptor.implementation.trim());
                    if (ServiceFinderStrategy.class.isAssignableFrom(finderClass)) {
                        finders.add((ServiceFinderStrategy) finderClass.newInstance());
                    } else {
                        throw new InvalidClassException(renderContributionError(
                                contributor, "class " + finderDescriptor.implementation
                                        + " is not an instance of "
                                        + ServiceFinderStrategy.class.getName()));
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
        synchronized (finders) {
        if (extensionPoint.equals(EXTENSTION_POINT_STRATEGIES)) {
            ServiceFinderStrategyDescriptor finderDescriptor = (ServiceFinderStrategyDescriptor) contribution;
            if (finderDescriptor.enabled) {
                Class<?> finderClass = Class.forName(finderDescriptor.implementation.trim());
                try {
                    for (ServiceFinderStrategy finder : finders) {
                        if (finder.getClass().equals(finderClass)) {
                            finders.remove(finder);
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
