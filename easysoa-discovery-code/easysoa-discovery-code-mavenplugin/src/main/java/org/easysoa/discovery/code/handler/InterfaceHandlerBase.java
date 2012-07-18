package org.easysoa.discovery.code.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.MavenDeliverable;
import org.easysoa.discovery.code.ParsingUtils;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.Type;

public abstract class InterfaceHandlerBase implements SourcesHandler {
    
    private static final String ANN_INJECT = "javax.inject.Inject";

    // configuration :
    
    private boolean allInjected = true;
    private Set<String> injectionAnnotations = new HashSet<String>();

    // state :
    
    /** to be filled by extending classes */
    protected List<JavaClass> wsClientsAndItfs = new ArrayList<JavaClass>();
    
    protected InterfaceHandlerBase() {
        this.injectionAnnotations.add(ANN_INJECT);
        this.injectionAnnotations.add("org.osoa.sca.annotations.Reference");
        this.injectionAnnotations.add("org.springframework.beans.factory.annotation.Autowired");
        this.injectionAnnotations.add("com.google.inject.Inject");
        this.injectionAnnotations.add("javax.ejb.EJB");
    }



    protected void handleInjectedMembers(JavaClass c, MavenDeliverable deliverable, Log log) {
        // Java 6 injection of fields by service-annotated interfaces 
        // in java 6-injected fields :
        HashSet<String> injectedBeanProperties = new HashSet<String>();
        for (JavaField javaField : c.getFields()) { // TODO also superfields...
            addConsumerFoundInInjectedMember(deliverable, javaField, javaField.getType(), javaField.getName(), injectedBeanProperties);
        }
        // in java 6-injected setters :
        for (BeanProperty beanProperty : c.getBeanProperties()) {
            JavaMethod method = beanProperty.getMutator();
            if (method != null) {
                JavaParameter[] parameters = method.getParameters();
                if (parameters.length == 1) {
                    addConsumerFoundInInjectedMember(deliverable, method, parameters[0].getType(), beanProperty.getName(), injectedBeanProperties);
                }
            }
        }
    }


    private void addConsumerFoundInInjectedMember(MavenDeliverable deliverable,
            AbstractJavaEntity injectedMember, Type injectedType,
            String beanPropertyName, HashSet<String> injectedBeanProperties) {
        if (injectedBeanProperties.contains(beanPropertyName)) {
            return;
        }
        String injectionAnnotation = getInjectionAnnotation(injectedMember);
        if (injectionAnnotation != null || allInjected) {
            if (isWSClientOrItf(injectedType)) {
                deliverable.addRequirement(
                        injectedMember.getParentClass().getFullyQualifiedName()
                        + " consumes WS of JAX-WS interface " 
                        + injectedType.getJavaClass().getFullyQualifiedName()
                        + " (through property " + beanPropertyName + " injected by "
                        + ((injectionAnnotation != null) ? injectionAnnotation : "unknown"));
                injectedBeanProperties.add(beanPropertyName);
            }
        }
    }

    private String getInjectionAnnotation(AbstractJavaEntity javaInjectionMember) {
        for (String injectionAnnotation : this.injectionAnnotations) {
            if (ParsingUtils.hasAnnotation(javaInjectionMember, injectionAnnotation)) {
                return injectionAnnotation;
            }
        }
        return null;
    }
    
    private boolean isWSClientOrItf(Type type) {
        return wsClientsAndItfs.contains(type.getJavaClass());
    }
}
