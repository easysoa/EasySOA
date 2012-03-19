package org.easysoa.validation;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.DocumentService;
import org.easysoa.services.PublicationService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 *
 */
public class ValidationSchedulerEventListener implements EventListener {

	private ServiceValidatorComponent serviceValidatorService;

	private PublicationService publicationService;
	
	private DocumentService docService;

    private static Log log = LogFactory.getLog(ValidationSchedulerEventListener.class);
    
	public ValidationSchedulerEventListener() throws Exception {
		serviceValidatorService = Framework.getService(ServiceValidatorComponent.class);
		publicationService = Framework.getService(PublicationService.class);
		docService = Framework.getService(DocumentService.class);
	}
	
	@Override
	public void handleEvent(Event event) throws ClientException {
		
		try {
		
			// Init
			CoreSession session = event.getContext().getCoreSession();
			String environmentName = (String) event.getContext().getProperty("category"); // (see ValidationSchedulerComponent.registerContribution()
			String tmpWorkspace = "tmp" + environmentName + System.currentTimeMillis();
			
			// Fork existing environment
			DocumentModel environmentModel = docService.findEnvironment(session, environmentName);
			publicationService.forkEnvironment(session, environmentModel); // TODO add param tmpWorkspace
			
			// Validate temporary environment
			DocumentModel tmpWorkspaceModel = docService.findWorkspace(session, tmpWorkspace);
			serviceValidatorService.validateServices(session, tmpWorkspaceModel);
			
			// Remove temporary environment
			session.removeDocument(tmpWorkspaceModel.getRef());
			
			// Create report
			// TODO
			
		
		}
		
		catch (Exception e) {
			log.error("Failed to run scheduled validation", e);
		}
	}
	
}
