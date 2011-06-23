
package fr.inria.galaxy.j1.scenario1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the fr.inria.galaxy.j1.scenario1 package. 
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

    private final static QName _Process_QNAME = new QName("http://scenario1.j1.galaxy.inria.fr/", "process");
    private final static QName _ProcessResponse_QNAME = new QName("http://scenario1.j1.galaxy.inria.fr/", "processResponse");
    private final static QName _ProcessArg0_QNAME = new QName("http://scenario1.j1.galaxy.inria.fr/", "arg0");
    private final static QName _ProcessArg1_QNAME = new QName("http://scenario1.j1.galaxy.inria.fr/", "arg1");
    private final static QName _ProcessResponseReturn_QNAME = new QName("http://scenario1.j1.galaxy.inria.fr/", "return");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fr.inria.galaxy.j1.scenario1
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
     * Create an instance of {@link JAXBElement }{@code <}{@link Process }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://scenario1.j1.galaxy.inria.fr/", name = "process")
    public JAXBElement<Process> createProcess(Process value) {
        return new JAXBElement<Process>(_Process_QNAME, Process.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://scenario1.j1.galaxy.inria.fr/", name = "processResponse")
    public JAXBElement<ProcessResponse> createProcessResponse(ProcessResponse value) {
        return new JAXBElement<ProcessResponse>(_ProcessResponse_QNAME, ProcessResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://scenario1.j1.galaxy.inria.fr/", name = "arg0", scope = Process.class)
    public JAXBElement<String> createProcessArg0(String value) {
        return new JAXBElement<String>(_ProcessArg0_QNAME, String.class, Process.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://scenario1.j1.galaxy.inria.fr/", name = "arg1", scope = Process.class)
    public JAXBElement<String> createProcessArg1(String value) {
        return new JAXBElement<String>(_ProcessArg1_QNAME, String.class, Process.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://scenario1.j1.galaxy.inria.fr/", name = "return", scope = ProcessResponse.class)
    public JAXBElement<String> createProcessResponseReturn(String value) {
        return new JAXBElement<String>(_ProcessResponseReturn_QNAME, String.class, ProcessResponse.class, value);
    }

}
