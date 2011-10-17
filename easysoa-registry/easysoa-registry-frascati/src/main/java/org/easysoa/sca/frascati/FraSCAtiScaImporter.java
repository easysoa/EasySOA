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
 * Contact : easysoa-dev@groups.google.com
 */

package org.easysoa.sca.frascati;

import java.io.File;
import java.net.MalformedURLException;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import javax.xml.namespace.QName;
//import javax.xml.stream.XMLInputFactory;
//import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
//import javax.xml.stream.events.XMLEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.doctypes.ServiceReference;
import org.easysoa.registry.frascati.FraSCAtiService;
import org.easysoa.sca.IScaImporter;
//import org.easysoa.sca.XMLScaImporter;
//import org.easysoa.sca.XMLScaImporter;
//import org.easysoa.sca.visitors.RestBindingScaVisitor;
//import org.easysoa.sca.visitors.RestReferenceBindingVisitor;
//import org.easysoa.sca.visitors.ScaVisitor;
//import org.easysoa.sca.visitors.WSBindingScaVisitor;
//import org.easysoa.sca.visitors.WSReferenceBindingVisitor;
import org.easysoa.services.DocumentService;
import org.easysoa.services.NotificationService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.stp.sca.Binding;
import org.eclipse.stp.sca.Component;
import org.eclipse.stp.sca.ComponentReference;
import org.eclipse.stp.sca.ComponentService;
import org.eclipse.stp.sca.Composite;
import org.eclipse.stp.sca.Reference;
import org.eclipse.stp.sca.Service;
import org.eclipse.stp.sca.WebServiceBinding;
import org.eclipse.stp.sca.impl.ComponentReferenceImpl;
import org.eclipse.stp.sca.impl.ComponentServiceImpl;
import org.eclipse.stp.sca.impl.ServiceImpl;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

