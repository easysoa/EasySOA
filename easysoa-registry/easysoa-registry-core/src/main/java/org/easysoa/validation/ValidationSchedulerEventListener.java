package org.easysoa.validation;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.DocumentService;
import org.easysoa.services.PublicationService;
import org.easysoa.services.ServiceValidationService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * 
 * @author mkalam-alami
 *
 */
public class ValidationSchedulerEventListener implements EventListener {

	private ServiceValidationService serviceValidationService;

	private PublicationService publicationService;
	
	private DocumentService docService;
	
	private CoreSession session = null;
	
    private static Log log = LogFactory.getLog(ValidationSchedulerEventListener.class);
	
	public ValidationSchedulerEventListener() throws Exception {
		serviceValidationService = Framework.getService(ServiceValidationService.class);
		publicationService = Framework.getService(PublicationService.class);
		docService = Framework.getService(DocumentService.class);
	}
	
	@Override
	public void handleEvent(Event event) throws ClientException {
		
		synchronized(log) {
		
		try {
		
			// Init
	        RepositoryManager mgr = Framework.getService(RepositoryManager.class);
	        Repository repository = mgr.getDefaultRepository();
	        TransactionHelper.startTransaction();
	        session = repository.open();
			String environmentName = (String) event.getContext().getProperty("eventCategory"); // (see ValidationSchedulerComponent.registerContribution()
			String tmpWorkspaceName = "tmp" + environmentName + System.currentTimeMillis();
			
			// Fork existing environment
			DocumentModel environmentModel = docService.findEnvironment(session, environmentName);
			if (environmentModel != null) {
				DocumentModel tmpWorkspaceModel = null;
				ValidationResultList validationResults = null;

				try {
					// Create temporary environment
					tmpWorkspaceModel = publicationService.forkEnvironment(session, environmentModel, tmpWorkspaceName);
					
					// Run discovery replay
					// TODO
					
					// Validate temporary environment
					validationResults = serviceValidationService.validateServices(session, tmpWorkspaceModel); 
				}
				catch (Exception e) {
					log.error("Failed to run scheduled validation", e);
				}
				finally {
					if (tmpWorkspaceModel != null) {
						// Remove temporary environment
						session.removeDocument(tmpWorkspaceModel.getRef());
					}
				}
				
				// Create report
				DocumentModel workspaceModel = docService.findWorkspace(session, environmentName);
				// TODO
			
			}
			else {
				log.error("Failed to run scheduled validation: environment '" + environmentName + "' doesn't exist");
			}
			
		}
		
		catch (Exception e) {
			log.error("Failed to run scheduled validation", e);
		}
		
		finally {
	        TransactionHelper.commitOrRollbackTransaction();
	        if (session != null) {
	            CoreInstance.getInstance().close(session);
	        }
		}
		
		}
	}
	
}
