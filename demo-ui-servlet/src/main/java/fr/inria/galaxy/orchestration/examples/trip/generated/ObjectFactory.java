
package fr.inria.galaxy.orchestration.examples.trip.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the fr.inria.galaxy.orchestration.examples.trip.generated package. 
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

    private final static QName _ProcessParam0_QNAME = new QName("", "param0");
    private final static QName _ProcessParam1_QNAME = new QName("", "param1");
    private final static QName _ProcessResponseReturn_QNAME = new QName("", "return");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fr.inria.galaxy.orchestration.examples.trip.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Process }
     * 
     */
    public Process createProcess() {
        return new Process();
    }

    /**
     * Create an instance of {@link ProcessResponse }
     * 
     */
    public ProcessResponse createProcessResponse() {
        return new ProcessResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "param0", scope = Process.class)
    public JAXBElement<String> createProcessParam0(String value) {
        return new JAXBElement<String>(_ProcessParam0_QNAME, String.class, Process.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "param1", scope = Process.class)
    public JAXBElement<String> createProcessParam1(String value) {
        return new JAXBElement<String>(_ProcessParam1_QNAME, String.class, Process.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "return", scope = ProcessResponse.class)
    public JAXBElement<String> createProcessResponseReturn(String value) {
        return new JAXBElement<String>(_ProcessResponseReturn_QNAME, String.class, ProcessResponse.class, value);
    }

}
