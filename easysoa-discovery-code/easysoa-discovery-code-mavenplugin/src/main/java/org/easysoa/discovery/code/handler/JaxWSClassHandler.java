package org.easysoa.discovery.code.handler;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.DeliverableInfo;
import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.discovery.code.ServiceImplInfo;

import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

public class JaxWSClassHandler implements ClassHandler {

    private static final String ANN_WS = "javax.jws.WebService";
    private static final String ANN_WEBRESULT = "javax.jws.WebResult";
    private static final String ANN_WEBPARAM = "javax.jws.WebParam";
    
    public void handleClass(JavaClass c, DeliverableInfo deliverableInfo, Log log) {
        StringBuilder classInfo = new StringBuilder();
        
        // Check JAX-WS annotation
        if (c.isInterface() && ParsingUtils.hasAnnotation(c, ANN_WS)) {
            // Extract WS info
            ServiceImplInfo serviceInfo = new ServiceImplInfo(deliverableInfo,
                    "JAX-WS", c.getFullyQualifiedName());
            classInfo.append("Soap WebService " + c.getName() + "\n");
            classInfo.append("[ID='" + serviceInfo.getId() + "', VERSION='" + serviceInfo.getVersion() + "']\n");
            classInfo.append("----------------------------\n");
            
            // Exctract operations info
            for (JavaMethod method : c.getMethods()) {
                if (ParsingUtils.hasAnnotation(method, "javax.jws.WebResult")) {
                    Annotation webResultAnn = ParsingUtils.getAnnotation(method, ANN_WEBRESULT);
                    classInfo.append("* Operation " + webResultAnn.getProperty("name") + "\n");
                    
                    // Extract parameters info
                    for (JavaParameter parameter : method.getParameters()) {
                        Annotation webParamAnn = ParsingUtils.getAnnotation(parameter, ANN_WEBPARAM);
                        classInfo.append("  " + webParamAnn.getProperty("name").getParameterValue()
                                + " : " + parameter.getType().toString() + "\n");
                    }
                }
            }
            classInfo.append("----------------------------\n\n");
            log.info(classInfo.toString());
        }
    }
}
