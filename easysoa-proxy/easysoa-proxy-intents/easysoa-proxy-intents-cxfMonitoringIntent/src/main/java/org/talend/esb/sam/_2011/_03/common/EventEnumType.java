
package org.talend.esb.sam._2011._03.common;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for eventEnumType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="eventEnumType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="REQ_IN"/>
 *     &lt;enumeration value="REQ_OUT"/>
 *     &lt;enumeration value="RESP_IN"/>
 *     &lt;enumeration value="RESP_OUT"/>
 *     &lt;enumeration value="FAULT_IN"/>
 *     &lt;enumeration value="FAULT_OUT"/>
 *     &lt;enumeration value="SERVER_START"/>
 *     &lt;enumeration value="SERVER_STOP"/>
 *     &lt;enumeration value="SERVICE_START"/>
 *     &lt;enumeration value="SERVICE_STOP"/>
 *     &lt;enumeration value="CLIENT_CREATE"/>
 *     &lt;enumeration value="CLIENT_DESTROY"/>
 *     &lt;enumeration value="JOB_START"/>
 *     &lt;enumeration value="JOB_STOP"/>
 *     &lt;enumeration value="LOG"/>
 *     &lt;enumeration value="UNKNOWN"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "eventEnumType")
@XmlEnum
public enum EventEnumType {

    REQ_IN,
    REQ_OUT,
    RESP_IN,
    RESP_OUT,
    FAULT_IN,
    FAULT_OUT,
    SERVER_START,
    SERVER_STOP,
    SERVICE_START,
    SERVICE_STOP,
    CLIENT_CREATE,
    CLIENT_DESTROY,
    JOB_START,
    JOB_STOP,
    LOG,
    UNKNOWN;

    public String value() {
        return name();
    }

    public static EventEnumType fromValue(String v) {
        return valueOf(v);
    }

}
