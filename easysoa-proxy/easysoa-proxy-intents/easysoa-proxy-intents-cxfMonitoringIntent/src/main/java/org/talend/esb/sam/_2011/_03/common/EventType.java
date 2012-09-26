
package org.talend.esb.sam._2011._03.common;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for eventType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eventType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="eventType" type="{http://www.talend.org/esb/sam/2011/03/common}eventEnumType"/>
 *         &lt;element name="originator" type="{http://www.talend.org/esb/sam/2011/03/common}originatorType"/>
 *         &lt;element name="messageInfo" type="{http://www.talend.org/esb/sam/2011/03/common}messageInfoType"/>
 *         &lt;element name="customInfo" type="{http://www.talend.org/esb/sam/2011/03/common}customInfoType"/>
 *         &lt;element name="contentCut" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="content" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventType", propOrder = {
    "timestamp",
    "eventType",
    "originator",
    "messageInfo",
    "customInfo",
    "contentCut",
    "content"
})
public class EventType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
    @XmlElement(required = true)
    protected EventEnumType eventType;
    @XmlElement(required = true)
    protected OriginatorType originator;
    @XmlElement(required = true)
    protected MessageInfoType messageInfo;
    @XmlElement(required = true)
    protected CustomInfoType customInfo;
    protected boolean contentCut;
    @XmlElement(required = true)
    @XmlMimeType("application/octet-stream")
    protected DataHandler content;

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the eventType property.
     * 
     * @return
     *     possible object is
     *     {@link EventEnumType }
     *     
     */
    public EventEnumType getEventType() {
        return eventType;
    }

    /**
     * Sets the value of the eventType property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventEnumType }
     *     
     */
    public void setEventType(EventEnumType value) {
        this.eventType = value;
    }

    /**
     * Gets the value of the originator property.
     * 
     * @return
     *     possible object is
     *     {@link OriginatorType }
     *     
     */
    public OriginatorType getOriginator() {
        return originator;
    }

    /**
     * Sets the value of the originator property.
     * 
     * @param value
     *     allowed object is
     *     {@link OriginatorType }
     *     
     */
    public void setOriginator(OriginatorType value) {
        this.originator = value;
    }

    /**
     * Gets the value of the messageInfo property.
     * 
     * @return
     *     possible object is
     *     {@link MessageInfoType }
     *     
     */
    public MessageInfoType getMessageInfo() {
        return messageInfo;
    }

    /**
     * Sets the value of the messageInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link MessageInfoType }
     *     
     */
    public void setMessageInfo(MessageInfoType value) {
        this.messageInfo = value;
    }

    /**
     * Gets the value of the customInfo property.
     * 
     * @return
     *     possible object is
     *     {@link CustomInfoType }
     *     
     */
    public CustomInfoType getCustomInfo() {
        return customInfo;
    }

    /**
     * Sets the value of the customInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomInfoType }
     *     
     */
    public void setCustomInfo(CustomInfoType value) {
        this.customInfo = value;
    }

    /**
     * Gets the value of the contentCut property.
     * 
     */
    public boolean isContentCut() {
        return contentCut;
    }

    /**
     * Sets the value of the contentCut property.
     * 
     */
    public void setContentCut(boolean value) {
        this.contentCut = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    public DataHandler getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    public void setContent(DataHandler value) {
        this.content = value;
    }

}
