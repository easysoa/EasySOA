/**
 * EasySOA Samples - Smart Travel
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
 * Contact : easysoa-dev@groups.google.com
 */


package de.daenet.webservices.currencyserver;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.daenet.webservices.currencyserver package. 
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

    private final static QName _String_QNAME = new QName("http://www.daenet.de/webservices/CurrencyServer", "string");
    private final static QName _DataSet_QNAME = new QName("http://www.daenet.de/webservices/CurrencyServer", "DataSet");
    private final static QName _Double_QNAME = new QName("http://www.daenet.de/webservices/CurrencyServer", "double");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.daenet.webservices.currencyserver
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetDataSetResponse }
     * 
     */
    public GetDataSetResponse createGetDataSetResponse() {
        return new GetDataSetResponse();
    }

    /**
     * Create an instance of {@link GetDollarValue }
     * 
     */
    public GetDollarValue createGetDollarValue() {
        return new GetDollarValue();
    }

    /**
     * Create an instance of {@link GetDataSet }
     * 
     */
    public GetDataSet createGetDataSet() {
        return new GetDataSet();
    }

    /**
     * Create an instance of {@link DataSet }
     * 
     */
    public DataSet createDataSet() {
        return new DataSet();
    }

    /**
     * Create an instance of {@link GetProviderTimestamp }
     * 
     */
    public GetProviderTimestamp createGetProviderTimestamp() {
        return new GetProviderTimestamp();
    }

    /**
     * Create an instance of {@link GetProviderList }
     * 
     */
    public GetProviderList createGetProviderList() {
        return new GetProviderList();
    }

    /**
     * Create an instance of {@link GetProviderTimestampResponse }
     * 
     */
    public GetProviderTimestampResponse createGetProviderTimestampResponse() {
        return new GetProviderTimestampResponse();
    }

    /**
     * Create an instance of {@link GetCurrencyValue }
     * 
     */
    public GetCurrencyValue createGetCurrencyValue() {
        return new GetCurrencyValue();
    }

    /**
     * Create an instance of {@link GetXmlStream }
     * 
     */
    public GetXmlStream createGetXmlStream() {
        return new GetXmlStream();
    }

    /**
     * Create an instance of {@link GetProviderListResponse }
     * 
     */
    public GetProviderListResponse createGetProviderListResponse() {
        return new GetProviderListResponse();
    }

    /**
     * Create an instance of {@link GetDollarValueResponse }
     * 
     */
    public GetDollarValueResponse createGetDollarValueResponse() {
        return new GetDollarValueResponse();
    }

    /**
     * Create an instance of {@link GetDataSetResponse.GetDataSetResult }
     * 
     */
    public GetDataSetResponse.GetDataSetResult createGetDataSetResponseGetDataSetResult() {
        return new GetDataSetResponse.GetDataSetResult();
    }

    /**
     * Create an instance of {@link GetProviderDescriptionResponse }
     * 
     */
    public GetProviderDescriptionResponse createGetProviderDescriptionResponse() {
        return new GetProviderDescriptionResponse();
    }

    /**
     * Create an instance of {@link GetCurrencyValueResponse }
     * 
     */
    public GetCurrencyValueResponse createGetCurrencyValueResponse() {
        return new GetCurrencyValueResponse();
    }

    /**
     * Create an instance of {@link GetProviderDescription }
     * 
     */
    public GetProviderDescription createGetProviderDescription() {
        return new GetProviderDescription();
    }

    /**
     * Create an instance of {@link GetXmlStreamResponse }
     * 
     */
    public GetXmlStreamResponse createGetXmlStreamResponse() {
        return new GetXmlStreamResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.daenet.de/webservices/CurrencyServer", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataSet }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.daenet.de/webservices/CurrencyServer", name = "DataSet")
    public JAXBElement<DataSet> createDataSet(DataSet value) {
        return new JAXBElement<DataSet>(_DataSet_QNAME, DataSet.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.daenet.de/webservices/CurrencyServer", name = "double")
    public JAXBElement<Double> createDouble(Double value) {
        return new JAXBElement<Double>(_Double_QNAME, Double.class, null, value);
    }

}
