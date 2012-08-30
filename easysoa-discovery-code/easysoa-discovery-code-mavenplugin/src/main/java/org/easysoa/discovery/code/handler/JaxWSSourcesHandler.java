package org.easysoa.discovery.code.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.registry.rest.client.types.ServiceImplInformation;
import org.easysoa.registry.rest.client.types.ServiceInformation;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.ServiceImplementation;

import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.annotation.AnnotationValue;


/**
 * In project & current deliverable, reports :
 * * JAXWS service impl and their impl'd service interface (from impl and also by themselves),
 * both by being annotated by @WebService
 * * thanks to InterfaceHandlerBase, member (field or bean setter)-injected service :
 * typed by @WebService annotated interfaces or @WebServiceClient annotated classes)
 * 
 * @author mdutoo
 *
 */
public class JaxWSSourcesHandler extends InterfaceHandlerBase implements SourcesHandler {

    private static final String ANN_WSPROVIDER = "javax.jws.WebServiceProvider";
    private static final String ANN_WS = "javax.jws.WebService";
    private static final String ANN_WEBRESULT = "javax.jws.WebResult";
    private static final String ANN_WEBPARAM = "javax.jws.WebParam";

    private static final String ANN_XML_WSCLIENT = "javax.xml.ws.WebServiceClient";
    private static final String ANN_XML_WSREF = "javax.xml.ws.WebServiceRef";
    private static final String ANN_XML_WSPROVIDER = "javax.xml.ws.WebServiceProvider";
    
    public JaxWSSourcesHandler() {
        super();
        this.injectionAnnotations.add(ANN_WSPROVIDER);
        this.injectionAnnotations.add(ANN_XML_WSCLIENT);
        this.injectionAnnotations.add(ANN_XML_WSREF);
        this.injectionAnnotations.add(ANN_XML_WSPROVIDER);
    }
    
    @Override
    public Collection<SoaNodeInformation> handleSources(JavaSource[] sources,
            MavenDeliverableInformation mavenDeliverable, Log log) throws Exception {
        Collection<SoaNodeInformation> discoveredNodes = new ArrayList<SoaNodeInformation>();
        
        // Pass 1 : Find all WS clients/interfaces
        for (JavaSource source : sources) {
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                boolean isWs = ParsingUtils.hasAnnotation(c, ANN_WS);
                boolean isInterface = c.isInterface();
                
                if (isWs) {
                    if (isInterface) {
                        wsInjectableTypeSet.add(c.asType());
                        
                        // also in first pass for itf, Extract WS info
                        ServiceInformation serviceDef = new ServiceInformation(c.getName());
                        discoveredNodes.add(serviceDef);
                    }
                } else if (isWs && isInterface
                        || ParsingUtils.hasAnnotation(c, ANN_XML_WSCLIENT)
                        || ParsingUtils.hasAnnotation(c, ANN_WSPROVIDER)
                        || ParsingUtils.hasAnnotation(c, ANN_XML_WSPROVIDER)) {
                    wsInjectableTypeSet.add(c.asType());
                }
            }
        }
        
