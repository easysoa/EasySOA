package org.easysoa.discovery.code.handler;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.registry.rest.client.types.DeliverableInformation;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.Type;


/**
 * Provides Java-generic discovery features for interfaces, for now only
 * detection of Java interface-typed injected (through various methods) members.
 * 
 * This provides only potential, coarse grain (among a set of deployed deliverables,
 * any service provider implementation MAY use any service consumer implementation) &
 * partial (services may also be consumed after being looked up in a registry, not
 * only through member injection) dependencies between services.
 * 
 * These dependencies should be refined by providing EasySOA with explicit
 * architecture information about architectural components (classified deliverable
 * may already be a good guide there) and business processes. 
 * 
 * LATER Thoughts about more injection strategies :
 * recursive (including native Java services being injected in one another, but adds complexity),
 * imports (but qdox can't), non-null assigned variable (but requires almost being runtime)
 * 
 * @author mdutoo
 *
 */
public abstract class InterfaceHandlerBase implements SourcesHandler {
    
    private static final String ANN_INJECT = "javax.inject.Inject";

    // configuration :
    
    private boolean allInjected = true;
    protected Set<String> injectionAnnotations = new HashSet<String>();

    // state :
    
    /** to be filled by extending classes */
    protected HashSet<Type> wsInjectableTypeSet = new HashSet<Type>();
    
    protected InterfaceHandlerBase() {
        this.injectionAnnotations.add(ANN_INJECT); // Java 6
        this.injectionAnnotations.add("org.osoa.sca.annotations.Reference");
        this.injectionAnnotations.add("org.springframework.beans.factory.annotation.Autowired");
        this.injectionAnnotations.add("com.google.inject.Inject");
        this.injectionAnnotations.add("javax.ejb.EJB");
    }



    protected void handleInjectedMembers(JavaClass c, DeliverableInformation deliverable, Log log) {
        // Java 6 (and other methods) injection of fields by service-annotated interfaces 
        // in injected fields :
        HashSet<String> injectedBeanProperties = new HashSet<String>();
        for (JavaField javaField : c.getFields()) { // TODO also superfields...
            addConsumerFoundInInjectedMember(deliverable, javaField, javaField.getType(), javaField.getName(), injectedBeanProperties);
        }
        // in injected setters :
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


    private void addConsumerFoundInInjectedMember(DeliverableInformation deliverable,
            AbstractJavaEntity injectedMember, Type injectedType,
            String beanPropertyName, HashSet<String> injectedBeanProperties) {
        if (injectedBeanProperties.contains(beanPropertyName)) {
            return;
        }
        String injectionAnnotation = getInjectionAnnotation(injectedMember);
        if (allInjected || injectionAnnotation != null) {
            if (isWSInjectableType(injectedType)) {
                // TODO
               /* deliverable.addRequirement(
                        injectedMember.getParentClass().getFullyQualifiedName()
                        + " consumes WS of JAX-WS interface (or client, provider) " 
                        + injectedType.getJavaClass().getFullyQualifiedName()
                        + " (through property " + beanPropertyName + " injected by "
                        + ((injectionAnnotation != null) ? injectionAnnotation : "unknown"));*/
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
    
    private boolean isWSInjectableType(Type type) {
        return wsInjectableTypeSet.contains(type);
    }


    protected JavaClass getWsItf(JavaClass c) {
        for (JavaClass itfClass : c.getImplementedInterfaces()) {
            if (this.wsInjectableTypeSet.contains(itfClass.asType())) {
                return itfClass;
            }
        }
        return null;
    }
    
}
