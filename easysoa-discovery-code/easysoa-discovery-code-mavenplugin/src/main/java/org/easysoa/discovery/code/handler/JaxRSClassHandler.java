package org.easysoa.discovery.code.handler;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.MavenDeliverable;
import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.discovery.rest.model.SOANode;
import org.easysoa.discovery.rest.model.SOANodeType;
import org.easysoa.discovery.rest.model.ServiceImpl;

import com.thoughtworks.qdox.model.JavaClass;

public class JaxRSClassHandler implements ClassHandler {

    private static final String ANN_PATH = "javax.ws.rs.Path";
//    private static final String[] ANN_METHODS = new String[] {
//        "javax.ws.rs.GET", "javax.ws.rs.POST", "javax.ws.rs.PUT",
//        "javax.ws.rs.HEAD", "javax.ws.rs.OPTIONS"
//      };

    public Collection<SOANode> handleClass(JavaClass c, MavenDeliverable deliverableInfo, Log log) {
        List<SOANode> discoveredNodes = new LinkedList<SOANode>();
        
        // Check JAX-WS annotation
        if (ParsingUtils.hasAnnotation(c, ANN_PATH)) {
            // Extract WS info
            ServiceImpl serviceImpl = new ServiceImpl(deliverableInfo,
                    "JAX-RS", c.getFullyQualifiedName(), c.getName());
            deliverableInfo.addRelation(SOANodeType.SERVICEIMPL, serviceImpl.getId());
            discoveredNodes.add(serviceImpl);
            
//            // Exctract operations info
//            for (JavaMethod method : c.getMethods()) {
//                if (ParsingUtils.hasAnnotation(method, ANN_PATH)) {
//                    // Extract service path
//                    Object path = ParsingUtils.getAnnotation(method, ANN_PATH).getProperty("value");
//                    
//                    // Extract HTTP method
//                    String httpMethod = "???";
//                    for (String annHttpMethod : ANN_METHODS) {
//                        if (ParsingUtils.hasAnnotation(method, annHttpMethod)) {
//                            httpMethod = annHttpMethod.replace("javax.ws.rs.", "");
//                            break;
//                        }
//                    }
//                    
//                    classInfo.append("* " + httpMethod + " Operation " + method.getName() + " (" + path + ")\n");
//                }
//            }
        }
        
        return discoveredNodes;
        
    }

}
