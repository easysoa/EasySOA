
package com.microsofttranslator.api.v1.soap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfstring;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.microsofttranslator.api.v1.soap package. 
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

    private final static QName _DetectAppId_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "appId");
    private final static QName _DetectText_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "text");
    private final static QName _GetLanguageNamesResponseGetLanguageNamesResult_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "GetLanguageNamesResult");
    private final static QName _TranslateTo_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "to");
    private final static QName _TranslateFrom_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "from");
    private final static QName _GetLanguagesResponseGetLanguagesResult_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "GetLanguagesResult");
    private final static QName _DetectResponseDetectResult_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "DetectResult");
    private final static QName _GetLanguageNamesLocale_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "locale");
    private final static QName _TranslateResponseTranslateResult_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "TranslateResult");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.microsofttranslator.api.v1.soap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Detect }
     * 
     */
    public Detect createDetect() {
        return new Detect();
    }

    /**
     * Create an instance of {@link GetLanguageNamesResponse }
     * 
     */
    public GetLanguageNamesResponse createGetLanguageNamesResponse() {
        return new GetLanguageNamesResponse();
    }

    /**
     * Create an instance of {@link Translate }
     * 
     */
    public Translate createTranslate() {
        return new Translate();
    }

    /**
     * Create an instance of {@link GetLanguagesResponse }
     * 
     */
    public GetLanguagesResponse createGetLanguagesResponse() {
        return new GetLanguagesResponse();
    }

    /**
     * Create an instance of {@link DetectResponse }
     * 
     */
    public DetectResponse createDetectResponse() {
        return new DetectResponse();
    }

    /**
     * Create an instance of {@link GetLanguageNames }
     * 
     */
    public GetLanguageNames createGetLanguageNames() {
        return new GetLanguageNames();
    }

    /**
     * Create an instance of {@link GetLanguages }
     * 
     */
    public GetLanguages createGetLanguages() {
        return new GetLanguages();
    }

    /**
     * Create an instance of {@link TranslateResponse }
     * 
     */
    public TranslateResponse createTranslateResponse() {
        return new TranslateResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "appId", scope = Detect.class)
    public JAXBElement<String> createDetectAppId(String value) {
        return new JAXBElement<String>(_DetectAppId_QNAME, String.class, Detect.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "text", scope = Detect.class)
    public JAXBElement<String> createDetectText(String value) {
        return new JAXBElement<String>(_DetectText_QNAME, String.class, Detect.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "GetLanguageNamesResult", scope = GetLanguageNamesResponse.class)
    public JAXBElement<ArrayOfstring> createGetLanguageNamesResponseGetLanguageNamesResult(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_GetLanguageNamesResponseGetLanguageNamesResult_QNAME, ArrayOfstring.class, GetLanguageNamesResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "appId", scope = Translate.class)
    public JAXBElement<String> createTranslateAppId(String value) {
        return new JAXBElement<String>(_DetectAppId_QNAME, String.class, Translate.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "text", scope = Translate.class)
    public JAXBElement<String> createTranslateText(String value) {
        return new JAXBElement<String>(_DetectText_QNAME, String.class, Translate.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "to", scope = Translate.class)
    public JAXBElement<String> createTranslateTo(String value) {
        return new JAXBElement<String>(_TranslateTo_QNAME, String.class, Translate.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "from", scope = Translate.class)
    public JAXBElement<String> createTranslateFrom(String value) {
        return new JAXBElement<String>(_TranslateFrom_QNAME, String.class, Translate.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "GetLanguagesResult", scope = GetLanguagesResponse.class)
    public JAXBElement<ArrayOfstring> createGetLanguagesResponseGetLanguagesResult(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_GetLanguagesResponseGetLanguagesResult_QNAME, ArrayOfstring.class, GetLanguagesResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "DetectResult", scope = DetectResponse.class)
    public JAXBElement<String> createDetectResponseDetectResult(String value) {
        return new JAXBElement<String>(_DetectResponseDetectResult_QNAME, String.class, DetectResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "appId", scope = GetLanguageNames.class)
    public JAXBElement<String> createGetLanguageNamesAppId(String value) {
        return new JAXBElement<String>(_DetectAppId_QNAME, String.class, GetLanguageNames.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "locale", scope = GetLanguageNames.class)
    public JAXBElement<String> createGetLanguageNamesLocale(String value) {
        return new JAXBElement<String>(_GetLanguageNamesLocale_QNAME, String.class, GetLanguageNames.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "appId", scope = GetLanguages.class)
    public JAXBElement<String> createGetLanguagesAppId(String value) {
        return new JAXBElement<String>(_DetectAppId_QNAME, String.class, GetLanguages.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "TranslateResult", scope = TranslateResponse.class)
    public JAXBElement<String> createTranslateResponseTranslateResult(String value) {
        return new JAXBElement<String>(_TranslateResponseTranslateResult_QNAME, String.class, TranslateResponse.class, value);
    }

}
