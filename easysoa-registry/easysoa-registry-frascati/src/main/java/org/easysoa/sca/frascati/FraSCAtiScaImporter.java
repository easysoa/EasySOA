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
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.sca.frascati;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.registry.frascati.FraSCAtiService;
import org.easysoa.registry.frascati.RestBindingInfoProvider;
import org.easysoa.registry.frascati.WSBindingInfoProvider;
import org.easysoa.sca.BindingInfoProvider;
import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.visitors.ReferenceBindingVisitor;
import org.easysoa.sca.visitors.ScaVisitor;
import org.easysoa.sca.visitors.ServiceBindingVisitor;
import org.easysoa.services.DocumentService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.stp.sca.BaseReference;
import org.eclipse.stp.sca.BaseService;
import org.eclipse.stp.sca.Binding;
import org.eclipse.stp.sca.Component;
import org.eclipse.stp.sca.ComponentReference;
import org.eclipse.stp.sca.ComponentService;
import org.eclipse.stp.sca.Composite;
import org.eclipse.stp.sca.Reference;
import org.eclipse.stp.sca.Service;
import org.eclipse.stp.sca.WebServiceBinding;
import org.eclipse.stp.sca.domainmodel.frascati.RestBinding;
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
	//public static final QName SCA_COMPONENT_QNAME = new QName(SCA_URI, "component");
	//public static final QName SCA_SERVICE_QNAME = new QName(SCA_URI, "service");
	//public static final QName SCA_REFERENCE_QNAME = new QName(SCA_URI, "reference");

	// Logger
	private static Log log = LogFactory.getLog(FraSCAtiScaImporter.class);

	// Nuxeo document manager
	private CoreSession documentManager;
	private Blob compositeFile;
	private String serviceStackType;
	private String serviceStackUrl;
	private DocumentModel parentAppliImplModel;

	private Stack<String> archiNameStack = new Stack<String>();
	private Stack<EObject> bindingStack = new Stack<EObject>();

	private/* @Inject */FraSCAtiService frascatiService; // TODO better Inject doesn't work outside tests ?!

	/**
	 * List of binding info providers
	 */
    private ArrayList<BindingInfoProvider> bindingInfoProviders = null;
	
    public FraSCAtiScaImporter(){
    	this.documentManager = null;
    	this.compositeFile = null;
    	this.parentAppliImplModel = null;
    }
    
	/**
	 * Constructor
	 * @param documentManager Nuxeo document manager
	 * @param compositeFile Composite file to import
	 * @throws ClientException 
	 */
	public FraSCAtiScaImporter(CoreSession documentManager, Blob compositeFile) throws ClientException {
		this.documentManager = documentManager;
		this.compositeFile = compositeFile;
		if(documentManager != null){
			this.parentAppliImplModel = Framework.getRuntime().getService(DocumentService.class).getDefaultAppliImpl(documentManager);
		}
	}

	/**
	 * Build a list of binding info providers
	 * @return
	 */
    public List<BindingInfoProvider> createBindingInfoProviders() {
    	if (bindingInfoProviders == null) {
    		bindingInfoProviders = new ArrayList<BindingInfoProvider>();
            bindingInfoProviders.add(new WSBindingInfoProvider(this));
            bindingInfoProviders.add(new RestBindingInfoProvider(this));
    	}
        return bindingInfoProviders;
    }	
	
    /**
     * creates and returns a ServiceBindingVisitor
     * @return
     */
    public ScaVisitor createServiceBindingVisitor() {
        return new ServiceBindingVisitor(this);
    }
    
    /**
     * creates and returns a ReferenceBindingVisitor 
     * @return
     */
    public ScaVisitor createReferenceBindingVisitor() {
        return new ReferenceBindingVisitor(this);
    }	
	
	/**
	 * Visit the composite. For eachservice, component, reference ... visit the associated bindings
	 * @param scaComposite the composite to visit
	 */
	private void visitComposite(Composite scaComposite) {
		log.debug("visiting composite");
		log.debug("scaComposite" + scaComposite);
		log.debug("Composite.name=" + scaComposite.getName());
		// Get services
		for (Service service : scaComposite.getService()) {
			log.debug("Service.name=" + service.getName());
			getArchiNameStack().push(service.getName());
			getBindingStack().push(service);
			for (Binding binding : service.getBinding()) {
				try {
                    acceptBindingVisitors(binding, createServiceBindingVisitor(), createBindingInfoProviders());
				} catch (Exception e) {
					log.warn("A problem occurs during the visit of services", e);
				}
			}
			getBindingStack().pop();			
			getArchiNameStack().pop();
		}
		// Get references
		for (Reference reference : scaComposite.getReference()) {
			log.debug("Reference.name=" + reference.getName());
			getArchiNameStack().push(reference.getName());
			getBindingStack().push(reference);
			for (Binding binding : reference.getBinding()) {
				try {
                    acceptBindingVisitors(binding, createReferenceBindingVisitor(), createBindingInfoProviders());
				} catch (Exception e) {
					log.warn("A problem occurs during the visit of references", e);
				}
			}
			getBindingStack().pop();			
			getArchiNameStack().pop();
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
						acceptBindingVisitors(binding, createServiceBindingVisitor(), createBindingInfoProviders());
					} catch (Exception e) {
						log.warn("A problem occurs during the visit of component services", e);
					}
				}
				getBindingStack().pop();				
				getArchiNameStack().pop();
			}
			// Get component references
			for (ComponentReference componentReference : component.getReference()) {
				log.debug("ComponentReference.name=" + componentReference.getName());
				getArchiNameStack().push(componentReference.getName());
				getBindingStack().push(componentReference);				
				for (Binding binding : componentReference.getBinding()) {
					try {
						acceptBindingVisitors(binding, createReferenceBindingVisitor(), createBindingInfoProviders());
					} catch (Exception e) {
						log.warn("A problem occurs during the visit of component references", e);
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
	 * @param scaQname 
	 * @param scaVisitor Binding visitor to register in Nuxeo
	 * @param bindingInfoProviders Bindings info provider list
	 */
	private void acceptBindingVisitors(Binding binding, ScaVisitor scaVisitor, List<BindingInfoProvider> bindingInfoProviders) {
        for (BindingInfoProvider bindingInfoProvider : bindingInfoProviders) {
            if (bindingInfoProvider.isOkFor(binding)) {
	        	try {
	                scaVisitor.visit(bindingInfoProvider);
	                //scaVisitorsToPostCheck.add(scaVisitor);
	            } catch (Exception ex) {
	                log.error("Error when visiting binding " + scaVisitor.getDescription()
	                        + " at archi path " + toCurrentArchiPath()
	                        + " in SCA composite file " + compositeFile.getFilename(), ex);
	            }
            }
        }
	}

	@Override
    public String getBindingUrl() {
    	String serviceUrl = null;
    	Binding binding = null;
    	if(getCurrentBinding() instanceof BaseService){
    		// NB. only difference between Service and ComponentService is Service.promote
    		BaseService componentService = (BaseService) getCurrentBinding();
    		// TODO do not take only the first one, missing bindings in the case of multiple bindings
    		binding = componentService.getBinding().get(0);
    	} else if(getCurrentBinding() instanceof BaseReference){
    		// NB. only difference between Reference and ComponentReferenceis Reference.promote
    		BaseReference componentReference = (BaseReference) getCurrentBinding();
    		binding = componentReference.getBinding().get(0);
    	}
    	if (binding != null) {
    		serviceUrl = binding.getUri();
    	}
    	log.debug("binding name : " + binding.getName());    	
    	if(serviceUrl == null){
			// wsdlLocation in case of wsdl binding
			if (binding instanceof WebServiceBinding) {
				List<String> wsdlLocations = ((WebServiceBinding) binding).getWsdlLocation();
				if (wsdlLocations != null && wsdlLocations.size() != 0) {
					serviceUrl = wsdlLocations.get(0).replace("?wsdl", "");
				}
			// URI in case of rest binding
			} else if(binding instanceof RestBinding){
				String uri = ((RestBinding) binding).getUri();
				if (uri != null && !"".equals(uri)) {
					serviceUrl = uri;
				}				
			}
    	}
    	log.debug("serviceUrl : " + serviceUrl);
    	return serviceUrl; 
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
	 * Import all the composite contained in the zip archive
	 * requires all ref'd composites and classes to be there TODO rather replace
	 * by old XML one ??
	 * @throws Exception
	 */
	public void importSCAZip() throws Exception {
		log.debug("Importing SCA Composite contained in ZIP");
		// Initialization
		File scaZipTmpFile = File.createTempFile(IdUtils.generateStringId(), ".jar");
		compositeFile.transferTo(scaZipTmpFile);
		// Read an (Eclipse SOA) SCA composite then visit it
		frascatiService = Framework.getService(FraSCAtiService.class); // TODO in init
		Set<Composite> scaZipCompositeSet = frascatiService.readScaZip(scaZipTmpFile);
		// Visit each composite
		for (Composite scaZipComposite : scaZipCompositeSet) {
			visitComposite(scaZipComposite);
		}
	}

	/**
	 * Import a SCA composite
	 * @throws Exception
	 */
	public void importSCAComposite() throws Exception {
		log.debug("Importing SCA Composite");
		// Initialization
		File compositeTmpFile = File.createTempFile(IdUtils.generateStringId(), ".composite");
		compositeFile.transferTo(compositeTmpFile);
		// Read an (Eclipse SOA) SCA composite then visit it
		frascatiService = Framework.getService(FraSCAtiService.class); // TODO in init
		Composite scaZipComposite = frascatiService.readComposite(compositeTmpFile.toURI().toURL());
		visitComposite(scaZipComposite);
	}

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
	public void importSCA() throws Exception {
		String scaFileName = compositeFile.getFilename();
		log.debug("scaFileName = " + compositeFile.getFilename());
		if (scaFileName.endsWith(".composite")) {
			importSCAComposite();
		} else if (scaFileName.endsWith(".jar") || scaFileName.endsWith(".zip")) {
			importSCAZip();
		} else {
			throw new Exception("Unsupported file type : neither a Composite nor an SCA zip or jar");
		}		
	}

}
