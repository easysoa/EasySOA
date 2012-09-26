
package org.talend.esb.sam._2011._03.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for faultType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="faultType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="faultCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="faultMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="stackTrace" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "faultType", propOrder = {
    "faultCode",
    "faultMessage",
    "stackTrace"
})
public class FaultType {

    @XmlElement(required = true)
    protected String faultCode;
    @XmlElement(required = true)
    protected String faultMessage;
    @XmlElement(required = true)
    protected String stackTrace;

    /**
     * Gets the value of the faultCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFaultCode() {
        return faultCode;
    }

    /**
     * Sets the value of the faultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFaultCode(String value) {
        this.faultCode = value;
    }

    /**
     * Gets the value of the faultMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFaultMessage() {
        return faultMessage;
    }

    /**
     * Sets the value of the faultMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFaultMessage(String value) {
        this.faultMessage = value;
    }

    /**
     * Gets the value of the stackTrace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * Sets the value of the stackTrace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStackTrace(String value) {
        this.stackTrace = value;
    }

}
