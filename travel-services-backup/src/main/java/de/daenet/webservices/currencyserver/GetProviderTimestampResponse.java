
package de.daenet.webservices.currencyserver;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="getProviderTimestampResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getProviderTimestampResult"
})
@XmlRootElement(name = "getProviderTimestampResponse")
public class GetProviderTimestampResponse {

    protected String getProviderTimestampResult;

    /**
     * Gets the value of the getProviderTimestampResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetProviderTimestampResult() {
        return getProviderTimestampResult;
    }

    /**
     * Sets the value of the getProviderTimestampResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetProviderTimestampResult(String value) {
        this.getProviderTimestampResult = value;
    }

}
