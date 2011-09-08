
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
 *         &lt;element name="getCurrencyValueResult" type="{http://www.w3.org/2001/XMLSchema}double"/>
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
    "getCurrencyValueResult"
})
@XmlRootElement(name = "getCurrencyValueResponse")
public class GetCurrencyValueResponse {

    protected double getCurrencyValueResult;

    /**
     * Gets the value of the getCurrencyValueResult property.
     * 
     */
    public double getGetCurrencyValueResult() {
        return getCurrencyValueResult;
    }

    /**
     * Sets the value of the getCurrencyValueResult property.
     * 
     */
    public void setGetCurrencyValueResult(double value) {
        this.getCurrencyValueResult = value;
    }

}
