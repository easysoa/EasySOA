package org.easysoa.discovery.code.handler;

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
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;

public class JaxWSClassHandler implements ClassHandler {

    private static final String ANN_WS = "javax.jws.WebService";
    private static final String ANN_WEBRESULT = "javax.jws.WebResult";
    private static final String ANN_WEBPARAM = "javax.jws.WebParam";
    
    public Collection<SoaNode> handleClass(JavaClass c, JavaSource[] sources,
            MavenDeliverable deliverableInfo, Log log) {
        List<SoaNode> discoveredNodes = new LinkedList<SoaNode>();
        
        // Check JAX-WS annotation
        if (!c.isInterface() && ParsingUtils.hasAnnotation(c, ANN_WS)) {

            // Extract WS info
            ServiceImpl serviceImpl = new ServiceImpl(deliverableInfo,
                    "JAX-WS", c.getFullyQualifiedName(), c.getName());
            deliverableInfo.addRelation(serviceImpl);
            discoveredNodes.add(serviceImpl);
            
            // Extract interface info
            Annotation wsImplAnnotation = ParsingUtils.getAnnotation(c, ANN_WS);
            JavaClass itfClass = findWsInterface(wsImplAnnotation, sources);
            if (itfClass != null) {
            
                // Extract WS info
                Service serviceDef = new Service(itfClass.getName(), deliverableInfo.getVersion());
                serviceDef.addRelation(serviceImpl);
                discoveredNodes.add(serviceDef);
       
                // Extract operations info
                StringBuilder operationsInfo = new StringBuilder();
                for (JavaMethod method : itfClass.getMethods()) {
                    if (ParsingUtils.hasAnnotation(method, "javax.jws.WebResult")) {
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
}
