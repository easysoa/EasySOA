
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

    private final static QName _SummarizeResponseReturn_QNAME = new QName("http://scenario1.j1.galaxy.inria.fr/", "return");
    private final static QName _Summarize_QNAME = new QName("http://scenario1.j1.galaxy.inria.fr/", "summarize");
    private final static QName _SummarizeResponse_QNAME = new QName("http://scenario1.j1.galaxy.inria.fr/", "summarizeResponse");
    private final static QName _SummarizeArg4_QNAME = new QName("http://scenario1.j1.galaxy.inria.fr/", "arg4");
    private final static QName _SummarizeArg5_QNAME = new QName("http://scenario1.j1.galaxy.inria.fr/", "arg5");
    private final static QName _SummarizeArg2_QNAME = new QName("http://scenario1.j1.galaxy.inria.fr/", "arg2");
    private final static QName _SummarizeArg0_QNAME = new QName("http://scenario1.j1.galaxy.inria.fr/", "arg0");
    private final static QName _SummarizeArg1_QNAME = new QName("http://scenario1.j1.galaxy.inria.fr/", "arg1");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fr.inria.galaxy.j1.scenario1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SummarizeResponse }
     * 
     */
    public SummarizeResponse createSummarizeResponse() {
        return new SummarizeResponse();
    }

    /**
     * Create an instance of {@link Summarize }
     * 
     */
    public Summarize createSummarize() {
        return new Summarize();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://scenario1.j1.galaxy.inria.fr/", name = "return", scope = SummarizeResponse.class)
    public JAXBElement<String> createSummarizeResponseReturn(String value) {
        return new JAXBElement<String>(_SummarizeResponseReturn_QNAME, String.class, SummarizeResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Summarize }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://scenario1.j1.galaxy.inria.fr/", name = "summarize")
    public JAXBElement<Summarize> createSummarize(Summarize value) {
        return new JAXBElement<Summarize>(_Summarize_QNAME, Summarize.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SummarizeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://scenario1.j1.galaxy.inria.fr/", name = "summarizeResponse")
    public JAXBElement<SummarizeResponse> createSummarizeResponse(SummarizeResponse value) {
        return new JAXBElement<SummarizeResponse>(_SummarizeResponse_QNAME, SummarizeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://scenario1.j1.galaxy.inria.fr/", name = "arg4", scope = Summarize.class)
    public JAXBElement<String> createSummarizeArg4(String value) {
        return new JAXBElement<String>(_SummarizeArg4_QNAME, String.class, Summarize.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://scenario1.j1.galaxy.inria.fr/", name = "arg5", scope = Summarize.class)
    public JAXBElement<String> createSummarizeArg5(String value) {
        return new JAXBElement<String>(_SummarizeArg5_QNAME, String.class, Summarize.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://scenario1.j1.galaxy.inria.fr/", name = "arg2", scope = Summarize.class)
    public JAXBElement<String> createSummarizeArg2(String value) {
        return new JAXBElement<String>(_SummarizeArg2_QNAME, String.class, Summarize.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://scenario1.j1.galaxy.inria.fr/", name = "arg0", scope = Summarize.class)
    public JAXBElement<String> createSummarizeArg0(String value) {
        return new JAXBElement<String>(_SummarizeArg0_QNAME, String.class, Summarize.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://scenario1.j1.galaxy.inria.fr/", name = "arg1", scope = Summarize.class)
    public JAXBElement<String> createSummarizeArg1(String value) {
        return new JAXBElement<String>(_SummarizeArg1_QNAME, String.class, Summarize.class, value);
    }

}
