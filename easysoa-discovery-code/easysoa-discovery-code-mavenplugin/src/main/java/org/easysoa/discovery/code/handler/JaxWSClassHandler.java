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

public class JaxWSClassHandler implements ClassHandler {

    private static final String ANN_WS = "javax.jws.WebService";
//    private static final String ANN_WEBRESULT = "javax.jws.WebResult";
//    private static final String ANN_WEBPARAM = "javax.jws.WebParam";
    
    public Collection<SOANode> handleClass(JavaClass c, MavenDeliverable deliverableInfo, Log log) {
        List<SOANode> discoveredNodes = new LinkedList<SOANode>();
        
        // Check JAX-WS annotation
        if (c.isInterface() && ParsingUtils.hasAnnotation(c, ANN_WS)) {
            // Extract WS info
            ServiceImpl serviceImpl = new ServiceImpl(deliverableInfo,
                    "JAX-WS", c.getFullyQualifiedName(), c.getName());
            deliverableInfo.addRelation(SOANodeType.SERVICEIMPL, serviceImpl.getId());
            discoveredNodes.add(serviceImpl);
   
//            // Exctract operations info
//            for (JavaMethod method : c.getMethods()) {
//                if (ParsingUtils.hasAnnotation(method, "javax.jws.WebResult")) {
//                    Annotation webResultAnn = ParsingUtils.getAnnotation(method, ANN_WEBRESULT);
//                    classInfo.append("* Operation " + webResultAnn.getProperty("name") + "\n");
//                    
//                    // Extract parameters info
//                    for (JavaParameter parameter : method.getParameters()) {
//                        Annotation webParamAnn = ParsingUtils.getAnnotation(parameter, ANN_WEBPARAM);
//                        classInfo.append("  " + webParamAnn.getProperty("name").getParameterValue()
//                                + " : " + parameter.getType().toString() + "\n");
//                    }
//                }
//            }
        }
        
        return discoveredNodes;
    }
}
