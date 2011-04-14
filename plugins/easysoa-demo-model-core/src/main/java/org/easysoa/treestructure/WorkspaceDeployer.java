package org.easysoa.treestructure;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.tools.RelationService;
import org.easysoa.tools.VocabularyService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

/**
 * Creates all needed folders and common vocabulary entries.
 * 
 * @author mkalam-alami
 * 
 */
public class WorkspaceDeployer extends UnrestrictedSessionRunner {
	private static final Log log = LogFactory.getLog(WorkspaceDeployer.class);
	private static List<String> descriptorTypes = null;
	public static final String ROOT = "/default-domain/workspaces";
	public static final String DESCRIPTORS_WORKSPACE = "/default-domain/workspaces/Descriptors/";
	public static final String SERVICES_WORKSPACE = "/default-domain/workspaces/Services/";

	public WorkspaceDeployer(String repositoryName) {
		super(repositoryName);
	}

	public void run() throws ClientException {
		if (descriptorTypes == null) {
			descriptorTypes = new ArrayList<String>();
			descriptorTypes.add("WSDL");
		}

		// Workspace structure
		if (!this.session.exists(new PathRef(DESCRIPTORS_WORKSPACE))) {
			log.info("Created "+DESCRIPTORS_WORKSPACE);
			createAndSaveDocument(this.session, "Workspace", ROOT, "Descripteurs"); // TODO: l10n
		}
		if (!this.session.exists(new PathRef(SERVICES_WORKSPACE))) {
			log.info("Created "+SERVICES_WORKSPACE);
			createAndSaveDocument(this.session, "Workspace", ROOT, "Services");
		}
		for (String descriptorType : descriptorTypes) {
			PathRef descriptorTypePath = new PathRef(DESCRIPTORS_WORKSPACE + descriptorType);
			
			if (!this.session.exists(descriptorTypePath)) {
				createAndSaveDocument(this.session, "Folder", DESCRIPTORS_WORKSPACE,
						descriptorType);
				log.info("Created " + descriptorTypePath);
			}
		}

		// Relations vocabulary
		try {
			if (!VocabularyService.entryExists(this.session, "predicates",
					RelationService.DEFAULT_PREDICATE)) {
				VocabularyService.addEntry(this.session, "predicates",
						RelationService.DEFAULT_PREDICATE, RelationService.DEFAULT_PREDICATE);

				VocabularyService.addEntry(this.session, "inverse_predicates",
						RelationService.DEFAULT_PREDICATE, RelationService.DEFAULT_PREDICATE_INVERSE);

				log.info("Created new relation predicate : "+RelationService.DEFAULT_PREDICATE);
			}
		} catch (Exception e) {
			log.error("Cannot initialize custom predicates.", e);
		}
	}

	private void createAndSaveDocument(CoreSession session, String type,
			String parent, String name) throws ClientException {
		DocumentModel docModel = session
				.createDocumentModel(parent, name, type);

		docModel = session.createDocument(docModel);
		docModel.setProperty("dublincore", "title", name);
		session.saveDocument(docModel);
	}

	public List<String> getDescriptorTypes() {
		return descriptorTypes;
	}
}