        // Pass 2 : Explore each impl
        for (JavaSource source : sources) {
            // TODO diff between main & tests
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                discoveredNodes.addAll(this.handleClass(c, sources, mavenDeliverable, log));
            }
        }
        return discoveredNodes;
    }
    
    public Collection<SoaNodeInformation> handleClass(JavaClass c, JavaSource[] sources,
            MavenDeliverableInformation deliverable, Log log) throws Exception {
        List<SoaNodeInformation> discoveredNodes = new LinkedList<SoaNodeInformation>();
        
        // Check JAX-WS annotation
        if (!c.isInterface() && ParsingUtils.hasAnnotation(c, ANN_WS)) { // TODO superclass ? TODO interface !

            // Extract WS info
            ServiceImplInformation serviceImpl = new ServiceImplInformation(c.getName());
            serviceImpl.setTitle(c.getFullyQualifiedName());
            serviceImpl.setProperty(ServiceImplementation.XPATH_TECHNOLOGY, "JAX-WS"); // TODO tech
            serviceImpl.addParentDocument(deliverable.getSoaNodeId());
            discoveredNodes.add(serviceImpl);
            
            // Extract interface info
            //System.out.println("\ncp:\n" + System.getProperty("java.class.path"));
            JavaClass itfClass = findWsInterface(c, sources); // TODO several interfaces ???
            if (itfClass != null) {
            
                // Extract WS info
                ServiceInformation serviceDef = new ServiceInformation(itfClass.getName());
                serviceImpl.addParentDocument(serviceDef.getSoaNodeId());
                discoveredNodes.add(serviceDef);
       
                // Extract operations info
                StringBuilder operationsInfo = new StringBuilder();
                for (JavaMethod method : itfClass.getMethods()) {
                    if (ParsingUtils.hasAnnotation(method, ANN_WEBRESULT)) {
                        Annotation webResultAnn = ParsingUtils.getAnnotation(method, ANN_WEBRESULT);
                        operationsInfo.append("Operation " + webResultAnn.getProperty("name") + " (");
                        
                        // Extract parameters info
                        for (JavaParameter parameter : method.getParameters()) {
                            Annotation webParamAnn = ParsingUtils.getAnnotation(parameter, ANN_WEBPARAM);
                            operationsInfo.append(webParamAnn.getProperty("name").getParameterValue()
                                    + "=" + parameter.getType().toString() + ", ");
                        }
                        operationsInfo.delete(operationsInfo.length() - 2, operationsInfo.length());
                        operationsInfo.append("), ");
                    }
                }
                if (operationsInfo.length() > 0) {
                    // Cosmetic changes before storage
                    String operationInfoString = operationsInfo.toString().substring(0, operationsInfo.length() - 2);
                    operationInfoString = operationInfoString.replace("\"", "'");
                    serviceImpl.setProperty("dc:description", operationInfoString); // TODO
                    //serviceDef.addRequirement(operationInfoString); // TODO
                }
            
            }
        }
        
        // NB. JAXWS WebServiceClient (generated client stub) not reported as such but through injection below
        // (though they could be, as "connector" TODO)
        
        // member injected by WebService annotated interfaces, WebServiceClients (generated client stub) or WebServiceRefs :
        handleInjectedMembers(c, deliverable, log);
        
        return discoveredNodes;
    }

    private JavaClass findWsInterface(JavaClass c, JavaSource[] sources) {
        // getting referenced endpointInterface, see http://pic.dhe.ibm.com/infocenter/wasinfo/v7r0/index.jsp?topic=%2Fcom.ibm.websphere.express.doc%2Finfo%2Fexp%2Fae%2Ftwbs_devjaxwsendpt.html
        String endpointInterfaceAnnotationValue = null;
        Annotation wsImplAnnotation = ParsingUtils.getAnnotation(c, ANN_WS); // should not be null
        AnnotationValue endpointInterfaceAnnotation = wsImplAnnotation.getProperty("endpointInterface");
        
        if (endpointInterfaceAnnotation != null) {
            Object itfParameter = endpointInterfaceAnnotation.getParameterValue();
            if (itfParameter != null && itfParameter instanceof String) {
                endpointInterfaceAnnotationValue = ((String) itfParameter).replace("\"", "");
            }
        }
        
        if (endpointInterfaceAnnotationValue != null && !endpointInterfaceAnnotationValue.isEmpty()) {
            // use endpointInterface to find interface
            String itfFullName = endpointInterfaceAnnotationValue;
            //return wsInjectableTypeToClassMap.get(new Type(itfFullName)); // TODO rather than below ?
            // return c.getSource().getJavaClassContext().getClassByName(itfFullName); // TODO rather than below ?
            for (JavaSource source : sources) {
                JavaClass itf = source.getJavaClassContext().getClassByName(itfFullName);
                if (itf != null) {
                    return itf;
                }
                /*for (JavaClass candidateClass : source.getClasses()) {
                    if (itfFullName.equals(candidateClass.getFullyQualifiedName())) {
                        return candidateClass;
                    }
                }*/
            }
            
        } else {
            // find first impl'd ws interface
            for (JavaClass itf : c.getImplementedInterfaces()) {
                Annotation wsItfAnnotation = ParsingUtils.getAnnotation(itf, ANN_WS);
                if (wsItfAnnotation != null) {
                    return itf;
                }
            }
        }

        return null;
    }
}
