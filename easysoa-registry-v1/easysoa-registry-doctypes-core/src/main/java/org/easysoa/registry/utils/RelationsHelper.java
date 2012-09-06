package org.easysoa.registry.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.relations.api.Node;
import org.nuxeo.ecm.platform.relations.api.QNameResource;
import org.nuxeo.ecm.platform.relations.api.RelationManager;
import org.nuxeo.ecm.platform.relations.api.Resource;
import org.nuxeo.ecm.platform.relations.api.ResourceAdapter;
import org.nuxeo.ecm.platform.relations.api.Statement;
import org.nuxeo.ecm.platform.relations.api.impl.ResourceImpl;
import org.nuxeo.ecm.platform.relations.api.impl.StatementImpl;
import org.nuxeo.ecm.platform.relations.api.util.RelationConstants;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 *
 */
public class RelationsHelper {
    
	private static final Log log = LogFactory.getLog(RelationsHelper.class);

	public static DocumentModelList getOutgoingRelations(CoreSession documentManager, DocumentModel documentModel, String predicate) {
        return getRelations(documentManager, documentModel, predicate, true);
	}
	
    public static DocumentModelList getIncomingRelations(CoreSession documentManager, DocumentModel documentModel, String predicate) {
        return getRelations(documentManager, documentModel, predicate, false);
    }

    private static DocumentModelList getRelations(CoreSession documentManager, DocumentModel documentModel, String predicate, boolean outgoing) {
        try {
            RelationManager relations = Framework.getService(RelationManager.class);
            QNameResource documentResource = getDocumentResource(relations, documentModel);
            Resource predicateResource = new ResourceImpl(predicate);
            return getDocuments(relations, documentManager, documentResource, predicateResource, outgoing);
        } catch (Exception e) {
            log.error("Failed to get document relations", e);
            return null;
        }
    }
    
	public static boolean createRelation(CoreSession documentManager, DocumentModel fromModel, String predicate, DocumentModel toModel) {
		try {
			RelationManager relations = Framework.getService(RelationManager.class);
            QNameResource obj = getDocumentResource(relations, fromModel);
	        QNameResource subject = getDocumentResource(relations, toModel);
	        Resource predicateResource = new ResourceImpl(predicate);
	        Statement stmt = new StatementImpl(obj, predicateResource, subject);
	        relations.getGraphByName(RelationConstants.GRAPH_NAME).add(stmt);
	        return true;
		} catch (Exception e) {
			log.error("Failed to create document relation", e);
			return false;
		}
	}
	
	public static boolean deleteRelation(CoreSession documentManager, DocumentModel fromModel, String predicate, DocumentModel toModel) {
		try {
			RelationManager relations = Framework.getService(RelationManager.class);
	        QNameResource obj = getDocumentResource(relations, fromModel);
            QNameResource subject = getDocumentResource(relations, toModel);
	        Resource predicateResource = new ResourceImpl(predicate);
	        Statement stmt = new StatementImpl(obj, predicateResource, subject);
	        relations.getGraphByName(RelationConstants.GRAPH_NAME).remove(stmt);
	        return true;
		}
		catch (Exception e) {
			log.error("Failed to delete document relation", e);
			return false;
		}
	}

	public static boolean deleteOutgoingRelations(CoreSession documentManager, DocumentModel fromModel, String predicate) {
		try {
			RelationManager relations = Framework.getService(RelationManager.class);
	        QNameResource subject = getDocumentResource(relations, fromModel);
	        Resource predicateResource = new ResourceImpl(predicate);
	        List<Statement> outgoingStatements = getOutgoingStatements(relations, subject, predicateResource);
	        relations.getGraphByName(RelationConstants.GRAPH_NAME).remove(outgoingStatements);
	        return true;
		}
		catch (Exception e) {
			log.error("Failed to delete document relations", e);
			return false;
		}
	}
	
	public static boolean deleteIncomingRelations(CoreSession documentManager, DocumentModel fromModel, String predicate) {
		try {
			RelationManager relations = Framework.getService(RelationManager.class);
	        QNameResource subject = getDocumentResource(relations, fromModel);
	        Resource predicateResource = new ResourceImpl(predicate);
	        List<Statement> incomingStatements = getIncomingStatements(relations, subject, predicateResource);
	        relations.getGraphByName(RelationConstants.GRAPH_NAME).remove(incomingStatements);
	        return true;
		}
		catch (Exception e) {
			log.error("Failed to delete document relations", e);
			return false;
		}
	}
	
	// Methods copied from GetRelations/CreateRelation automation operations
	
	private static QNameResource getDocumentResource(RelationManager relations, DocumentModel document)
            throws ClientException {
        return (QNameResource) relations.getResource(
                RelationConstants.DOCUMENT_NAMESPACE, document, null);
    }
    
	private static DocumentModelList getDocuments(RelationManager relations, CoreSession session, QNameResource res,
            Resource predicate, boolean outgoing) throws ClientException {
        if (outgoing) {
            List<Statement> statements = getOutgoingStatements(relations, res, predicate);
            DocumentModelList docs = new DocumentModelListImpl(
                    statements.size());
            for (Statement st : statements) {
                DocumentModel dm = getDocumentModel(session, relations, st.getObject());
                if (dm != null) {
                    docs.add(dm);
                }
            }
            return docs;
        } else {
            List<Statement> statements = getIncomingStatements(relations, res, predicate);
            DocumentModelList docs = new DocumentModelListImpl(
                    statements.size());
            for (Statement st : statements) {
                DocumentModel dm = getDocumentModel(session, relations, st.getSubject());
                if (dm != null) {
                    docs.add(dm);
                }
            }
            return docs;
        }
    }
    
	private static List<Statement> getIncomingStatements(RelationManager relations, QNameResource res,
            Resource predicate) throws ClientException {
        return relations.getGraphByName(RelationConstants.GRAPH_NAME).getStatements(null,
                predicate, res);
    }

	private static List<Statement> getOutgoingStatements(RelationManager relations, QNameResource res,
            Resource predicate) throws ClientException {
        return relations.getGraphByName(RelationConstants.GRAPH_NAME).getStatements(res,
                predicate, null);
    }

	private static DocumentModel getDocumentModel(CoreSession session, RelationManager relations, Node node) throws ClientException {
        if (node.isQNameResource()) {
            QNameResource resource = (QNameResource) node;
            Map<String, Serializable> context = new HashMap<String, Serializable>();
            context.put(ResourceAdapter.CORE_SESSION_ID_CONTEXT_KEY,
                    session.getSessionId());
            Object o = relations.getResourceRepresentation(
                    resource.getNamespace(), resource, context);
            if (o instanceof DocumentModel) {
                return (DocumentModel) o;
            }
        }
        return null;
    }

}
