package org.easysoa.services;

import java.net.MalformedURLException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

public interface DocumentService {

    /**
     * Creates an Appli Impl. in the Master workspace.
     * @param session
     * @param url
     * @return
     * @throws ClientException
     * @throws MalformedURLException
     */
    public abstract DocumentModel createAppliImpl(CoreSession session, String url) throws ClientException, MalformedURLException;

    /**
     * Creates an Appli Impl. in the given workspace.
     * If no such workspace exists, it is created.
     * @param session
     * @param url
     * @param workspace
     * @return
     * @throws ClientException
     * @throws MalformedURLException
     */
    public abstract DocumentModel createAppliImpl(CoreSession session, String url, String workspace) throws ClientException, MalformedURLException;

    /**
     * 
     * @param session
     * @param parentPath Valid Service API or Appli Impl.
     * @param url
     * @return
     * @throws ClientException
     * @throws MalformedURLException 
     */
    public abstract DocumentModel createServiceAPI(CoreSession session, String parentPath, String url) throws ClientException, MalformedURLException;

    /**
     * Returns null if the service API doesn't exist.
     * 
     * @param session
     * @param parentPath Valid Service API
     * @param url
     * @return
     * @throws ClientException
     * @throws MalformedURLException 
     */
    public abstract DocumentModel createService(CoreSession session, String parentPath, String url) throws ClientException, MalformedURLException;

    /**
     * Returns null if the service API Impl doesn't exist.
     * 
     * @param session
     * @param parentPath service API Impl
     * @param archiPath
     * @return
     * @throws ClientException
     */
    public abstract DocumentModel createReference(CoreSession session, String parentPath, String title) throws ClientException;

    public abstract DocumentModel findWorkspace(CoreSession session, String name) throws ClientException;
    
    public abstract DocumentModel findEnvironment(CoreSession session, String name) throws ClientException;
    
    public abstract DocumentModel findAppliImpl(CoreSession session, String appliUrl) throws ClientException;

    public abstract DocumentModel findServiceApi(CoreSession session, String apiUrl) throws ClientException;

    public abstract DocumentModel findService(CoreSession session, String url) throws ClientException, MalformedURLException;

    public abstract DocumentModel findServiceReference(CoreSession session, String referenceArchiPath) throws ClientException;

    /**
     * Merges properties from a document to another,
     * i.e. copies properties from a source model to the destination.
     * The source document is deleted, and the destination saved.
     * @param from
     * @param to
     * @param overwrite If destination properties have to be overwritten
     * @return
     * @throws ClientException
     */
    public abstract boolean mergeDocument(CoreSession session, DocumentModel from, DocumentModel to, boolean overwrite) throws ClientException;

    public abstract String generateDocumentID(DocumentModel doc);
    
    /**
     * Returns the default Appli Impl., creates it if necessary.
     * @param session
     * @return
     * @throws ClientException
     */
    public abstract DocumentModel getDefaultAppliImpl(CoreSession session) throws ClientException;

    /**
     * Returns the default Appli Impl. in the desired workspace, creates it if necessary.
     * @param session
     * @param workspace
     * @return Never returns null
     * @throws ClientException
     */
    public abstract DocumentModel getDefaultAppliImpl(CoreSession session, String workspace) throws ClientException;
    
    /**
     * Returns the workspace in which the current document is.
     * @param session
     * @param model
     * @return The workspace or null
     * @throws Exception
     */
    public abstract DocumentModel getWorkspace(CoreSession session, DocumentModel model) throws ClientException;
    
    public abstract DocumentRef getWorkspaceRoot(CoreSession session) throws ClientException;

}