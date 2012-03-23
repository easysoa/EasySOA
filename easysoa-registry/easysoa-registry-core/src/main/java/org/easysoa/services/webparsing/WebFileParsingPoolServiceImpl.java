package org.easysoa.services.webparsing;

import java.net.URL;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.security.auth.login.LoginContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.HttpDownloader;
import org.easysoa.services.HttpDownloaderService;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 *
 * @author mkalam-alami
 *
 */
public class WebFileParsingPoolServiceImpl extends DefaultComponent implements Runnable, WebFileParsingPoolService {

    private static Log log = LogFactory.getLog(WebFileParsingPoolServiceImpl.class);

    public static final String PARSERS_EXTENSIONPOINT = "parsers";

    private Thread parsingPoolThread;
    private Deque<WebFileParsingPoolEntry> parsingPool = new LinkedList<WebFileParsingPoolEntry>();
    private Map<String, WebFileParser> parsers = new HashMap<String, WebFileParser>();

    private CoreSession coreSession = null;
    private LoginContext loginContext = null;
    
    @Override
    public void activate(ComponentContext context) throws Exception {

    }

    @Override
    public void applicationStarted(ComponentContext context) throws Exception {
        if (parsingPoolThread == null) {
            parsingPoolThread = new Thread(this);
            parsingPoolThread.setName("WebFileParsingPoolService");
        }
        parsingPoolThread.start();
    }

    @Override
    public void deactivate(ComponentContext context) throws Exception {
        if (parsingPoolThread != null) {
            parsingPoolThread.interrupt();
        }
        if (coreSession != null) {
        	CoreInstance.getInstance().close(coreSession);
        }
        if (loginContext != null) {
        	loginContext.logout();
        }
    }

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception {
        if (extensionPoint.equals(PARSERS_EXTENSIONPOINT)) {
            try {
                // Register/remove web file parser
                WebFileParserDescriptor parserDescriptor = (WebFileParserDescriptor) contribution;
                if (parsers.containsKey(parserDescriptor.implementation) && !parserDescriptor.enabled) {
                    parsers.remove(parserDescriptor.implementation);
                }
                else if (!parsers.containsKey(parserDescriptor.implementation) && parserDescriptor.enabled) {
                    Class<?> parserClass = Class.forName(parserDescriptor.implementation.trim());
                    WebFileParser newInstance = (WebFileParser) parserClass.newInstance();
                    parsers.put(parserDescriptor.implementation, newInstance);
                }
            } catch (Exception e) {
                log.error("Error while registering web file parser contribution", e);
            }
        }
    }

    @Override
    public void unregisterContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception {
        if (extensionPoint.equals(PARSERS_EXTENSIONPOINT)) {
            try {
                // Unregister web file parser
                WebFileParserDescriptor parserDescriptor = (WebFileParserDescriptor) contribution;
                if (parsers.containsKey(parserDescriptor.implementation)) {
                    parsers.remove(parserDescriptor.implementation);
                }
            } catch (Exception e) {
                log.error("Error while registering web file parser contribution", e);
            }
        }
    }

    @Override
    public void run() {

            // Log in and gather needed data
            HttpDownloaderService downloaderService = Framework.getLocalService(HttpDownloaderService.class);

            while (true) { // Terminates by being interrupted during components' deactivation

                // Blocking step
                boolean empty;
                synchronized (parsingPool) {
                    empty = parsingPool.isEmpty();
                }

                if (empty) {
                    try {
                        synchronized (this) {
                            wait();
                        }
                    } catch (InterruptedException e) {
                        // Do nothing
                    }
                }

                // Entry processing
                WebFileParsingPoolEntry entry = null;
                synchronized (parsingPool) {
                    if (!parsingPool.isEmpty()) {
                        entry = parsingPool.pop();
                    }
                }

                if (entry != null) {
                    try {
                        // Download
                        HttpDownloader httpDownloader = downloaderService.createHttpDownloader(entry.getUrl());
                        Blob blob = httpDownloader.download().getBlob();

                        // Save blob in content property
                        DocumentModel targetModel = entry.getTargetModel();
                        String storageProp = entry.getStorageProp();
                        if (storageProp != null) {
                            targetModel.getProperty(storageProp).setValue(blob);
                        }

                        try {
                        	
                            loginContext = Framework.login();
                            RepositoryManager mgr = Framework.getService(RepositoryManager.class);
                            Repository repository = mgr.getDefaultRepository();
                            TransactionHelper.startTransaction();
                            coreSession = null;
                            
                            if (repository != null) {
                                coreSession = repository.open();
                            }

                            if (coreSession!=null) {
                                // Run through every parser registered
                                for (WebFileParser parser : parsers.values()) {
                                    parser.parse(coreSession, blob, targetModel, entry.getOptions());
                                }

                                // Save
                                coreSession.saveDocument(targetModel);
                                coreSession.save();
                            } else {
                                throw new ClientException("Unable to get CoreSession");
                            }
                        } catch (Throwable t) {
                            log.warn("Error while processing parsing pool entry: " + t.getMessage());
                            TransactionHelper.setTransactionRollbackOnly();
                            // TODO: handle exception
                        } finally {
                            TransactionHelper.commitOrRollbackTransaction();
                            if (coreSession != null) {
	                            CoreInstance.getInstance().close(coreSession);
	                        }
                            if (loginContext != null) {
                            	loginContext.logout();
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Error while downloading file: " + e.getMessage());
                    }
                }

            }
    }

    @Override
    public void append(URL url, DocumentModel targetModel, String storageProp,
            Map<String, String> options) throws IllegalArgumentException {
        synchronized (parsingPool) {
            parsingPool.push(new WebFileParsingPoolEntry(url, targetModel, storageProp, options));
            synchronized (this) {
                notifyAll();
            }
        }
    }

}
