
package org.talend.esb.sam._2011._03.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for messageInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="messageInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="messageId" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="flowId" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="porttype" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="operationName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="transport" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "messageInfoType", propOrder = {
    "messageId",
    "flowId",
    "porttype",
    "operationName",
    "transport"
})
public class MessageInfoType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String messageId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String flowId;
    @XmlElement(required = true)
    protected QName porttype;
    @XmlElement(required = true)
    protected String operationName;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String transport;

    /**
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
    }

    /**
     * Gets the value of the flowId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlowId() {
        return flowId;
    }

    /**
     * Sets the value of the flowId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlowId(String value) {
        this.flowId = value;
    }

    /**
     * Gets the value of the porttype property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getPorttype() {
        return porttype;
    }

    /**
     * Sets the value of the porttype property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setPorttype(QName value) {
        this.porttype = value;
    }

    /**
     * Gets the value of the operationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * Sets the value of the operationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperationName(String value) {
        this.operationName = value;
    }

    /**
     * Gets the value of the transport property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransport() {
        return transport;
    }

    /**
     * Sets the value of the transport property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransport(String value) {
        this.transport = value;
    }

}
