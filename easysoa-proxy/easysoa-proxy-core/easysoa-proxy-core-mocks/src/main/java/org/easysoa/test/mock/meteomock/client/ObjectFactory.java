/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */


package org.easysoa.test.mock.meteomock.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openwide.easysoa.test.mock.meteomock package. 
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

    private final static QName _GetTomorrowForecast_QNAME = new QName("http://meteomock.mock.test.easysoa.openwide.org/", "getTomorrowForecast");
    private final static QName _GetTomorrowForecastResponse_QNAME = new QName("http://meteomock.mock.test.easysoa.openwide.org/", "getTomorrowForecastResponse");
    private final static QName _GetTomorrowForecastArg0_QNAME = new QName("http://meteomock.mock.test.easysoa.openwide.org/", "arg0");
    private final static QName _GetTomorrowForecastResponseReturn_QNAME = new QName("http://meteomock.mock.test.easysoa.openwide.org/", "return");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openwide.easysoa.test.mock.meteomock
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetTomorrowForecastResponse }
     * 
     */
    public GetTomorrowForecastResponse createGetTomorrowForecastResponse() {
        return new GetTomorrowForecastResponse();
    }

    /**
     * Create an instance of {@link GetTomorrowForecast }
     * 
     */
    public GetTomorrowForecast createGetTomorrowForecast() {
        return new GetTomorrowForecast();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTomorrowForecast }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://meteomock.mock.test.easysoa.openwide.org/", name = "getTomorrowForecast")
    public JAXBElement<GetTomorrowForecast> createGetTomorrowForecast(GetTomorrowForecast value) {
        return new JAXBElement<GetTomorrowForecast>(_GetTomorrowForecast_QNAME, GetTomorrowForecast.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTomorrowForecastResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://meteomock.mock.test.easysoa.openwide.org/", name = "getTomorrowForecastResponse")
    public JAXBElement<GetTomorrowForecastResponse> createGetTomorrowForecastResponse(GetTomorrowForecastResponse value) {
        return new JAXBElement<GetTomorrowForecastResponse>(_GetTomorrowForecastResponse_QNAME, GetTomorrowForecastResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://meteomock.mock.test.easysoa.openwide.org/", name = "arg0", scope = GetTomorrowForecast.class)
    public JAXBElement<String> createGetTomorrowForecastArg0(String value) {
        return new JAXBElement<String>(_GetTomorrowForecastArg0_QNAME, String.class, GetTomorrowForecast.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://meteomock.mock.test.easysoa.openwide.org/", name = "return", scope = GetTomorrowForecastResponse.class)
    public JAXBElement<String> createGetTomorrowForecastResponseReturn(String value) {
        return new JAXBElement<String>(_GetTomorrowForecastResponseReturn_QNAME, String.class, GetTomorrowForecastResponse.class, value);
    }

}
