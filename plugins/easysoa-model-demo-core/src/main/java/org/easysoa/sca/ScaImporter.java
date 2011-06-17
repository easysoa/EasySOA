package org.easysoa.sca;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.DocumentService;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ScaImporter {

	public static final String SCA_URI = "http://www.osoa.org/xmlns/sca/1.0";
	public static final String FRASCATI_URI = "http://frascati.ow2.org/xmlns/sca/1.1";
	public static final String WSDLINSTANCE_URI = "http://www.w3.org/2004/08/wsdl-instance";
	public static final QName SCA_SERVICE_QNAME = new QName(SCA_URI, "service");
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(ScaImporter.class);

	private CoreSession documentManager;
	private Blob compositeFile;
	private String serviceStackType;
	private String serviceStackUrl;
	private DocumentModel parentAppliImplModel;
	
	public ScaImporter(CoreSession documentManager, Blob compositeFile) throws ClientException {
		this.documentManager = documentManager;
		this.compositeFile = compositeFile;
		this.parentAppliImplModel = DocumentService.getDefaultAppliImpl(documentManager);
	}

	public List<ScaVisitor> createScaVisitors() {
		List<ScaVisitor> visitors = new ArrayList<ScaVisitor>();
		visitors.add(new WSBindingScaVisitor(documentManager, parentAppliImplModel, serviceStackType, serviceStackUrl));
		visitors.add(new RestBindingScaVisitor(documentManager, parentAppliImplModel, serviceStackType, serviceStackUrl));
		return visitors;
	}

	public void importSCA() throws IOException, XMLStreamException, ClientException {

		// Initialization
		File compositeTmpFile = File.createTempFile(IdUtils.generateStringId(), ".composite");
		compositeFile.transferTo(compositeTmpFile);
		
		// Parsing using StAX (to get both SAX & DOM parsing benefits)
		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		XMLStreamReader compositeReader = xmlif.createXMLStreamReader(new FileInputStream(compositeTmpFile));
		
		while(compositeReader.hasNext()) {
			if (compositeReader.next() == XMLEvent.START_ELEMENT
					&& compositeReader.getName().equals(SCA_SERVICE_QNAME)) {
				// service !
				String serviceName = compositeReader.getAttributeValue("", "name"); // TODO SCA_URI
				
				while (compositeReader.next() != XMLEvent.END_ELEMENT
						|| !compositeReader.getName().equals(SCA_SERVICE_QNAME)) {
					if (compositeReader.getEventType() == XMLEvent.START_ELEMENT
							&& compositeReader.getLocalName().startsWith("binding.")) {
						// binding !
						acceptBindingVisitors(compositeReader, serviceName);
					}
				}
			}
		}

		documentManager.save();
	}

	public void setServiceStackType(String serviceStackType) {
		this.serviceStackType = serviceStackType;
	}

	public void setParentAppliImpl(DocumentModel parentAppliImplModel) {
		this.parentAppliImplModel = parentAppliImplModel;
	}

	public void setServiceStackUrl(String serviceStackUrl) {
		this.serviceStackUrl = serviceStackUrl;
	}

	/**
	 * Guesses an API url given the service URL and others
	 * XXX: Currently, is somehow hacky and doesn't always work as expected
	 * @param serviceUrl
	 * @param appliImplUrl
	 * @param serviceStackUrl
	 * @return
	 */
	public static String getApiUrl(String serviceUrl, String appliImplUrl, String serviceStackUrl) {
		
		int apiPathIndex = -1;
		 
		String apiPath = serviceStackUrl; // TODO  + "/" + serviceStackUrl + "/"
		if (appliImplUrl != null) {
			apiPath = appliImplUrl + apiPath;
			apiPathIndex = serviceUrl.indexOf(apiPath);
		}
		
		if (apiPathIndex == -1) {
			apiPathIndex = serviceUrl.lastIndexOf('/');
		}
		
		return serviceUrl.substring(0, apiPathIndex + apiPath.length()); // TODO http://localhost:9000/hrestSoapProxyWSIntern
	}

	private void acceptBindingVisitors(XMLStreamReader compositeReader, String serviceName) throws ClientException {
		QName bindingQName = compositeReader.getName();
		for (ScaVisitor scaVisitor : createScaVisitors()) {
			if (scaVisitor.isOkFor(bindingQName)) {
				scaVisitor.visit(compositeReader, serviceName);
				// TODO for now, whether it visits children is done in handle()
			}
		}
	}	

}
