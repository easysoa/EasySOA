package org.easysoa.discovery.code.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.MavenDeliverable;
import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.discovery.rest.model.Service;
import org.easysoa.discovery.rest.model.ServiceImpl;
import org.easysoa.discovery.rest.model.SoaNode;

import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;

public class JaxWSSourcesHandler implements SourcesHandler {

    private static final String ANN_WSCLIENT = "javax.jws.WebServiceClient";
    private static final String ANN_WS = "javax.jws.WebService";
    private static final String ANN_WEBRESULT = "javax.jws.WebResult";
    private static final String ANN_WEBPARAM = "javax.jws.WebParam";
    private static final String ANN_INJECT = "javax.inject.Inject";

    private List<JavaClass> wsClientsAndItfs = new ArrayList<JavaClass>();
    
    @Override
    public Collection<SoaNode> handleSources(JavaSource[] sources,
            MavenDeliverable mavenDeliverable, Log log) {
        Collection<SoaNode> discoveredNodes = new ArrayList<SoaNode>();
        
        // Find all WS clients/interfaces
        for (JavaSource source : sources) {
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                if (ParsingUtils.hasAnnotation(c, ANN_WSCLIENT)
                        || ParsingUtils.hasAnnotation(c, ANN_WS)) {
                    wsClientsAndItfs.add(c);
                }
            }
        }
        
        // Explore each class
        for (JavaSource source : sources) {
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                discoveredNodes.addAll(this.handleClass(c, sources, mavenDeliverable, log));
            }
        }
        return discoveredNodes;
    }
    
    public Collection<SoaNode> handleClass(JavaClass c, JavaSource[] sources,
            MavenDeliverable deliverable, Log log) {
        List<SoaNode> discoveredNodes = new LinkedList<SoaNode>();
        
        // Check JAX-WS annotation
        if (!c.isInterface() && ParsingUtils.hasAnnotation(c, ANN_WS)) { // TODO superclass ?

            // Extract WS info
            ServiceImpl serviceImpl = new ServiceImpl(deliverable,
                    "JAX-WS", c.getFullyQualifiedName(), c.getName());
            deliverable.addRelation(serviceImpl);
            discoveredNodes.add(serviceImpl);
            
            // Extract interface info
            Annotation wsImplAnnotation = ParsingUtils.getAnnotation(c, ANN_WS);
            //System.out.println("\ncp:\n" + System.getProperty("java.class.path"));
            JavaClass itfClass = findWsInterface(wsImplAnnotation, sources); // TODO several interfaces ???
            if (itfClass != null) {
            
                // Extract WS info
                Service serviceDef = new Service(itfClass.getName(), deliverable.getVersion());
                serviceDef.addRelation(serviceImpl);
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
                    serviceImpl.setOperationsInfo(operationInfoString);
                    serviceDef.addRequirement(operationInfoString);
                }
            
            }
        }
        
        // JAXWS WebServiceClient (generated client stub) :
        
        // Java 6 injection of fields by service-annotated interfaces or WebServiceClients (generated client stub) :
        // in java 6-injected fields :
        for (JavaField javaField : c.getFields()) { // TODO also superfields...
            if (ParsingUtils.hasAnnotation(javaField, ANN_INJECT) && isWSClientOrItf(javaField.getType())) {
                deliverable.addRequirement(
                        c.getFullyQualifiedName()
                        + " consumes WS of JAX-WS interface " 
                        + javaField.getType().getJavaClass().getFullyQualifiedName());
            }
        }
        // in java 6-injected setters :
        for (BeanProperty beanProperty : c.getBeanProperties()) {
            JavaMethod method = beanProperty.getMutator();
            
            if (method != null && ParsingUtils.hasAnnotation(method, ANN_INJECT)) {
                JavaParameter[] parameters = method.getParameters();
                if (parameters.length == 1 && isWSClientOrItf(parameters[0].getType())) {
                    deliverable.addRequirement(
                            c.getFullyQualifiedName()
                            + " consumes WS of JAX-WS interface " 
                            + parameters[0].getType().getJavaClass().getFullyQualifiedName());
                }
            }
        }
        
        return discoveredNodes;
    }

    private JavaClass findWsInterface(Annotation wsImplAnnotation, JavaSource[] sources) {
        Object itfParameter = wsImplAnnotation.getProperty("endpointInterface").getParameterValue();

        if (itfParameter != null && itfParameter instanceof String) {
            String itfFullName = ((String) itfParameter).replace("\"", "");
            for (JavaSource source : sources) {
                for (JavaClass candidateClass : source.getClasses()) {
                    if (itfFullName.equals(candidateClass.getFullyQualifiedName())) {
                        return candidateClass;
                    }
                }
            }
        }

        return null;
    }
    
    private boolean isWSClientOrItf(Type type) {
        return wsClientsAndItfs.contains(type.getJavaClass());
    }
}
