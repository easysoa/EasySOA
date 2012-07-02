package org.easysoa.discovery.code.handler;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.DeliverableInfo;
import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.discovery.code.ServiceImplInfo;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

public class JaxRSClassHandler implements ClassHandler {

    private static final String ANN_PATH = "javax.ws.rs.Path";
    private static final String[] ANN_METHODS = new String[] {
        "javax.ws.rs.GET", "javax.ws.rs.POST", "javax.ws.rs.PUT",
        "javax.ws.rs.HEAD", "javax.ws.rs.OPTIONS"
      };

    public void handleClass(JavaClass c, DeliverableInfo deliverableInfo, Log log) {
        StringBuilder classInfo = new StringBuilder();
        
        // Check JAX-WS annotation
        if (ParsingUtils.hasAnnotation(c, ANN_PATH)) {
            // Extract WS info
            ServiceImplInfo serviceInfo = new ServiceImplInfo(deliverableInfo,
                    "JAX-RS", c.getFullyQualifiedName());
            classInfo.append("Rest WebService " + c.getName() + "\n");
            classInfo.append("[ID='" + serviceInfo.getId() + "', VERSION='" + serviceInfo.getVersion() + "']\n");
            classInfo.append("----------------------------\n");
            
            // Exctract operations info
            for (JavaMethod method : c.getMethods()) {
                if (ParsingUtils.hasAnnotation(method, ANN_PATH)) {
                    // Extract service path
                    Object path = ParsingUtils.getAnnotation(method, ANN_PATH).getProperty("value");
                    
                    // Extract HTTP method
                    String httpMethod = "???";
                    for (String annHttpMethod : ANN_METHODS) {
                        if (ParsingUtils.hasAnnotation(method, annHttpMethod)) {
                            httpMethod = annHttpMethod.replace("javax.ws.rs.", "");
                            break;
                        }
                    }
                    
                    classInfo.append("* " + httpMethod + " Operation " + method.getName() + " (" + path + ")\n");
                }
            }
            classInfo.append("----------------------------\n\n");
            log.info(classInfo.toString());
        }
        
    }

}
