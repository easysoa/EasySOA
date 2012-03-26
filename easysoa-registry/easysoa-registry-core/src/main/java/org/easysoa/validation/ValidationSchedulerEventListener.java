package org.easysoa.validation;


import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.DocumentService;
import org.easysoa.services.PublicationService;
import org.easysoa.services.ServiceValidationService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * 
 * @author mkalam-alami
 *
 */
public class ValidationSchedulerEventListener implements EventListener {

	private static final String TEMPLATES_FOLDER = "./nxserver/nuxeo.war/templates";
	private static final String VALIDATION_REPORT_TEMPLATE = "validationReport.ftl";

    private static Log log = LogFactory.getLog(ValidationSchedulerEventListener.class);
    
	private static Configuration freemarkerCfg = null;
    
	private ServiceValidationService serviceValidationService;

	private PublicationService publicationService;
	
	private DocumentService docService;
	
	private CoreSession session = null;
	
    static {
    	try {
        	freemarkerCfg = new Configuration();
			freemarkerCfg.setDirectoryForTemplateLoading(new File(TEMPLATES_FOLDER));
	    	freemarkerCfg.setObjectWrapper(new DefaultObjectWrapper());
		} catch (IOException e) {
			log.error("Failed to initialize templating configuration, no validation reports will be produced.", e);
		}
    }
    
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

				if (freemarkerCfg != null) {
					// Create report
			        Template tpl = freemarkerCfg.getTemplate(VALIDATION_REPORT_TEMPLATE);
			        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
			        for (ValidationResult validationResult : validationResults) {
				        List<Map<String, Object>> validatorsResults = new ArrayList<Map<String, Object>>();
				        for (ValidatorResult validatorResult : validationResult) {
					        Map<String, Object> paramsOneValidator = new HashMap<String, Object>();
					        paramsOneValidator.put("validationSuccess", validatorResult.isValidated() ? "passed" : "failed");
					        paramsOneValidator.put("validatorName", validatorResult.getValidatorName());
					        paramsOneValidator.put("validationLog", validatorResult.getValidationLog());
					        validatorsResults.add(paramsOneValidator);
				        }
				        Map<String, Object> paramsOneResult = new HashMap<String, Object>();
				        paramsOneResult.put("validatorsResults", validatorsResults);
				        paramsOneResult.put("validationSuccess", validationResult.isValidationPassed() ? "passed" : "failed");
				        paramsOneResult.put("serviceName", validationResult.getServiceName());
				        results.add(paramsOneResult);
			        }
			        Map<String, Object> params = new HashMap<String, Object>();
			        List<String> validatorsNames = new ArrayList<String>();
			        for (ValidatorResult validatorResult : validationResults.get(0)) {
			        	validatorsNames.add(validatorResult.getValidatorName());
			        }
			        params.put("results", results);
			        params.put("validatorsNames", validatorsNames);
			        params.put("validationSuccess", validationResults.isEveryValidationPassed() ? "passed" : "failed");
			        params.put("date", new Date());
			        params.put("runName", "TODO"); // TODO
			        params.put("environmentName", environmentName);
			        
			        StringWriter writer = new StringWriter();
			        tpl.process(params, writer);
			        writer.flush();
			        String reportAsString = writer.getBuffer().toString();
	
					// Save report | TODO if too much files, remove the older ones
					DocumentModel workspaceModel = docService.findWorkspace(session, environmentName);
					@SuppressWarnings("unchecked")
					List<Map<String, Object>> files = (List<Map<String, Object>>) workspaceModel.getProperty("files", "files");
					Map<String, Object> newFile = new HashMap<String, Object>();
					newFile.put("filename", "Validation report - " + new Date(System.currentTimeMillis()).toString());
					newFile.put("file", new StringBlob(reportAsString));
	
					files.add(newFile);
					workspaceModel.setProperty("files", "files", files);
					session.saveDocument(workspaceModel);
				}
			
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
