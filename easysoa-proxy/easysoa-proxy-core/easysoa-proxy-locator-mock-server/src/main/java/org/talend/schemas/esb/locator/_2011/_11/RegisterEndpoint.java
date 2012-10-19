
package org.talend.schemas.esb.locator._2011._11;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="serviceName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="endpointURL" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="binding" type="{http://talend.org/schemas/esb/locator/2011/11}BindingType"/>
 *         &lt;element name="transport" type="{http://talend.org/schemas/esb/locator/2011/11}TransportType"/>
 *         &lt;element name="properties" type="{http://talend.org/schemas/esb/locator/2011/11}SLPropertiesType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "serviceName",
    "endpointURL",
    "binding",
    "transport",
    "properties"
})
@XmlRootElement(name = "registerEndpoint")
public class RegisterEndpoint {

    @XmlElement(required = true)
    protected QName serviceName;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String endpointURL;
    @XmlElement(required = true)
    protected BindingType binding;
    @XmlElement(required = true)
    protected TransportType transport;
    protected SLPropertiesType properties;

    /**
     * Gets the value of the serviceName property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getServiceName() {
        return serviceName;
    }

    /**
     * Sets the value of the serviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setServiceName(QName value) {
        this.serviceName = value;
    }

    /**
     * Gets the value of the endpointURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndpointURL() {
        return endpointURL;
    }

    /**
     * Sets the value of the endpointURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndpointURL(String value) {
        this.endpointURL = value;
    }

    /**
     * Gets the value of the binding property.
     * 
     * @return
     *     possible object is
     *     {@link BindingType }
     *     
     */
    public BindingType getBinding() {
        return binding;
    }

    /**
     * Sets the value of the binding property.
     * 
     * @param value
     *     allowed object is
     *     {@link BindingType }
     *     
     */
    public void setBinding(BindingType value) {
        this.binding = value;
    }

    /**
     * Gets the value of the transport property.
     * 
     * @return
     *     possible object is
     *     {@link TransportType }
     *     
     */
    public TransportType getTransport() {
        return transport;
    }

    /**
     * Sets the value of the transport property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportType }
     *     
     */
    public void setTransport(TransportType value) {
        this.transport = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link SLPropertiesType }
     *     
     */
    public SLPropertiesType getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link SLPropertiesType }
     *     
     */
    public void setProperties(SLPropertiesType value) {
        this.properties = value;
    }

}
