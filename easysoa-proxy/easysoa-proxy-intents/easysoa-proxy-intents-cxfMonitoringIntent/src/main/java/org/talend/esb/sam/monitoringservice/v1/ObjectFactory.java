
package org.talend.esb.sam.monitoringservice.v1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.talend.esb.sam._2011._03.common.FaultType;
import org.talend.esb.sam._2011._03.common.SuccessType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.talend.esb.sam.monitoringservice.v1 package. 
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

    private final static QName _PutEventsResponse_QNAME = new QName("http://www.talend.org/esb/sam/MonitoringService/v1", "putEventsResponse");
    private final static QName _Fault_QNAME = new QName("http://www.talend.org/esb/sam/MonitoringService/v1", "fault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.talend.esb.sam.monitoringservice.v1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PutEvents }
     * 
     */
    public PutEvents createPutEvents() {
        return new PutEvents();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SuccessType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.talend.org/esb/sam/MonitoringService/v1", name = "putEventsResponse")
    public JAXBElement<SuccessType> createPutEventsResponse(SuccessType value) {
        return new JAXBElement<SuccessType>(_PutEventsResponse_QNAME, SuccessType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FaultType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.talend.org/esb/sam/MonitoringService/v1", name = "fault")
    public JAXBElement<FaultType> createFault(FaultType value) {
        return new JAXBElement<FaultType>(_Fault_QNAME, FaultType.class, null, value);
    }

}
