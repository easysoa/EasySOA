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

package com.microsofttranslator.api.v1.soap_svc;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
//import javax.jws.WebService;
//import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.3.3
 * 2011-07-22T18:52:21.962+02:00
 * Generated source version: 2.3.3
 * 
 */
 
//@WebService(targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "LanguageService")
//@XmlSeeAlso({com.microsofttranslator.api.v1.soap.ObjectFactory.class})
public interface LanguageService {

    @WebResult(name = "DetectResult", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc")
    @Action(input = "http://api.microsofttranslator.com/v1/soap.svc/LanguageService/Detect", output = "http://api.microsofttranslator.com/v1/soap.svc/LanguageService/DetectResponse")
    @RequestWrapper(localName = "Detect", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc", className = "com.microsofttranslator.api.v1.soap.Detect")
    @WebMethod(operationName = "Detect", action = "http://api.microsofttranslator.com/v1/soap.svc/LanguageService/Detect")
    @ResponseWrapper(localName = "DetectResponse", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc", className = "com.microsofttranslator.api.v1.soap.DetectResponse")
    public java.lang.String detect(
        @WebParam(name = "appId", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc")
        java.lang.String appId,
        @WebParam(name = "text", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc")
        java.lang.String text
    );

    @WebResult(name = "TranslateResult", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc")
    @Action(input = "http://api.microsofttranslator.com/v1/soap.svc/LanguageService/Translate", output = "http://api.microsofttranslator.com/v1/soap.svc/LanguageService/TranslateResponse")
    @RequestWrapper(localName = "Translate", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc", className = "com.microsofttranslator.api.v1.soap.Translate")
    @WebMethod(operationName = "Translate", action = "http://api.microsofttranslator.com/v1/soap.svc/LanguageService/Translate")
    @ResponseWrapper(localName = "TranslateResponse", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc", className = "com.microsofttranslator.api.v1.soap.TranslateResponse")
    public java.lang.String translate(
        @WebParam(name = "appId", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc")
        java.lang.String appId,
        @WebParam(name = "text", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc")
        java.lang.String text,
        @WebParam(name = "from", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc")
        java.lang.String from,
        @WebParam(name = "to", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc")
        java.lang.String to
    );

    @WebResult(name = "GetLanguageNamesResult", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc")
    @Action(input = "http://api.microsofttranslator.com/v1/soap.svc/LanguageService/GetLanguageNames", output = "http://api.microsofttranslator.com/v1/soap.svc/LanguageService/GetLanguageNamesResponse")
    @RequestWrapper(localName = "GetLanguageNames", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc", className = "com.microsofttranslator.api.v1.soap.GetLanguageNames")
    @WebMethod(operationName = "GetLanguageNames", action = "http://api.microsofttranslator.com/v1/soap.svc/LanguageService/GetLanguageNames")
    @ResponseWrapper(localName = "GetLanguageNamesResponse", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc", className = "com.microsofttranslator.api.v1.soap.GetLanguageNamesResponse")
    public com.microsofttranslator.api.v1.soap.ArrayOfstring getLanguageNames(
        @WebParam(name = "appId", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc")
        java.lang.String appId,
        @WebParam(name = "locale", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc")
        java.lang.String locale
    );

    @WebResult(name = "GetLanguagesResult", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc")
    @Action(input = "http://api.microsofttranslator.com/v1/soap.svc/LanguageService/GetLanguages", output = "http://api.microsofttranslator.com/v1/soap.svc/LanguageService/GetLanguagesResponse")
    @RequestWrapper(localName = "GetLanguages", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc", className = "com.microsofttranslator.api.v1.soap.GetLanguages")
    @WebMethod(operationName = "GetLanguages", action = "http://api.microsofttranslator.com/v1/soap.svc/LanguageService/GetLanguages")
    @ResponseWrapper(localName = "GetLanguagesResponse", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc", className = "com.microsofttranslator.api.v1.soap.GetLanguagesResponse")
    public com.microsofttranslator.api.v1.soap.ArrayOfstring getLanguages(
        @WebParam(name = "appId", targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc")
        java.lang.String appId
    );
}
