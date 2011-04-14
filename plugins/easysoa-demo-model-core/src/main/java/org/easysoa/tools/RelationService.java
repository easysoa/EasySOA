package org.easysoa.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.relations.api.QNameResource;
import org.nuxeo.ecm.platform.relations.api.RelationManager;
import org.nuxeo.ecm.platform.relations.api.Resource;
import org.nuxeo.ecm.platform.relations.api.Statement;
import org.nuxeo.ecm.platform.relations.api.impl.ResourceImpl;
import org.nuxeo.ecm.platform.relations.api.impl.StatementImpl;
import org.nuxeo.ecm.platform.relations.api.util.RelationHelper;
import org.nuxeo.runtime.api.Framework;

/**
 * Helpers for managing relations between documents.
 * Only considers the relations of type RelationService.DEFAULT_PREDICATE
 * @author mkalam-alami
 *
 */
public class RelationService {
	private static final Log log = LogFactory.getLog(RelationService.class);
	public static final String DEFAULT_PREDICATE = "Est rattaché à";
	public static final String DEFAULT_PREDICATE_INVERSE = "Est implémenté par";

	/**
	 * Returns all document related to specified document.
	 * @param session
	 * @param doc
	 * @return
	 */
	public static final DocumentModelList getRelations(CoreSession session,
			DocumentModel doc) {
		DocumentModelList relations = null;
		try {
			AutomationService automationService = (AutomationService) Framework
					.getService(AutomationService.class);
			OperationContext opCtxt = new OperationContext(session);
			opCtxt.setInput(doc);

			OperationChain chain = new OperationChain(null);
			chain.add("Context.FetchDocument");
			chain.add("Relations.GetRelations").set("predicate",
					DEFAULT_PREDICATE);

			relations = (DocumentModelList) automationService
					.run(opCtxt, chain);
		} catch (Exception e) {
			log.error("Failed to get document relations", e);
		}

		return relations;
	}

	/**
	 * Creates a relation between two documents.
	 * @param from
	 * @param to
	 */
	public static final void createRelation(DocumentModel from, DocumentModel to) {
		try {
			RelationManager service = RelationHelper.getRelationManager();
			QNameResource fromResource = RelationHelper
					.getDocumentResource(from);
			QNameResource toResource = RelationHelper.getDocumentResource(to);
			Resource predicate = new ResourceImpl(DEFAULT_PREDICATE);

			List<Statement> stmts = new ArrayList<Statement>();
			Statement stmt = new StatementImpl(fromResource, predicate,
					toResource);
			stmts.add(stmt);
			if (!service.hasStatement("default", stmt)) {
				service.add("default", stmts);
				log.info("Relation creation : " + from.getTitle()
						+ " -> " + to.getTitle());
			} else {
				log.info("Relation " + from.getTitle() + " -> " + to.getTitle()
						+ " already exists.");
			}
		} catch (Exception e) {
			log.error("Relation creation failed", e);
		}
	}

	/**
	 * Clears all relations from specified document.
	 * @param doc
	 */
	public static final void clearRelations(DocumentModel doc) {
		try {
			Resource predicate = new ResourceImpl(DEFAULT_PREDICATE);
			List<Statement> stmts = RelationHelper.getStatements(doc, predicate);
			log.info("Deleting all " + stmts.size() + " relations of document " + doc.getTitle());
			RelationHelper.getRelationManager().remove("default", stmts);
		} catch (Exception e) {
			log.error("Relations reset failed", e);
		}
	}
}