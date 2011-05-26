package org.easysoa.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.DirectoryServiceImpl;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.api.Framework;

/**
 * Helpers for managing vocabularies.
 * 
 * @author mkalam-alami
 *
 */
public class VocabularyService {

	private static final Log log = LogFactory.getLog(VocabularyService.class);
	private static final DirectoryServiceImpl dirService = (DirectoryServiceImpl) Framework
			.getRuntime().getComponent(DirectoryService.NAME);

	/**
	 * Checks if entry ID exists in a vocabulary.
	 * @param session
	 * @param vocabularyName
	 * @param entryId
	 * @throws DirectoryException
	 * @throws ClientException
	 */
	public static boolean entryExists(CoreSession session,
			String vocabularyName, String entryId) throws DirectoryException,
			ClientException {
		Session dirSession = dirService.open(vocabularyName);

		for (DocumentModel model : dirSession.getEntries()) {
			if (model.getId().equals(entryId)) {
				dirSession.close();
				return true;
			}
		}

		dirSession.close();
		return false;
	}

	/**
	 * Adds an entry to a vocabulary.
	 * @param session
	 * @param vocabularyName
	 * @param entryId
	 * @param entryName
	 */
	public static void addEntry(CoreSession session, String vocabularyName,
			String entryId, String entryName) {
		addEntry(session, vocabularyName, entryId, entryName, null);
	}

	/**
	 * Adds an entry to a child vocabulary.
	 * @param session
	 * @param vocabularyName
	 * @param entryId
	 * @param entryName
	 * @param parentId
	 */
	public static void addEntry(CoreSession session, String vocabularyName,
			String entryId, String entryName, String parentId) {
		try {
			if (entryExists(session, vocabularyName, entryId)) {
				return;
			}
			log.info("New " + vocabularyName + " vocabulary entry: "
					+ entryName);

			Session dirSession = dirService.open(vocabularyName);

			Map<String, Object> fieldMap = new HashMap<String, Object>();
			fieldMap.put("id", entryId);
			fieldMap.put("label", entryName);
			if (parentId != null) {
				fieldMap.put("parent", parentId);
			}
			dirSession.createEntry(fieldMap);

			dirSession.close();
		} catch (Exception e1) {
			log.error("Error while creating vocabulary entry", e1);
		}
	}

	/**
	 * Adds a list of entries to a vocabulary, using the name as ID.
	 * @param session
	 * @param vocabularyName
	 * @param entryNames
	 */
	public static void addEntries(CoreSession session, String vocabularyName,
			List<String> entryNames) {
		try {
			Session dirSession = dirService.open(vocabularyName);

			for (String entryName : entryNames) {
				Map<String, Object> fieldMap = new HashMap<String, Object>();
				fieldMap.put("id", entryName);
				fieldMap.put("label", entryName);
				dirSession.createEntry(fieldMap);
			}

			dirSession.close();
		} catch (Exception e1) {
			log.error("Error while creating vocabulary entry", e1);
		}
	}

	/**
	 * Removes en entry from a vocabulary.
	 * @param session
	 * @param vocabularyName
	 * @param entryId
	 */
	public static void removeEntry(CoreSession session, String vocabularyName,
			String entryId) {
		try {
			Session dirSession = dirService.open(vocabularyName);
			DocumentModel model = dirSession.getEntry(entryId);
			if (model != null)
				dirSession.deleteEntry(dirSession.getEntry(entryId));
		} catch (Exception e1) {
			log.error("Error while removing '" + entryId + "' from vocabulary "
					+ vocabularyName, e1);
		}
	}

	/**
	 * Empties a vocabulary.
	 * @param session
	 * @param vocabularyName
	 */
	public static void removeAllEntries(CoreSession session,
			String vocabularyName) {
		try {
			Session dirSession = dirService.open(vocabularyName);
			for (DocumentModel model : dirSession.getEntries())
				dirSession.deleteEntry(model);
		} catch (Exception e1) {
			log.error("Error while clearing vocabulary " + vocabularyName, e1);
		}
	}
}