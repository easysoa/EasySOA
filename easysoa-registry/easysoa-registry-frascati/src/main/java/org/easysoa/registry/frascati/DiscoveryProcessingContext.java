package org.easysoa.registry.frascati;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.frascati.ApiFraSCAtiScaImporter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.stp.sca.Composite;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.assembly.factory.api.ProcessingContext;
import org.ow2.frascati.assembly.factory.api.ProcessingMode;

public class DiscoveryProcessingContext implements ProcessingContext {

	protected ProcessingContext delegate;
	protected List<String> warningMessages = new ArrayList<String>();
	protected List<String> errorMessages = new ArrayList<String>();	

	static final Log log = LogFactory.getLog(DiscoveryProcessingContext.class);
	
	//protected IScaImporter scaImporter;
	
	public DiscoveryProcessingContext(ProcessingContext delegate/*, IScaImporter scaImporter*/) {
		this.delegate = delegate;
		//this.scaImporter = scaImporter;
	}	
	
	//////////////////////////////////////////////
	// additional methods
	
	public List<String> getWarningMessages() {
		return warningMessages;
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	//////////////////////////////////////////////
	// delegate enhanced methods	
	
	public ClassLoader getClassLoader() {
		return delegate.getClassLoader();
	}

	public ProcessingMode getProcessingMode() {
		return delegate.getProcessingMode();
	}

	public <T> Class<T> loadClass(String className) throws ClassNotFoundException {
		try {
			return delegate.loadClass(className);
		} catch (Exception e) {
	          this.warning("Java class (interface.java, implementation.java...) '" + className + "' not found");
	          return null;
		}		
	}

	public void setProcessingMode(ProcessingMode processingMode) {
		delegate.setProcessingMode(processingMode);
	}

	public URL getResource(String name) {
		return delegate.getResource(name);
	}

	public Composite getRootComposite() {
		return delegate.getRootComposite();
	}

	public void setRootComposite(Composite composite) {
		delegate.setRootComposite(composite);
	}

	public <T> void putData(Object key, Class<T> type, T data) {
		delegate.putData(key, type, data);
	}

	public <T> T getData(Object key, Class<T> type) {
		if (key instanceof Composite && type.equals(Component.class)) {
			//XXX hack to call EasySOA service discovery
			//TODO later in AssemblyFactoryManager ex. in delegate CompositeProcessor...
			Composite composite = (Composite) key;
			discover(composite);
		}
		return delegate.getData(key, type);
	}

	// called only with instanciate processing mode
	protected void discover(Composite composite) {
		log.debug("discovering composite ....");
		// TODO FraSCAtiSCAImporter(...Base/Api...).visitComposite(composite)
		try {
			// TODO : do not creates a new ScaImporter .... Use the one created by the test or SCA import bean ....
			// Establish or find the relation between frascatiService and FrascatiScaImporter ...
			ApiFraSCAtiScaImporter frascatiImporter = new ApiFraSCAtiScaImporter();

			// pass the importer as a parameters when the processing context is created ?
			frascatiImporter.visitComposite(composite);
			//scaImporter.visitComposite(composite);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// then reimpl ScaVisitors on top of a nuxeo-free EasySOA API (instead of Nuxeo), by calling
		// either some of the existing RestNotificationRequestImpl
		// or LATER an API common to client and server like INotificationRest :
		
		// Getting notification API
        //INotificationRest notificationRest = EasySoaApiFactory.getInstance().getNotificationRest();
		// Sending a notification
        //notificationRest.
        
		// TODO when necessary, put nuxeo-free code in easysoa-registry-api and this one in nxserver/lib (instead of lib/)
	}

	public void warning(String message) {
		delegate.warning(message);
	}

	public int getWarnings() {
		return delegate.getWarnings();
	}

	public void error(String message) {
		delegate.error(message);
	}

	public int getErrors() {
		return delegate.getErrors();
	}

	public String getLocationURI(EObject eObject) {
		return delegate.getLocationURI(eObject);
	}
	
}
