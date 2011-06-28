package org.easysoa.sca;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

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


/**
 * Imports an SCA composite file.
 * 
 * Supports partial import if errors.
 * 
 * TODO support inclusion (implementation.composite, or even other impls). NB. not possible without other files
 * TODO support advanced linking : promote (for service & reference), wire
 * @author mkalam-alami, jguillemotte, mdutoo
 *
 */
public class ScaImporter {

	public static final String SCA_URI = "http://www.osoa.org/xmlns/sca/1.0";
	public static final String FRASCATI_URI = "http://frascati.ow2.org/xmlns/sca/1.1";
	public static final String WSDLINSTANCE_URI = "http://www.w3.org/2004/08/wsdl-instance";
	public static final QName SCA_COMPONENT_QNAME = new QName(SCA_URI, "component");
	public static final QName SCA_SERVICE_QNAME = new QName(SCA_URI, "service");
	public static final QName SCA_REFERENCE_QNAME = new QName(SCA_URI, "reference");
	
	private static final Log log = LogFactory.getLog(ScaImporter.class);

	private CoreSession documentManager;
	private Blob compositeFile;
	private String serviceStackType;
	private String serviceStackUrl;
	private DocumentModel parentAppliImplModel;
	
	private XMLStreamReader compositeReader;
	//private List<ScaVisitor> serviceBindingVisitors;
	//private List<ScaVisitor> referenceBindingVisitors;
	private HashMap<QName, ScaVisitor> elementQnameToScaVisitor;
	private Stack<String> archiNameStack = new Stack<String>();
	private ArrayList<ScaVisitor> scaVisitorsToPostCheck = new ArrayList<ScaVisitor>();
	
	
	public ScaImporter(CoreSession documentManager, Blob compositeFile) throws ClientException {
		this.documentManager = documentManager;
		this.compositeFile = compositeFile;
		this.parentAppliImplModel = DocumentService.getDefaultAppliImpl(documentManager);
		
		init();
	}

	private void init() {
		elementQnameToScaVisitor = new HashMap<QName, ScaVisitor>();
		elementQnameToScaVisitor.put(SCA_SERVICE_QNAME, null);
		elementQnameToScaVisitor.put(SCA_REFERENCE_QNAME, null);
	}
	 
	public List<ScaVisitor> createServiceBindingVisitors() {
		ArrayList<ScaVisitor> serviceBindingVisitors = new ArrayList<ScaVisitor>();
		serviceBindingVisitors.add(new WSBindingScaVisitor(this));
		serviceBindingVisitors.add(new RestBindingScaVisitor(this));
		return serviceBindingVisitors;
	}
	 
	public List<ScaVisitor> createReferenceBindingVisitors() {
		ArrayList<ScaVisitor> serviceBindingVisitors = new ArrayList<ScaVisitor>();
		serviceBindingVisitors.add(new WSReferenceBindingVisitor(this));
		serviceBindingVisitors.add(new RestReferenceBindingVisitor(this));
		return serviceBindingVisitors;
	}

