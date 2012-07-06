package org.easysoa.registry.frascati;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.easysoa.frascati.FraSCAtiServiceException;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.frascati.factory.NuxeoFraSCAtiStartedEvent;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
*
* Component that allows the deployment of FraSCAti apps on startup through
* simple XML contributions.
*
* @author mkalam-alami
*
*/
public class FraSCAtiAppComponent extends DefaultComponent implements EventListener {

    private static final String APP_EXTENSION_POINT = "apps";

    private static final Logger logger = Logger.getLogger(FraSCAtiAppComponent.class);

    private static List<FraSCAtiAppDescriptor> contributionCache = new LinkedList<FraSCAtiAppDescriptor>();
    
    private FraSCAtiServiceItf frascatiService;


    /**
* Stores composite instance keys in order to be able to stop them.
*/
    private Map<FraSCAtiAppDescriptor, String> compositeInstances = new HashMap<FraSCAtiAppDescriptor, String>();

    public void handleEvent(Event event) throws ClientException {
        if (NuxeoFraSCAtiStartedEvent.ID.equals(event.getName())) {
            NuxeoFraSCAtiStartedEvent nuxeoFraSCAtiEvent = (NuxeoFraSCAtiStartedEvent) event;
            frascatiService = nuxeoFraSCAtiEvent.getFraSCAtiService();
            synchronized (contributionCache) {
                if (!contributionCache.isEmpty()) {
                    logger.info("FraSCAti in Nuxeo is ready, handling " + contributionCache.size()
                            + " postponed contributions");
                    for (FraSCAtiAppDescriptor contribution : contributionCache) {
                        handleContribution(contribution);
                    }
                }
            }
        }
    }

    @Override
    public void deactivate(ComponentContext context) throws Exception {
        for (String value : compositeInstances.values()) {
            frascatiService.remove(value);
        }
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint,
            ComponentInstance contributor) throws Exception {
        if (APP_EXTENSION_POINT.equals(extensionPoint)
                && contribution instanceof FraSCAtiAppDescriptor) {
            FraSCAtiAppDescriptor app = (FraSCAtiAppDescriptor) contribution;
            if (frascatiService == null) {
                logger.info("Contribution " + app.name
                        + " postponed, FraSCAti in Nuxeo is not ready yet");
                synchronized (contributionCache) {
                    contributionCache.add(app);
                }
            } else {
                handleContribution(app);
            }
        }
    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint,
            ComponentInstance contributor) throws Exception {
        if (APP_EXTENSION_POINT.equals(extensionPoint)
                && contribution instanceof FraSCAtiAppDescriptor) {
            FraSCAtiAppDescriptor app = (FraSCAtiAppDescriptor) contribution;
            String compositeToRemove = compositeInstances.remove(app);
            if (frascatiService != null && compositeToRemove != null) {
                try {
                    frascatiService.remove(compositeToRemove);
                } catch (FraSCAtiServiceException e) {
                    logger.error("Failed to stop and remove application " + app.name, e);
                }
            }
        }
    }

    private void handleContribution(FraSCAtiAppDescriptor app) {
        File appJar = new File(app.jarPath).getAbsoluteFile();
        try {
            URL[] appClasspath = filesToUrls(appJar, new File(app.libsPath).listFiles());
            logger.info("Loading app " + app.name);
            String newCompositeName = frascatiService.processComposite(app.compositeName,
                    FraSCAtiServiceItf.all, appClasspath);
            compositeInstances.put(app, newCompositeName);
            logger.info("Successfuly started app " + app.name);
        } catch (FraSCAtiServiceException e) {
            logger.error("Failed to load app " + app.name + " (composite " + app.compositeName
                    + " from jar '" + appJar.getAbsolutePath() + "')", e);
        } catch (MalformedURLException e) {
            logger.error("Invalid app Jar location", e);
        }
    }

    private URL[] filesToUrls(File jarFile, File[] libFiles) throws MalformedURLException {
        List<URL> urls = new LinkedList<URL>();
        urls.add(jarFile.toURI().toURL());
        for (File libFile : libFiles) { 
            if (libFile.getName().endsWith(".jar")) {
                urls.add(libFile.toURI().toURL());
            }
        };
        return urls.toArray(new URL[0]);
    }

}
