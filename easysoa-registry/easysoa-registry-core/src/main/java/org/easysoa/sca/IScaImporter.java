package org.easysoa.sca;

import javax.xml.stream.XMLStreamReader;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;


/**
 * Introduced to ease adding another (notably FraSCAti-based) SCA importer in addition to the
 * original XML-based one.
 * 
 * @author mdutoo
 *
 */
public interface IScaImporter {

	public CoreSession getDocumentManager();
	/** TODO alas still a tight coupling with the original XML-based importer */
	public XMLStreamReader getCompositeReader();

	public Blob getCompositeFile();
	public String getServiceStackType();
	public String getServiceStackUrl();
	public DocumentModel getParentAppliImplModel();

	public String getCurrentArchiName();
	public String toCurrentArchiPath();
}
