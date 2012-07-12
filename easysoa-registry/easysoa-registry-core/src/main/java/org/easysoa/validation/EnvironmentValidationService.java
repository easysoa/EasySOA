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
public class EnvironmentValidationService {

	private static final String TEMPLATES_FOLDER = "./nxserver/nuxeo.war/templates";
	private static final String VALIDATION_REPORT_TEMPLATE = "validationReport.ftl";

    private static Log log = LogFactory.getLog(EnvironmentValidationService.class);
    
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
			log.error("Failed to initialize templating configuration, no validation reports will be produced: " + e.getMessage());
		}
    }
    
	public EnvironmentValidationService() throws Exception {
		serviceValidationService = Framework.getService(ServiceValidationService.class);
		publicationService = Framework.getService(PublicationService.class);
		docService = Framework.getService(DocumentService.class);
	}
	
	public String run(String runName, String environmentName) throws ClientException {
		
		synchronized(log) {
		    
	    String validationReportHtml = null;

        boolean wasActiveTx = false;
		try {
		
			// Init
	        RepositoryManager mgr = Framework.getService(RepositoryManager.class);
	        Repository repository = mgr.getDefaultRepository();
	        
	        wasActiveTx = TransactionHelper.isTransactionActive();
	        if (wasActiveTx) {
	            TransactionHelper.commitOrRollbackTransaction();
	        }
	        
	        TransactionHelper.startTransaction();
	        session = repository.open();
			
			String tmpWorkspaceName = "tmp" +  + System.currentTimeMillis();
			
			// Fork existing environment
			DocumentModel environmentModel = docService.findEnvironment(session, environmentName);
			if (environmentModel != null) {
				DocumentModel tmpWorkspaceModel = null;
				ValidationResultList validationResults = null;

				//try {
					// Run discovery replay
					ExchangeReplayController exchangeReplayController = serviceValidationService.getExchangeReplayController();
					if (exchangeReplayController != null) {
	                    // Create temporary environment
	                    tmpWorkspaceModel = publicationService.forkEnvironment(session, environmentModel, tmpWorkspaceName);
	                    
	                    exchangeReplayController.replayRecord(runName, environmentName);
	                    
	                    // Validate temporary environment
	                    validationResults = serviceValidationService.validateServices(session, tmpWorkspaceModel); 
					}
					else {
	                    log.error("Cannot run scheduled validation: No exchange replay controller available");
					}
				/*}
				catch (Exception e) {
					log.error("Failed to run scheduled validation", e);
					throw e;
				}
				finally {
					if (tmpWorkspaceModel != null) {
						// Remove temporary environment
						session.removeDocument(tmpWorkspaceModel.getRef());
					}
				}*/

				if (freemarkerCfg != null && validationResults != null) {
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
			        params.put("runName", runName);
			        params.put("environmentName", environmentName);
			        
			        StringWriter writer = new StringWriter();
			        tpl.process(params, writer);
			        writer.flush();
			        validationReportHtml = writer.getBuffer().toString();
	
					// Save report | TODO if too much files, remove the older ones
					DocumentModel workspaceModel = docService.findWorkspace(session, environmentName);
					@SuppressWarnings("unchecked")
					List<Map<String, Object>> files = (List<Map<String, Object>>) workspaceModel.getProperty("files", "files");
					Map<String, Object> newFile = new HashMap<String, Object>();
					newFile.put("filename", "Validation report - " + new Date(System.currentTimeMillis()).toString() + ".html");
					newFile.put("file", new StringBlob(validationReportHtml));
	
					files.add(newFile);
					workspaceModel.setProperty("files", "files", files);
					session.saveDocument(workspaceModel);
				}
			
			}
			else {
				log.error("Failed to run scheduled validation: environment '" + environmentName + "' doesn't exist. Did you publish it?");
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
            if (wasActiveTx) {
                TransactionHelper.startTransaction();
            }
		}

        return validationReportHtml;
        
		}
		
	}
	
}
