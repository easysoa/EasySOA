
package org.talend.schemas.esb.locator._2011._11;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.talend.schemas.esb.locator._2011._11 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _LookupEndpoints_QNAME = new QName("http://talend.org/schemas/esb/locator/2011/11", "lookupEndpoints");
    private final static QName _LookupEndpoint_QNAME = new QName("http://talend.org/schemas/esb/locator/2011/11", "lookupEndpoint");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.talend.schemas.esb.locator._2011._11
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RegisterEndpoint }
     * 
     */
    public RegisterEndpoint createRegisterEndpoint() {
        return new RegisterEndpoint();
    }

    /**
     * Create an instance of {@link SLPropertiesType }
     * 
     */
    public SLPropertiesType createSLPropertiesType() {
        return new SLPropertiesType();
    }

    /**
     * Create an instance of {@link InterruptionFaultDetail }
     * 
     */
    public InterruptionFaultDetail createInterruptionFaultDetail() {
        return new InterruptionFaultDetail();
    }

    /**
     * Create an instance of {@link RegisterEndpointResponse }
     * 
     */
    public RegisterEndpointResponse createRegisterEndpointResponse() {
        return new RegisterEndpointResponse();
    }

    /**
     * Create an instance of {@link LookupEndpointsResponse }
     * 
     */
    public LookupEndpointsResponse createLookupEndpointsResponse() {
        return new LookupEndpointsResponse();
    }

    /**
     * Create an instance of {@link UnregisterEndpointResponse }
     * 
     */
    public UnregisterEndpointResponse createUnregisterEndpointResponse() {
        return new UnregisterEndpointResponse();
    }

    /**
     * Create an instance of {@link ServiceLocatorFaultDetail }
     * 
     */
    public ServiceLocatorFaultDetail createServiceLocatorFaultDetail() {
        return new ServiceLocatorFaultDetail();
    }

    /**
     * Create an instance of {@link LookupRequestType }
     * 
     */
    public LookupRequestType createLookupRequestType() {
        return new LookupRequestType();
    }

    /**
     * Create an instance of {@link LookupEndpointResponse }
     * 
     */
    public LookupEndpointResponse createLookupEndpointResponse() {
        return new LookupEndpointResponse();
    }

    /**
     * Create an instance of {@link UnregisterEndpoint }
     * 
     */
    public UnregisterEndpoint createUnregisterEndpoint() {
        return new UnregisterEndpoint();
    }

    /**
     * Create an instance of {@link EntryType }
     * 
     */
    public EntryType createEntryType() {
        return new EntryType();
    }

    /**
     * Create an instance of {@link AssertionType }
     * 
     */
    public AssertionType createAssertionType() {
        return new AssertionType();
    }

    /**
     * Create an instance of {@link MatcherDataType }
     * 
     */
    public MatcherDataType createMatcherDataType() {
        return new MatcherDataType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LookupRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://talend.org/schemas/esb/locator/2011/11", name = "lookupEndpoints")
    public JAXBElement<LookupRequestType> createLookupEndpoints(LookupRequestType value) {
        return new JAXBElement<LookupRequestType>(_LookupEndpoints_QNAME, LookupRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LookupRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://talend.org/schemas/esb/locator/2011/11", name = "lookupEndpoint")
    public JAXBElement<LookupRequestType> createLookupEndpoint(LookupRequestType value) {
        return new JAXBElement<LookupRequestType>(_LookupEndpoint_QNAME, LookupRequestType.class, null, value);
    }

}