	public void importSCA() throws IOException, XMLStreamException, ClientException {

		// Initialization
		File compositeTmpFile = File.createTempFile(IdUtils.generateStringId(), ".composite");
		compositeFile.transferTo(compositeTmpFile);
		
		// Parsing using StAX (to get both SAX & DOM parsing benefits)
		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		compositeReader = xmlif.createXMLStreamReader(new FileInputStream(compositeTmpFile));
		
		while(compositeReader.hasNext()) {
			compositeReader.next();

			if (compositeReader.getEventType() == XMLEvent.START_ELEMENT) {
				ScaVisitor elementVisitor = elementQnameToScaVisitor.get(compositeReader.getName());
				if (elementVisitor != null) {
					elementVisitor.visit();
				}
			}
			
			if (compositeReader.getEventType() == XMLEvent.START_ELEMENT) {
				String name = compositeReader.getAttributeValue(null, "name"); // rather than "" ?! // TODO SCA_URI
				QName elementName = compositeReader.getName();
				
				if (elementName.equals(SCA_COMPONENT_QNAME)) {
					// component !
					getArchiNameStack().push(name);

				} else if (compositeReader.getLocalName().startsWith("implementation.")) {
					// implementation !
					if (compositeReader.getLocalName().equals("implementation.composite")) {
						String compositeName = compositeReader.getAttributeValue(null, "name"); // rather than "" ?! // TODO SCA_URI
						//TODO visitComposite BUT CAN'T SINCE ONLY ONE SCA FILE HAS BEEN UPLOADED
						// so TODO alts : upload zip, use scm connector, export button from eclipse... 
					}

				} else if (elementName.equals(SCA_SERVICE_QNAME)) {
					// service !
					getArchiNameStack().push(name);
					acceptBindingParentVisitors(compositeReader,
							SCA_SERVICE_QNAME, createServiceBindingVisitors());
					getArchiNameStack().pop();
					
				} else if (elementName.equals(SCA_REFERENCE_QNAME)) {
					// reference !
					getArchiNameStack().push(name);
					acceptBindingParentVisitors(compositeReader,
							SCA_REFERENCE_QNAME, createReferenceBindingVisitors());
					getArchiNameStack().pop();
				}
				
			} else if (compositeReader.getEventType() == XMLEvent.END_ELEMENT) {
				if (compositeReader.getName().equals(SCA_COMPONENT_QNAME)) {
					// component !
					getArchiNameStack().pop();
				}
			}
		}
		
		documentManager.save(); // required else saved documents won't be resolved in postCheck
		
		// post check
		for (ScaVisitor scaVisitor : scaVisitorsToPostCheck) {
			scaVisitor.postCheck();
		}

		documentManager.save(); // NB. only required for additional, external code
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
		if (serviceUrl.contains(appliImplUrl + apiPath)) {
			apiPathIndex = serviceUrl.indexOf(appliImplUrl + apiPath);
		}
		
		if (apiPathIndex == -1) {
			apiPathIndex = serviceUrl.lastIndexOf('/');
		}
		
		return serviceUrl.substring(0, apiPathIndex + apiPath.length()); // TODO http://localhost:9000/hrestSoapProxyWSIntern
	}

	private void acceptBindingParentVisitors(XMLStreamReader compositeReader,
			QName scaQname, List<ScaVisitor> bindingVisitors)
			throws XMLStreamException, ClientException {
		while (compositeReader.next() != XMLEvent.END_ELEMENT
				|| !compositeReader.getName().equals(scaQname)) {
			if (compositeReader.getEventType() == XMLEvent.START_ELEMENT
					&& compositeReader.getLocalName().startsWith("binding.")) {
				// binding !
				acceptBindingVisitors(compositeReader, bindingVisitors);
			}
		}
	}

	private void acceptBindingVisitors(XMLStreamReader compositeReader,
			List<ScaVisitor> bindingVisitors) throws ClientException {
		QName bindingQName = compositeReader.getName();
		for (ScaVisitor bindingVisitor : bindingVisitors) {
			if (bindingVisitor.isOkFor(bindingQName)) {
				try {
					bindingVisitor.visit();
					scaVisitorsToPostCheck.add(bindingVisitor);
				} catch (ClientException cex) {
					log.error("Error when importing binding " + bindingVisitor
							+ " at archi path " + toCurrentArchiPath()
							+ " in SCA composite file " + compositeFile.getFilename(), cex);
				}
			}
		}
	}	
	


	public String getCurrentArchiName() {
		return getArchiNameStack().peek();
	}

	public String toCurrentArchiPath() {
		StringBuffer sbuf = new StringBuffer();
		for (String archiName : getArchiNameStack()) {
			sbuf.append("/");
			sbuf.append(archiName);
		}
		return sbuf.toString();
	}
	

	public CoreSession getDocumentManager() {
		return documentManager;
	}

	public Blob getCompositeFile() {
		return compositeFile;
	}

	public String getServiceStackType() {
		return serviceStackType;
	}

	public String getServiceStackUrl() {
		return serviceStackUrl;
	}

	public DocumentModel getParentAppliImplModel() {
		return parentAppliImplModel;
	}

	public Stack<String> getArchiNameStack() {
		return archiNameStack;
	}

	public XMLStreamReader getCompositeReader() {
		return compositeReader;
	}

}
