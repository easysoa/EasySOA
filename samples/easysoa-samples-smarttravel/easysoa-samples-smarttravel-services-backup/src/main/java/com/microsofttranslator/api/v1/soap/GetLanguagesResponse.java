
package com.microsofttranslator.api.v1.soap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="GetLanguagesResult" type="{http://api.microsofttranslator.com/v1/soap.svc}ArrayOfstring" minOccurs="0"/>
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
    "getLanguagesResult"
})
@XmlRootElement(name = "GetLanguagesResponse")
public class GetLanguagesResponse {

    @XmlElementRef(name = "GetLanguagesResult", namespace = "http://api.microsofttranslator.com/v1/soap.svc", type = JAXBElement.class/*, required = false*/)
    protected JAXBElement<ArrayOfstring> getLanguagesResult;

    /**
     * Gets the value of the getLanguagesResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public JAXBElement<ArrayOfstring> getGetLanguagesResult() {
        return getLanguagesResult;
    }

    /**
     * Sets the value of the getLanguagesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public void setGetLanguagesResult(JAXBElement<ArrayOfstring> value) {
        this.getLanguagesResult = ((JAXBElement<ArrayOfstring> ) value);
    }

}
