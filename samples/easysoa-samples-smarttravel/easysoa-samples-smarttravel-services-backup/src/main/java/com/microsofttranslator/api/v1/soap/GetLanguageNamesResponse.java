
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
 *         &lt;element name="GetLanguageNamesResult" type="{http://api.microsofttranslator.com/v1/soap.svc}ArrayOfstring" minOccurs="0"/>
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
    "getLanguageNamesResult"
})
@XmlRootElement(name = "GetLanguageNamesResponse")
public class GetLanguageNamesResponse {

    @XmlElementRef(name = "GetLanguageNamesResult", namespace = "http://api.microsofttranslator.com/v1/soap.svc", type = JAXBElement.class/*, required = false*/)
    protected JAXBElement<ArrayOfstring> getLanguageNamesResult;

    /**
     * Gets the value of the getLanguageNamesResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public JAXBElement<ArrayOfstring> getGetLanguageNamesResult() {
        return getLanguageNamesResult;
    }

    /**
     * Sets the value of the getLanguageNamesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}
     *     
     */
    public void setGetLanguageNamesResult(JAXBElement<ArrayOfstring> value) {
        this.getLanguageNamesResult = ((JAXBElement<ArrayOfstring> ) value);
    }

}
