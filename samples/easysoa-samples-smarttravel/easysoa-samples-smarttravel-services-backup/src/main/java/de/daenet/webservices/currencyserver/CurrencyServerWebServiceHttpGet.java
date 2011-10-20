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
 * Contact : easysoa-dev@googlegroups.com
 */

package de.daenet.webservices.currencyserver;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.3.3
 * 2011-07-20T14:27:53.496+02:00
 * Generated source version: 2.3.3
 * 
 */
 
@WebService(targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", name = "CurrencyServerWebServiceHttpGet")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface CurrencyServerWebServiceHttpGet {

    @WebResult(name = "double", targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", partName = "Body")
    @WebMethod
    public double getDollarValue(
        @WebParam(partName = "provider", name = "provider", targetNamespace = "")
        java.lang.String provider,
        @WebParam(partName = "currency", name = "currency", targetNamespace = "")
        java.lang.String currency
    );

    @WebResult(name = "string", targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", partName = "Body")
    @WebMethod
    public java.lang.String getProviderTimestamp(
        @WebParam(partName = "providerId", name = "providerId", targetNamespace = "")
        java.lang.String providerId,
        @WebParam(partName = "provider", name = "provider", targetNamespace = "")
        java.lang.String provider
    );

    @WebResult(name = "string", targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", partName = "Body")
    @WebMethod
    public java.lang.String getProviderList();

    @WebResult(name = "DataSet", targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", partName = "Body")
    @WebMethod
    public DataSet getDataSet(
        @WebParam(partName = "provider", name = "provider", targetNamespace = "")
        java.lang.String provider
    );

    @WebResult(name = "string", targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", partName = "Body")
    @WebMethod
    public java.lang.String getXmlStream(
        @WebParam(partName = "provider", name = "provider", targetNamespace = "")
        java.lang.String provider
    );

    @WebResult(name = "double", targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", partName = "Body")
    @WebMethod
    public double getCurrencyValue(
        @WebParam(partName = "provider", name = "provider", targetNamespace = "")
        java.lang.String provider,
        @WebParam(partName = "srcCurrency", name = "srcCurrency", targetNamespace = "")
        java.lang.String srcCurrency,
        @WebParam(partName = "dstCurrency", name = "dstCurrency", targetNamespace = "")
        java.lang.String dstCurrency
    );

    @WebResult(name = "string", targetNamespace = "http://www.daenet.de/webservices/CurrencyServer", partName = "Body")
    @WebMethod
    public java.lang.String getProviderDescription(
        @WebParam(partName = "provider", name = "provider", targetNamespace = "")
        java.lang.String provider
    );
}