/**
 * ScaImporter using FraSCAti SCA parser
 * 
 * About FraSCAti parsing archi : in AssemblyFactoryManager (conf'd in
 * "composite-manager"), by ScaCompositeParser/AbstractDelegateScaParser and
 * others in between and mainly in org.ow2.frascati.parser.core.ScaParser, also
 * in the appropriate Resolver, ex. JavaResolver, JavaInterfaceResolver
 * 
 * 
 * TODO get parsing errors from FraSCAti ?! OK wrap as delegate
 * ProcessingContextImpl.error/warning() (that extends ParsingContextImpl) TODO
 * and feed errors back to UI
 * 
 * DONE get FraSCAti not to aborts parsing on unknown classes (impl or itf, not
 * in the classpath) ? (though WSDLs are OK) FraSCAti archi : done in the
 * appropriate Resolver, ex. JavaResolver, JavaInterfaceResolver those could be
 * put back to WARNING, but other failures still possible (CompositeInterface,
 * ConstrainingType, ImplementationComposite, Reference, Promote, Autowire,
 * Callback, Authentication...) 1. easiest is to only support full SCA (zip /
 * jar with classes and all SCAs) for that, using
 * AssemblyFactoryManager.processContribution() requires sca-contribution.xml so
 * can't work (save if generated) OK so rather using custom unzip & recursive
 * readComposite 2. intercept unfound java class errors by wrapping as delegate
 * ProcessingContextImpl.loadClass() OK works, further : possibly resolve within
 * easysoa registry though still no support of dependency between zips
 * 
 * TODO LATER to further support incomplete composites : have to know what level
 * of incomplete, till then the other import impl can help
 * 
 * 
 * remaining TODOs :
 * 
 * TODO implement service & reference discovery in SCA visitor : OK for now only WS Service case, done in visitBinding() 
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

	//private XMLStreamReader compositeReader;
	// private List<ScaVisitor> serviceBindingVisitors;
	// private List<ScaVisitor> referenceBindingVisitors;
	//private HashMap<QName, ScaVisitor> elementQnameToScaVisitor;
	private Stack<String> archiNameStack = new Stack<String>();
	private Stack<EObject> bindingStack = new Stack<EObject>();
	//private ArrayList<ScaVisitor> scaVisitorsToPostCheck = new ArrayList<ScaVisitor>();

	private/* @Inject */FraSCAtiService frascatiService; // TODO better Inject doesn't work outside tests ?!

	/**
	 * Constructor
	 * @param documentManager Nuxeo document manager
	 * @param compositeFile Composite file to import
	 * @throws ClientException 
	 */
	public FraSCAtiScaImporter(CoreSession documentManager, Blob compositeFile) throws ClientException {
		this.documentManager = documentManager;
		this.compositeFile = compositeFile;
		this.parentAppliImplModel = Framework.getRuntime().getService(DocumentService.class).getDefaultAppliImpl(documentManager);
		/*init();*/
	}

	/**
	 * Creates a list of service binding visitors
	 * @return A <code>List</code> of service binding visitors
	 */
	/*
	public List<ScaVisitor> createServiceBindingVisitors() {
		ArrayList<ScaVisitor> serviceBindingVisitors = new ArrayList<ScaVisitor>();
		serviceBindingVisitors.add(new WSBindingScaVisitor(this));
		serviceBindingVisitors.add(new RestBindingScaVisitor(this));
		return serviceBindingVisitors;
	}*/
	
	/**
	 * Creates a list of reference binding visitors
	 * @return A <code>List</code> of reference binding visitors
	 */
	/*
	public List<ScaVisitor> createReferenceBindingVisitors() {
		ArrayList<ScaVisitor> serviceBindingVisitors = new ArrayList<ScaVisitor>();
		serviceBindingVisitors.add(new WSReferenceBindingVisitor(this));
		serviceBindingVisitors.add(new RestReferenceBindingVisitor(this));
		return serviceBindingVisitors;
	}*/

	/**
	 * Visit the composite
	 * @param scaComposite the composite to visit
	 */
	private void visitComposite(Composite scaComposite) {
		log.debug("********* visiting composite ***** ! ");
		log.debug("scaComposite" + scaComposite);
		log.debug("Composite.name=" + scaComposite.getName());
		// Get services
		for (Service service : scaComposite.getService()) {
			log.debug("Service.name=" + service.getName());
			getArchiNameStack().push(service.getName());
			getBindingStack().push(service);
			for (Binding binding : service.getBinding()) {
				try {
					visitBinding(binding);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			getArchiNameStack().pop();
			getBindingStack().pop();
		}
		// Get references
		for (Reference reference : scaComposite.getReference()) {
			log.debug("Reference.name=" + reference.getName());
			getArchiNameStack().push(reference.getName());
			getBindingStack().push(reference);
			for (Binding binding : reference.getBinding()) {
				try {
					visitBinding(binding);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			getArchiNameStack().pop();
			getBindingStack().pop();
		}
		// Get components
		for (Component component : scaComposite.getComponent()) {
			log.debug("Component.name=" + component.getName());
			getArchiNameStack().push(component.getName());
			getBindingStack().push(component);
			// Get component services
			for (ComponentService componentService : component.getService()) {
				log.debug("ComponentService.name="	+ componentService.getName());
				getArchiNameStack().push(componentService.getName());
				getBindingStack().push(componentService);
				for (Binding binding : componentService.getBinding()) {
					try {
						visitBinding(binding);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				getArchiNameStack().pop();
				getBindingStack().pop();
			}
			// Get component references
			for (ComponentReference componentReference : component.getReference()) {
				log.debug("ComponentReference.name=" + componentReference.getName());
				getArchiNameStack().push(componentReference.getName());
				getBindingStack().push(componentReference);
				for (Binding binding : componentReference.getBinding()) {
					try {
						visitBinding(binding);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				getArchiNameStack().pop();
				getBindingStack().pop();
			}
			getArchiNameStack().pop();
			getBindingStack().pop();
		}
	}

	/**
	 * 
	 * @param binding
	 * @return
	 */
	// TODO Complete this method for other binding like REST binding
	/*protected String getBindingUrl(Binding binding) {
		// TODO merge with getBindingUrl() through Stack<EObject>
		log.debug("Binding EObject : " +  binding.eClass());
		String serviceUrl = binding.getUri();
		log.debug("currentArchiName : " + getCurrentArchiName());
		if (serviceUrl == null) {
			log.debug("getBinding(), binding class type => " + binding);
			if (binding instanceof WebServiceBinding) {
				log.debug("instance of WebServiceBinding...");
				List<String> wsdlLocations = ((WebServiceBinding) binding).getWsdlLocation();
				if (wsdlLocations != null && wsdlLocations.size() != 0) {
					serviceUrl = wsdlLocations.get(0).replace("?wsdl", "");
				}
			}
			//eg :
			//else if (binding instanceof HttpServiceBinding) {
			//	problem ... Added a rest binding in the RestSoapProxyComposite
			//	...
			//}
		}
		log.debug("serviceUrl : " + serviceUrl);
		return serviceUrl;
	}*/
	
	@Override
    public String getBindingUrl() {
    	log.debug("$$$$ current binding : " + getCurrentBinding());
    	String serviceUrl = null;
    	Binding binding = null;
    	if(getCurrentBinding() instanceof ComponentService){
    		ComponentService componentService = (ComponentService) getCurrentBinding();
    		binding = componentService.getBinding().get(0);
    		serviceUrl = binding.getUri();
    	} else if(getCurrentBinding() instanceof Service){
    		Service service = (Service) getCurrentBinding();
    		binding = service.getBinding().get(0);
    		serviceUrl = binding.getUri();
    	} else if(getCurrentBinding() instanceof ComponentReference){
    		ComponentReference componentReference = (ComponentReference) getCurrentBinding();
    		binding = componentReference.getBinding().get(0);
    		log.debug("binding name => " + binding.getName());
    		serviceUrl = binding.getUri();
    	}
    	if(serviceUrl == null){
			log.debug("%%%%%% getBinding(), binding class type => " + binding);
			if (binding instanceof WebServiceBinding) {
				List<String> wsdlLocations = ((WebServiceBinding) binding).getWsdlLocation();
				if (wsdlLocations != null && wsdlLocations.size() != 0) {
					serviceUrl = wsdlLocations.get(0).replace("?wsdl", "");
				}
			}
			// TODO how to get REST Bindings ....
			// And how to register the bindings ...
    	}
    	//log.debug("serviceUrl => " + serviceUrl);
    	return serviceUrl; // TODO put here methods from REST & SOAP service visitors
    	//log.debug("getBindingUrl");
        /*
    	String serviceUrl = compositeReader.getAttributeValue(null, "uri"); // rather than "" ?! // TODO SCA_URI
        if (serviceUrl == null) {
            String wsdlLocation = compositeReader.getAttributeValue(XMLScaImporter.WSDLINSTANCE_URI , "wsdlLocation");
            if (wsdlLocation != null) {
                serviceUrl = wsdlLocation.replace("?wsdl", "");
            }
        }
        return serviceUrl;*/
    }

	/**
	 * 
	 * @param binding
	 * @throws Exception
	 */
	private void visitBinding(Binding binding) throws Exception {
		log.debug("###### visiting binding ##### ! ");
		// binding name is not mandatory in the composite file
		//log.debug("Binding.name = " + binding.getName());

		// init
		NotificationService notificationService = Framework.getRuntime().getService(NotificationService.class);
		FraSCAtiScaImporter scaImporter = this;

		//String serviceUrl = getBindingUrl(binding);
		String serviceUrl = getBindingUrl();
		if (serviceUrl == null) {
			log.debug("serviceUrl is null, method visitBinding returns");
			// TODO error
			return;
		}
		log.debug("serviceUrl is not null (" + serviceUrl + "), registering services");
		
		//String appliImplUrl = (String) scaImporter.getParentAppliImplModel().getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL);
		String apiUrl = notificationService.computeApiUrl(scaImporter.getAppliImplUrl(),scaImporter.getServiceStackUrl(), serviceUrl);

		// Enrich or create API
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(ServiceAPI.PROP_URL, apiUrl);
		properties.put(ServiceAPI.PROP_TITLE, scaImporter.getServiceStackType()); // TODO better, ex. from composite name...
		properties.put(ServiceAPI.PROP_DTIMPORT, scaImporter.getCompositeFile().getFilename());
		properties.put(ServiceAPI.PROP_PARENTURL, scaImporter.getAppliImplUrl());
		log.debug("properties before notify serviceAPI : " + properties);
		notificationService.notifyServiceApi(documentManager, properties);

		// Enrich or create service
		properties = new HashMap<String, String>();
		properties.put(org.easysoa.doctypes.Service.PROP_URL, serviceUrl);
		properties.put(org.easysoa.doctypes.Service.PROP_TITLE,	scaImporter.getCurrentArchiName());
		properties.put(org.easysoa.doctypes.Service.PROP_PARENTURL, apiUrl);
		properties.put(org.easysoa.doctypes.Service.PROP_ARCHIPATH,	scaImporter.toCurrentArchiPath());
		properties.put(org.easysoa.doctypes.Service.PROP_ARCHILOCALNAME, scaImporter.getCurrentArchiName());
		properties.put(org.easysoa.doctypes.Service.PROP_DTIMPORT, scaImporter.getCompositeFile().getFilename()); // TODO also upload and link to it ?
		log.debug("properties before notify service : " + properties);
		notificationService.notifyService(documentManager, properties);
		
		// Enrich or create appliImpl
		//notificationService.notifyAppliImpl(documentManager, properties);
		
		// Enrich or create references
        properties = new HashMap<String, String>();
        String refUrl = getBindingUrl();
        log.debug("refUrl = " + refUrl);
        properties.put(ServiceReference.PROP_REFURL, refUrl); // getting referenced service url
        properties.put(ServiceReference.PROP_ARCHIPATH, scaImporter.toCurrentArchiPath());
        properties.put(ServiceReference.PROP_ARCHILOCALNAME, scaImporter.getCurrentArchiName());
        properties.put(ServiceReference.PROP_DTIMPORT, scaImporter.getCompositeFile().getFilename()); // TODO also upload and link to it ??
        properties.put(ServiceReference.PROP_PARENTURL, (String) scaImporter.getParentAppliImplModel().getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL));
        notificationService.notifyServiceReference(documentManager, properties);
        
        // Notify referenced service
        properties = new HashMap<String, String>();
        properties.put(org.easysoa.doctypes.Service.PROP_URL, refUrl);
        properties.put(ServiceReference.PROP_DTIMPORT, scaImporter.getCompositeFile().getFilename()); // TODO also upload and link to it ??
        try {
            notificationService.notifyService(documentManager, properties);
        } catch (MalformedURLException e) {
            log.warn("Failed to register referenced service, composite seems to link to an invalid URL");
        }
        
        
		
	}

	/**
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String getAppliImplUrl() throws ClientException {
		return (String) getParentAppliImplModel().getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL);
	}

	/**
	 * requires all ref'd composites and classes to be there
	 * 
	 * @throws Exception
	 */
//	public void TODOimportSCA() throws /*IOException, XMLStreamException,Client*/ Exception {
//		String scaFileName = compositeFile.getFilename();
//		if (scaFileName.endsWith(".composite")) {
//			importSCAComposite();
//		} else if (scaFileName.endsWith(".jar") || scaFileName.endsWith(".zip")) {
//			importSCAZip();
//		} else {
//			throw new Exception("Unsupported file type : neither a Composite nor an SCA zip or jar");
//		}
//	}

	/**
	 * requires all ref'd composites and classes to be there TODO rather replace
	 * by old XML one ??
	 * 
	 * @throws Exception
	 */
	public void importSCAZip() throws /* IOException, XMLStreamException, Client */Exception {

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

	/**
	 * 
	 * @throws Exception
	 */
	public void importSCAComposite() throws /*IOException, XMLStreamException,Client*/ Exception {

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
	/*public void importSCAOld() throws IOException, XMLStreamException, ClientException {

		// Initialization
		// File compositeTmpFile =
		// File.createTempFile(IdUtils.generateStringId(), ".composite");
		File compositeTmpFile = File.createTempFile(IdUtils.generateStringId(), ".jar");
		compositeFile.transferTo(compositeTmpFile);

		// Parsing using StAX (to get both SAX & DOM parsing benefits)
		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		compositeReader = xmlif.createXMLStreamReader(new FileInputStream(compositeTmpFile));

		while (compositeReader.hasNext()) {
			compositeReader.next();

			 // if (compositeReader.getEventType() == XMLEvent.START_ELEMENT) {
			 // ScaVisitor elementVisitor =
			 // elementQnameToScaVisitor.get(compositeReader.getName()); if
			 // (elementVisitor != null) { elementVisitor.visit(); } }

			if (compositeReader.getEventType() == XMLEvent.START_ELEMENT) {
				String name = compositeReader.getAttributeValue(null, "name"); // rather than "" ?! TODO SCA_URI
				QName elementName = compositeReader.getName();
				if (elementName.equals(SCA_COMPONENT_QNAME)) {
					// component !
					getArchiNameStack().push(name);

//					 } else if
//					 (compositeReader.getLocalName().startsWith("implementation."
//					 )) { // implementation ! if
//					 (compositeReader.getLocalName(
//					 ).equals("implementation.composite")) { //String
//					 compositeName = compositeReader.getAttributeValue(null,
//					 "name"); // rather than "" ?! // TODO SCA_URI //TODO
//					 visitComposite BUT CAN'T SINCE ONLY ONE SCA FILE HAS BEEN
//					 UPLOADED // so TODO alts : upload zip, use scm connector,
//					 export button from eclipse... }
					 

				} else if (elementName.equals(SCA_SERVICE_QNAME)) {
					// service !
					getArchiNameStack().push(name);
					acceptBindingParentVisitors(compositeReader, SCA_SERVICE_QNAME, createServiceBindingVisitors());
					getArchiNameStack().pop();

				} else if (elementName.equals(SCA_REFERENCE_QNAME)) {
					// reference !
					getArchiNameStack().push(name);
					acceptBindingParentVisitors(compositeReader, SCA_REFERENCE_QNAME, createReferenceBindingVisitors());
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
				log.error("Error while postChecking scaVisitor " + scaVisitor.getDescription() + " in SCA composite file " + compositeFile.getFilename(), ex);
			}
		}

		documentManager.save(); // NB. only required for additional, external code
	}*/

	/**
	 * 
	 */
	public void setServiceStackType(String serviceStackType) {
		this.serviceStackType = serviceStackType;
	}

	/**
	 * 
	 * @param parentAppliImplModel
	 */
	public void setParentAppliImpl(DocumentModel parentAppliImplModel) {
		this.parentAppliImplModel = parentAppliImplModel;
	}

	/**
	 * 
	 * @param serviceStackUrl
	 */
	public void setServiceStackUrl(String serviceStackUrl) {
		this.serviceStackUrl = serviceStackUrl;
	}

	/**
	 * 
	 */
	/*private void init() {
		elementQnameToScaVisitor = new HashMap<QName, ScaVisitor>();
		elementQnameToScaVisitor.put(SCA_SERVICE_QNAME, null);
		elementQnameToScaVisitor.put(SCA_REFERENCE_QNAME, null);
	}*/

	/**
	 * 
	 * @param compositeReader
	 * @param scaQname
	 * @param bindingVisitors
	 * @throws XMLStreamException
	 * @throws ClientException
	 */
	/*private void acceptBindingParentVisitors(XMLStreamReader compositeReader, QName scaQname, List<ScaVisitor> bindingVisitors)	throws XMLStreamException, ClientException {
		while (compositeReader.next() != XMLEvent.END_ELEMENT || !compositeReader.getName().equals(scaQname)) {
			if (compositeReader.getEventType() == XMLEvent.START_ELEMENT && compositeReader.getLocalName().startsWith("binding.")) {
				// binding !
				acceptBindingVisitors(compositeReader, bindingVisitors);
			}
		}
	}*/

	/**
	 * 
	 * @param compositeReader
	 * @param bindingVisitors
	 * @throws ClientException
	 */
	/*
	private void acceptBindingVisitors(XMLStreamReader compositeReader,	List<ScaVisitor> bindingVisitors) throws ClientException {
		QName bindingQName = compositeReader.getName();
		for (ScaVisitor bindingVisitor : bindingVisitors) {
			if (bindingVisitor.isOkFor(bindingQName)) {
				try {
					bindingVisitor.visit();
					scaVisitorsToPostCheck.add(bindingVisitor);
				} catch (Exception ex) {
					log.error("Error when visiting binding " + bindingVisitor.getDescription() + " at archi path " + toCurrentArchiPath() + " in SCA composite file " + compositeFile.getFilename(), ex);
				}
			}
		}
	}*/

	@Override
	public String getCurrentArchiName() {
		return getArchiNameStack().peek();
	}

	//@Override
	public EObject getCurrentBinding() {
		return getBindingStack().peek();
	}
	
	@Override
	public String toCurrentArchiPath() {
		StringBuffer sbuf = new StringBuffer();
		for (String archiName : getArchiNameStack()) {
			sbuf.append("/");
			sbuf.append(archiName);
		}
		return sbuf.toString();
	}

	@Override
	public CoreSession getDocumentManager() {
		return documentManager;
	}

	@Override
	public Blob getCompositeFile() {
		return compositeFile;
	}

	@Override
	public String getServiceStackType() {
		return serviceStackType;
	}

	@Override
	public String getServiceStackUrl() {
		return serviceStackUrl;
	}

	@Override
	public DocumentModel getParentAppliImplModel() {
		return parentAppliImplModel;
	}

	/**
	 * 
	 * @return
	 */
	public Stack<String> getArchiNameStack() {
		return archiNameStack;
	}

	public Stack<EObject> getBindingStack(){
		return bindingStack;
	}
	
	@Override
	public XMLStreamReader getCompositeReader() {
		// Not used in case of FraSCAti SCA importer
		return null;
		//return compositeReader;
	}

}
