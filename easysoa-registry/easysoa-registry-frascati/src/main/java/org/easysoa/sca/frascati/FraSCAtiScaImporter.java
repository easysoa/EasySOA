package org.easysoa.sca.frascati;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.registry.frascati.FraSCAtiService;
import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.visitors.RestBindingScaVisitor;
import org.easysoa.sca.visitors.RestReferenceBindingVisitor;
import org.easysoa.sca.visitors.ScaVisitor;
import org.easysoa.sca.visitors.WSBindingScaVisitor;
import org.easysoa.sca.visitors.WSReferenceBindingVisitor;
import org.easysoa.services.DocumentService;
import org.easysoa.services.NotificationService;
import org.eclipse.stp.sca.Binding;
import org.eclipse.stp.sca.Component;
import org.eclipse.stp.sca.ComponentReference;
import org.eclipse.stp.sca.ComponentService;
import org.eclipse.stp.sca.Composite;
import org.eclipse.stp.sca.Reference;
import org.eclipse.stp.sca.Service;
import org.eclipse.stp.sca.WebServiceBinding;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;


/**
 * ScaImporter using FraSCAti SCA parser
 * 
 * About FraSCAti parsing archi : in AssemblyFactoryManager (conf'd in "composite-manager"),
 * by ScaCompositeParser/AbstractDelegateScaParser and others in between
 * and mainly in org.ow2.frascati.parser.core.ScaParser,
 * also in the appropriate Resolver, ex. JavaResolver, JavaInterfaceResolver
 * 
 * 
 * TODO get parsing errors from FraSCAti ?!
 * OK wrap as delegate ProcessingContextImpl.error/warning() (that extends ParsingContextImpl)
 * TODO and feed errors back to UI
 * 
 * DONE get FraSCAti not to aborts parsing on unknown classes (impl or itf, not in the classpath) ? (though WSDLs are OK)
 * FraSCAti archi : done in the appropriate Resolver, ex. JavaResolver, JavaInterfaceResolver
 * those could be put back to WARNING, but other failures still possible (CompositeInterface, ConstrainingType, ImplementationComposite, Reference, Promote, Autowire, Callback, Authentication...)
 * 1. easiest is to only support full SCA (zip / jar with classes and all SCAs)
 * for that, using AssemblyFactoryManager.processContribution() requires sca-contribution.xml so can't work (save if generated)
 * OK so rather using custom unzip & recursive readComposite
 * 2. intercept unfound java class errors by wrapping as delegate ProcessingContextImpl.loadClass()
 * OK works, further : possibly resolve within easysoa registry
 * though still no support of dependency between zips
 * 
 * TODO LATER to further support incomplete composites : have to know what level of incomplete, till then the other import impl can help
 * 
 * 
 * remaining TODOs : 
 * 
 * TODO implement service & reference discovery in SCA visitor :
 * OK for now only WS Service case, done in visitBinding()
 * TODO also references & REST service
 * 
 * TODO unify with original SCAImport : either remove original, or factorize business logic & unify fct 
 * TODO hook in UI and whole (release) build
 * TODO possibly extract & separate easysoa-frascati-nuxeo (original)
 * TODO refactor project (easysoa-registry(-core)-frascati ?) & package (org.easysoa.registry.frascati)
 * 
 * @author mdutoo
 *
 */
public class FraSCAtiScaImporter implements IScaImporter {

	public static final String SCA_URI = "http://www.osoa.org/xmlns/sca/1.0";
	public static final String FRASCATI_URI = "http://frascati.ow2.org/xmlns/sca/1.1";
	public static final String WSDLINSTANCE_URI = "http://www.w3.org/2004/08/wsdl-instance";
	public static final QName SCA_COMPONENT_QNAME = new QName(SCA_URI, "component");
	public static final QName SCA_SERVICE_QNAME = new QName(SCA_URI, "service");
	public static final QName SCA_REFERENCE_QNAME = new QName(SCA_URI, "reference");
	
	private static Log log = LogFactory.getLog(FraSCAtiScaImporter.class);

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

