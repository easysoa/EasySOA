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
            ComponentName.DEFAULT_TYPE, "org.easysoa.rest.servicefinder.ServiceFinderComponent");

    private static Log log = LogFactory.getLog(ServiceFinderComponent.class);
    
    private List<ServiceFinder> finders = new LinkedList<ServiceFinder>();

    public List<ServiceFinder> getServiceFinders() {
        return finders;
    }
    
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) throws Exception {
        try {
            synchronized (finders) {
            if (extensionPoint.equals("serviceFinders")) {
                ServiceFinderDescriptor finderDescriptor = (ServiceFinderDescriptor) contribution;
                if (finderDescriptor.enabled) {
                    Class<?> finderClass = Class.forName(finderDescriptor.implementation.trim());
                    if (ServiceFinder.class.isAssignableFrom(finderClass)) {
                        finders.add((ServiceFinder) finderClass.newInstance());
                    } else {
                        throw new InvalidClassException(renderContributionError(
                                contributor, "class " + finderDescriptor.implementation
                                        + " is not an instance of "
                                        + ServiceFinder.class.getName()));
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
        if (extensionPoint.equals("serviceFinders")) {
            ServiceFinderDescriptor finderDescriptor = (ServiceFinderDescriptor) contribution;
            if (finderDescriptor.enabled) {
                Class<?> finderClass = Class.forName(finderDescriptor.implementation.trim());
                try {
                    for (ServiceFinder finder : finders) {
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