	private /*@Inject*/ FraSCAtiService frascatiService; // TODO better Inject doesn't work outside tests ?!
    
	
	public FraSCAtiScaImporter(CoreSession documentManager, Blob compositeFile) throws ClientException {
		this.documentManager = documentManager;
		this.compositeFile = compositeFile;
		this.parentAppliImplModel = Framework.getRuntime().getService(DocumentService.class)
				.getDefaultAppliImpl(documentManager);
		init();
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
	

    private void visitComposite(Composite scaComposite)
    {
      System.out.println(scaComposite);
      System.out.println("");

      System.out.println("Composite.name=" + scaComposite.getName());
      for(Service service : scaComposite.getService()) {
        System.out.println("Service.name=" + service.getName());
		getArchiNameStack().push(service.getName());
        for(Binding binding : service.getBinding()) {
          try {
			visitBinding(binding);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
  		getArchiNameStack().pop();
      }
      for(Reference reference : scaComposite.getReference()) {
        System.out.println("Reference.name=" + reference.getName());
		getArchiNameStack().push(reference.getName());
        for(Binding binding : reference.getBinding()) {
          try {
			/////visitBinding(binding);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
		getArchiNameStack().pop();
      }
      for(Component component : scaComposite.getComponent()) {
        System.out.println("Component.name=" + component.getName());
		getArchiNameStack().push(component.getName());
        for(ComponentService componentService : component.getService()) {
          System.out.println("ComponentService.name=" + componentService.getName());
    	  getArchiNameStack().push(componentService.getName());
          for(Binding binding : componentService.getBinding()) {
            try {
				visitBinding(binding);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          }
  		getArchiNameStack().pop();
        }
        for(ComponentReference componentReference : component.getReference()) {
          System.out.println("ComponentReference.name=" + componentReference.getName());
    	  getArchiNameStack().push(componentReference.getName());
          for(Binding binding : componentReference.getBinding()) {
            try {
				/////visitBinding(binding);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          }
  		  getArchiNameStack().pop();
        }
		getArchiNameStack().pop();
      }
    }
	protected String getBindingUrl(Binding binding) {
		String serviceUrl = binding.getUri();
		if (serviceUrl == null) {
			if (binding instanceof WebServiceBinding) {
				List<String> wsdlLocations = ((WebServiceBinding) binding).getWsdlLocation();
				if (wsdlLocations != null && wsdlLocations.size() != 0) {
					serviceUrl = wsdlLocations.get(0).replace("?wsdl", "");
				}
			}
		}
		return serviceUrl;
	}

    private void visitBinding(Binding binding) throws Exception
    {
      System.out.println("Binding.name=" + binding.getName());

      // init
		NotificationService notificationService = Framework.getRuntime().getService(NotificationService.class);
		FraSCAtiScaImporter scaImporter = this;
		
		
		String serviceUrl = getBindingUrl(binding);
		if (serviceUrl == null) {
			// TODO error
			return;
		}
		
		String appliImplUrl = (String) scaImporter.getParentAppliImplModel().getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL);
		String apiUrl = notificationService.computeApiUrl(appliImplUrl, scaImporter.getServiceStackUrl(), serviceUrl);
		
		// enrich or create API
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(ServiceAPI.PROP_URL, apiUrl);
		properties.put(ServiceAPI.PROP_TITLE, scaImporter.getServiceStackType()); // TODO better, ex. from composite name...
		properties.put(ServiceAPI.PROP_DTIMPORT, scaImporter.getCompositeFile().getFilename());
		properties.put(ServiceAPI.PROP_PARENTURL, appliImplUrl);
		notificationService.notifyServiceApi(documentManager, properties);

		// enrich or create service
		properties = new HashMap<String, String>();
		properties.put(org.easysoa.doctypes.Service.PROP_URL, serviceUrl);
		properties.put(org.easysoa.doctypes.Service.PROP_TITLE, scaImporter.getCurrentArchiName());
		properties.put(org.easysoa.doctypes.Service.PROP_PARENTURL, apiUrl);
		properties.put(org.easysoa.doctypes.Service.PROP_ARCHIPATH, scaImporter.toCurrentArchiPath());
		properties.put(org.easysoa.doctypes.Service.PROP_ARCHILOCALNAME, scaImporter.getCurrentArchiName());
		properties.put(org.easysoa.doctypes.Service.PROP_DTIMPORT, scaImporter.getCompositeFile().getFilename()); // TODO also upload and link to it ?
		notificationService.notifyService(documentManager, properties);
		
		
    }

    /**
     * requires all ref'd composites and classes to be there
     * @throws Exception
     */
	public void TODOimportSCA() throws /*IOException, XMLStreamException, Client*/Exception {
		String scaFileName = compositeFile.getFilename();
		if (scaFileName.endsWith(".composite")) {
			importSCAComposite();
		} else if (scaFileName.endsWith(".jar") || scaFileName.endsWith(".zip")) {
			importSCAZip();
		} else {
			throw new Exception("Unsupported file type : neither a Composite nor an SCA zip or jar");
		}
	}

    /**
     * requires all ref'd composites and classes to be there
     * TODO rather replace by old XML one ??
     * @throws Exception
     */
	public void importSCAZip() throws /*IOException, XMLStreamException, Client*/Exception {

		// Initialization
		File scaZipTmpFile = File.createTempFile(IdUtils.generateStringId(), ".jar");
		compositeFile.transferTo(scaZipTmpFile);

		// Read an (Eclipse SOA) SCA composite then visit it
		frascatiService = Framework.getService(FraSCAtiService.class); // TODO in init
		Set<Composite> scaZipCompositeSet = frascatiService.readScaZip(scaZipTmpFile);
		for (Composite scaZipComposite : scaZipCompositeSet) {
			visitComposite(scaZipComposite);
		}
		
	}

	public void importSCAComposite() throws /*IOException, XMLStreamException, Client*/Exception {

		// Initialization
		File compositeTmpFile = File.createTempFile(IdUtils.generateStringId(), ".composite");
		compositeFile.transferTo(compositeTmpFile);

		// Read an (Eclipse SOA) SCA composite then visit it
		frascatiService = Framework.getService(FraSCAtiService.class); // TODO in init
		Composite scaZipComposite = frascatiService.readComposite(compositeTmpFile.toURI().toURL());
		visitComposite(scaZipComposite);
	}
	
	// OLD TODO use it to see what else did XML parsing,
	// and what it should have but couldn't (but FraSCAti maybe could)
	public void importSCAOld() throws IOException, XMLStreamException, ClientException {

		// Initialization
		//File compositeTmpFile = File.createTempFile(IdUtils.generateStringId(), ".composite");
		File compositeTmpFile = File.createTempFile(IdUtils.generateStringId(), ".jar");
		compositeFile.transferTo(compositeTmpFile);
		
		// Parsing using StAX (to get both SAX & DOM parsing benefits)
		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		compositeReader = xmlif.createXMLStreamReader(new FileInputStream(compositeTmpFile));
		
		while(compositeReader.hasNext()) {
			compositeReader.next();

			/*if (compositeReader.getEventType() == XMLEvent.START_ELEMENT) {
				ScaVisitor elementVisitor = elementQnameToScaVisitor.get(compositeReader.getName());
				if (elementVisitor != null) {
					elementVisitor.visit();
				}
			}*/
			
			if (compositeReader.getEventType() == XMLEvent.START_ELEMENT) {
				String name = compositeReader.getAttributeValue(null, "name"); // rather than "" ?! // TODO SCA_URI
				QName elementName = compositeReader.getName();
				
				if (elementName.equals(SCA_COMPONENT_QNAME)) {
					// component !
					getArchiNameStack().push(name);

				/*} else if (compositeReader.getLocalName().startsWith("implementation.")) {
					// implementation !
					if (compositeReader.getLocalName().equals("implementation.composite")) {
						//String compositeName = compositeReader.getAttributeValue(null, "name"); // rather than "" ?! // TODO SCA_URI
						//TODO visitComposite BUT CAN'T SINCE ONLY ONE SCA FILE HAS BEEN UPLOADED
						// so TODO alts : upload zip, use scm connector, export button from eclipse... 
					}*/

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
			try {
				scaVisitor.postCheck();
			} catch (Exception ex) {
				log.error("Error while postChecking scaVisitor " + scaVisitor.getDescription()
						+ " in SCA composite file " + compositeFile.getFilename(), ex);
			}
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

	private void init() {
		elementQnameToScaVisitor = new HashMap<QName, ScaVisitor>();
		elementQnameToScaVisitor.put(SCA_SERVICE_QNAME, null);
		elementQnameToScaVisitor.put(SCA_REFERENCE_QNAME, null);
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
				} catch (Exception ex) {
					log.error("Error when visiting binding " + bindingVisitor.getDescription()
							+ " at archi path " + toCurrentArchiPath()
							+ " in SCA composite file " + compositeFile.getFilename(), ex);
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
