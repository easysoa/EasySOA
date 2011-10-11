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


package com.microsofttranslator.api.v1.soap;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.microsofttranslator.api.v1.soap package. 
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

    private final static QName _GetLanguagesAppId_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "appId");
    private final static QName _AnyURI_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "anyURI");
    private final static QName _QName_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "QName");
    private final static QName _DateTime_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "dateTime");
    private final static QName _Char_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "char");
    private final static QName _ArrayOfunsignedLong_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOfunsignedLong");
    private final static QName _DateTimeOffset_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "DateTimeOffset");
    private final static QName _UnsignedShort_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "unsignedShort");
    private final static QName _Float_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "float");
    private final static QName _Long_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "long");
    private final static QName _Base64Binary_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "base64Binary");
    private final static QName _DBNull_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "DBNull");
    private final static QName _Short_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "short");
    private final static QName _Byte_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "byte");
    private final static QName _ArrayOffloat_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOffloat");
    private final static QName _Boolean_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "boolean");
    private final static QName _ArrayOfdateTime_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOfdateTime");
    private final static QName _ArrayOfshort_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOfshort");
    private final static QName _ArrayOfunsignedShort_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOfunsignedShort");
    private final static QName _ArrayOfboolean_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOfboolean");
    private final static QName _UnsignedInt_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "unsignedInt");
    private final static QName _Int_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "int");
    private final static QName _UnsignedByte_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "unsignedByte");
    private final static QName _AnyType_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "anyType");
    private final static QName _ArrayOfunsignedInt_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOfunsignedInt");
    private final static QName _ArrayOfQName_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOfQName");
    private final static QName _ArrayOfdouble_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOfdouble");
    private final static QName _ArrayOfint_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOfint");
    private final static QName _Decimal_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "decimal");
    private final static QName _Double_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "double");
    private final static QName _ArrayOfchar_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOfchar");
    private final static QName _ArrayOflong_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOflong");
    private final static QName _Duration_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "duration");
    private final static QName _ArrayOfdecimal_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOfdecimal");
    private final static QName _Guid_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "guid");
    private final static QName _String_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "string");
    private final static QName _ArrayOfstring_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOfstring");
    private final static QName _ArrayOfguid_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOfguid");
    private final static QName _UnsignedLong_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "unsignedLong");
    private final static QName _ArrayOfduration_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "ArrayOfduration");
    private final static QName _GetLanguageNamesResponseGetLanguageNamesResult_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "GetLanguageNamesResult");
    private final static QName _TranslateText_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "text");
    private final static QName _TranslateTo_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "to");
    private final static QName _TranslateFrom_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "from");
    private final static QName _TranslateResponseTranslateResult_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "TranslateResult");
    private final static QName _GetLanguagesResponseGetLanguagesResult_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "GetLanguagesResult");
    private final static QName _GetLanguageNamesLocale_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "locale");
    private final static QName _DetectResponseDetectResult_QNAME = new QName("http://api.microsofttranslator.com/v1/soap.svc", "DetectResult");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.microsofttranslator.api.v1.soap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ArrayOfunsignedLong }
     * 
     */
    public ArrayOfunsignedLong createArrayOfunsignedLong() {
        return new ArrayOfunsignedLong();
    }

    /**
     * Create an instance of {@link GetLanguagesResponse }
     * 
     */
    public GetLanguagesResponse createGetLanguagesResponse() {
        return new GetLanguagesResponse();
    }

    /**
     * Create an instance of {@link ArrayOfstring }
     * 
     */
    public ArrayOfstring createArrayOfstring() {
        return new ArrayOfstring();
    }

    /**
     * Create an instance of {@link DateTimeOffset }
     * 
     */
    public DateTimeOffset createDateTimeOffset() {
        return new DateTimeOffset();
    }

    /**
     * Create an instance of {@link DBNull }
     * 
     */
    public DBNull createDBNull() {
        return new DBNull();
    }

    /**
     * Create an instance of {@link ArrayOffloat }
     * 
     */
    public ArrayOffloat createArrayOffloat() {
        return new ArrayOffloat();
    }

    /**
     * Create an instance of {@link GetLanguageNames }
     * 
     */
    public GetLanguageNames createGetLanguageNames() {
        return new GetLanguageNames();
    }

    /**
     * Create an instance of {@link ArrayOfdateTime }
     * 
     */
    public ArrayOfdateTime createArrayOfdateTime() {
        return new ArrayOfdateTime();
    }

    /**
     * Create an instance of {@link Translate }
     * 
     */
    public Translate createTranslate() {
        return new Translate();
    }

    /**
     * Create an instance of {@link ArrayOfshort }
     * 
     */
    public ArrayOfshort createArrayOfshort() {
        return new ArrayOfshort();
    }

    /**
     * Create an instance of {@link TranslateResponse }
     * 
     */
    public TranslateResponse createTranslateResponse() {
        return new TranslateResponse();
    }

    /**
     * Create an instance of {@link ArrayOfboolean }
     * 
     */
    public ArrayOfboolean createArrayOfboolean() {
        return new ArrayOfboolean();
    }

    /**
     * Create an instance of {@link ArrayOfunsignedShort }
     * 
     */
    public ArrayOfunsignedShort createArrayOfunsignedShort() {
        return new ArrayOfunsignedShort();
    }

    /**
     * Create an instance of {@link Detect }
     * 
     */
    public Detect createDetect() {
        return new Detect();
    }

    /**
     * Create an instance of {@link ArrayOfdouble }
     * 
     */
    public ArrayOfdouble createArrayOfdouble() {
        return new ArrayOfdouble();
    }

    /**
     * Create an instance of {@link ArrayOfQName }
     * 
     */
    public ArrayOfQName createArrayOfQName() {
        return new ArrayOfQName();
    }

    /**
     * Create an instance of {@link ArrayOfunsignedInt }
     * 
     */
    public ArrayOfunsignedInt createArrayOfunsignedInt() {
        return new ArrayOfunsignedInt();
    }

    /**
     * Create an instance of {@link ArrayOflong }
     * 
     */
    public ArrayOflong createArrayOflong() {
        return new ArrayOflong();
    }

    /**
     * Create an instance of {@link ArrayOfchar }
     * 
     */
    public ArrayOfchar createArrayOfchar() {
        return new ArrayOfchar();
    }

    /**
     * Create an instance of {@link ArrayOfint }
     * 
     */
    public ArrayOfint createArrayOfint() {
        return new ArrayOfint();
    }

    /**
     * Create an instance of {@link ArrayOfdecimal }
     * 
     */
    public ArrayOfdecimal createArrayOfdecimal() {
        return new ArrayOfdecimal();
    }

    /**
     * Create an instance of {@link DetectResponse }
     * 
     */
    public DetectResponse createDetectResponse() {
        return new DetectResponse();
    }

    /**
     * Create an instance of {@link GetLanguages }
     * 
     */
    public GetLanguages createGetLanguages() {
        return new GetLanguages();
    }

    /**
     * Create an instance of {@link GetLanguageNamesResponse }
     * 
     */
    public GetLanguageNamesResponse createGetLanguageNamesResponse() {
        return new GetLanguageNamesResponse();
    }

    /**
     * Create an instance of {@link ArrayOfguid }
     * 
     */
    public ArrayOfguid createArrayOfguid() {
        return new ArrayOfguid();
    }

    /**
     * Create an instance of {@link ArrayOfduration }
     * 
     */
    public ArrayOfduration createArrayOfduration() {
        return new ArrayOfduration();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "appId", scope = GetLanguages.class)
    public JAXBElement<String> createGetLanguagesAppId(String value) {
        return new JAXBElement<String>(_GetLanguagesAppId_QNAME, String.class, GetLanguages.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "anyURI")
    public JAXBElement<String> createAnyURI(String value) {
        return new JAXBElement<String>(_AnyURI_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "QName")
    public JAXBElement<QName> createQName(QName value) {
        return new JAXBElement<QName>(_QName_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "dateTime")
    public JAXBElement<XMLGregorianCalendar> createDateTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DateTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "char")
    public JAXBElement<Integer> createChar(Integer value) {
        return new JAXBElement<Integer>(_Char_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfunsignedLong }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOfunsignedLong")
    public JAXBElement<ArrayOfunsignedLong> createArrayOfunsignedLong(ArrayOfunsignedLong value) {
        return new JAXBElement<ArrayOfunsignedLong>(_ArrayOfunsignedLong_QNAME, ArrayOfunsignedLong.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DateTimeOffset }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "DateTimeOffset")
    public JAXBElement<DateTimeOffset> createDateTimeOffset(DateTimeOffset value) {
        return new JAXBElement<DateTimeOffset>(_DateTimeOffset_QNAME, DateTimeOffset.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "unsignedShort")
    public JAXBElement<Integer> createUnsignedShort(Integer value) {
        return new JAXBElement<Integer>(_UnsignedShort_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Float }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "float")
    public JAXBElement<Float> createFloat(Float value) {
        return new JAXBElement<Float>(_Float_QNAME, Float.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "long")
    public JAXBElement<Long> createLong(Long value) {
        return new JAXBElement<Long>(_Long_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "base64Binary")
    public JAXBElement<byte[]> createBase64Binary(byte[] value) {
        return new JAXBElement<byte[]>(_Base64Binary_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DBNull }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "DBNull")
    public JAXBElement<DBNull> createDBNull(DBNull value) {
        return new JAXBElement<DBNull>(_DBNull_QNAME, DBNull.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "short")
    public JAXBElement<Short> createShort(Short value) {
        return new JAXBElement<Short>(_Short_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Byte }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "byte")
    public JAXBElement<Byte> createByte(Byte value) {
        return new JAXBElement<Byte>(_Byte_QNAME, Byte.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOffloat }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOffloat")
    public JAXBElement<ArrayOffloat> createArrayOffloat(ArrayOffloat value) {
        return new JAXBElement<ArrayOffloat>(_ArrayOffloat_QNAME, ArrayOffloat.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "boolean")
    public JAXBElement<Boolean> createBoolean(Boolean value) {
        return new JAXBElement<Boolean>(_Boolean_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfdateTime }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOfdateTime")
    public JAXBElement<ArrayOfdateTime> createArrayOfdateTime(ArrayOfdateTime value) {
        return new JAXBElement<ArrayOfdateTime>(_ArrayOfdateTime_QNAME, ArrayOfdateTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfshort }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOfshort")
    public JAXBElement<ArrayOfshort> createArrayOfshort(ArrayOfshort value) {
        return new JAXBElement<ArrayOfshort>(_ArrayOfshort_QNAME, ArrayOfshort.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfunsignedShort }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOfunsignedShort")
    public JAXBElement<ArrayOfunsignedShort> createArrayOfunsignedShort(ArrayOfunsignedShort value) {
        return new JAXBElement<ArrayOfunsignedShort>(_ArrayOfunsignedShort_QNAME, ArrayOfunsignedShort.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfboolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOfboolean")
    public JAXBElement<ArrayOfboolean> createArrayOfboolean(ArrayOfboolean value) {
        return new JAXBElement<ArrayOfboolean>(_ArrayOfboolean_QNAME, ArrayOfboolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "unsignedInt")
    public JAXBElement<Long> createUnsignedInt(Long value) {
        return new JAXBElement<Long>(_UnsignedInt_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "int")
    public JAXBElement<Integer> createInt(Integer value) {
        return new JAXBElement<Integer>(_Int_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "unsignedByte")
    public JAXBElement<Short> createUnsignedByte(Short value) {
        return new JAXBElement<Short>(_UnsignedByte_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "anyType")
    public JAXBElement<Object> createAnyType(Object value) {
        return new JAXBElement<Object>(_AnyType_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfunsignedInt }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOfunsignedInt")
    public JAXBElement<ArrayOfunsignedInt> createArrayOfunsignedInt(ArrayOfunsignedInt value) {
        return new JAXBElement<ArrayOfunsignedInt>(_ArrayOfunsignedInt_QNAME, ArrayOfunsignedInt.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfQName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOfQName")
    public JAXBElement<ArrayOfQName> createArrayOfQName(ArrayOfQName value) {
        return new JAXBElement<ArrayOfQName>(_ArrayOfQName_QNAME, ArrayOfQName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfdouble }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOfdouble")
    public JAXBElement<ArrayOfdouble> createArrayOfdouble(ArrayOfdouble value) {
        return new JAXBElement<ArrayOfdouble>(_ArrayOfdouble_QNAME, ArrayOfdouble.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfint }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOfint")
    public JAXBElement<ArrayOfint> createArrayOfint(ArrayOfint value) {
        return new JAXBElement<ArrayOfint>(_ArrayOfint_QNAME, ArrayOfint.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "decimal")
    public JAXBElement<BigDecimal> createDecimal(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_Decimal_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "double")
    public JAXBElement<Double> createDouble(Double value) {
        return new JAXBElement<Double>(_Double_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfchar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOfchar")
    public JAXBElement<ArrayOfchar> createArrayOfchar(ArrayOfchar value) {
        return new JAXBElement<ArrayOfchar>(_ArrayOfchar_QNAME, ArrayOfchar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOflong }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOflong")
    public JAXBElement<ArrayOflong> createArrayOflong(ArrayOflong value) {
        return new JAXBElement<ArrayOflong>(_ArrayOflong_QNAME, ArrayOflong.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "duration")
    public JAXBElement<Duration> createDuration(Duration value) {
        return new JAXBElement<Duration>(_Duration_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfdecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOfdecimal")
    public JAXBElement<ArrayOfdecimal> createArrayOfdecimal(ArrayOfdecimal value) {
        return new JAXBElement<ArrayOfdecimal>(_ArrayOfdecimal_QNAME, ArrayOfdecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "guid")
    public JAXBElement<String> createGuid(String value) {
        return new JAXBElement<String>(_Guid_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOfstring")
    public JAXBElement<ArrayOfstring> createArrayOfstring(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_ArrayOfstring_QNAME, ArrayOfstring.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfguid }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOfguid")
    public JAXBElement<ArrayOfguid> createArrayOfguid(ArrayOfguid value) {
        return new JAXBElement<ArrayOfguid>(_ArrayOfguid_QNAME, ArrayOfguid.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "unsignedLong")
    public JAXBElement<BigInteger> createUnsignedLong(BigInteger value) {
        return new JAXBElement<BigInteger>(_UnsignedLong_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfduration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "ArrayOfduration")
    public JAXBElement<ArrayOfduration> createArrayOfduration(ArrayOfduration value) {
        return new JAXBElement<ArrayOfduration>(_ArrayOfduration_QNAME, ArrayOfduration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "GetLanguageNamesResult", scope = GetLanguageNamesResponse.class)
    public JAXBElement<ArrayOfstring> createGetLanguageNamesResponseGetLanguageNamesResult(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_GetLanguageNamesResponseGetLanguageNamesResult_QNAME, ArrayOfstring.class, GetLanguageNamesResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "appId", scope = Translate.class)
    public JAXBElement<String> createTranslateAppId(String value) {
        return new JAXBElement<String>(_GetLanguagesAppId_QNAME, String.class, Translate.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "text", scope = Translate.class)
    public JAXBElement<String> createTranslateText(String value) {
        return new JAXBElement<String>(_TranslateText_QNAME, String.class, Translate.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "to", scope = Translate.class)
    public JAXBElement<String> createTranslateTo(String value) {
        return new JAXBElement<String>(_TranslateTo_QNAME, String.class, Translate.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "from", scope = Translate.class)
    public JAXBElement<String> createTranslateFrom(String value) {
        return new JAXBElement<String>(_TranslateFrom_QNAME, String.class, Translate.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "TranslateResult", scope = TranslateResponse.class)
    public JAXBElement<String> createTranslateResponseTranslateResult(String value) {
        return new JAXBElement<String>(_TranslateResponseTranslateResult_QNAME, String.class, TranslateResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "GetLanguagesResult", scope = GetLanguagesResponse.class)
    public JAXBElement<ArrayOfstring> createGetLanguagesResponseGetLanguagesResult(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_GetLanguagesResponseGetLanguagesResult_QNAME, ArrayOfstring.class, GetLanguagesResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "appId", scope = GetLanguageNames.class)
    public JAXBElement<String> createGetLanguageNamesAppId(String value) {
        return new JAXBElement<String>(_GetLanguagesAppId_QNAME, String.class, GetLanguageNames.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "locale", scope = GetLanguageNames.class)
    public JAXBElement<String> createGetLanguageNamesLocale(String value) {
        return new JAXBElement<String>(_GetLanguageNamesLocale_QNAME, String.class, GetLanguageNames.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "DetectResult", scope = DetectResponse.class)
    public JAXBElement<String> createDetectResponseDetectResult(String value) {
        return new JAXBElement<String>(_DetectResponseDetectResult_QNAME, String.class, DetectResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "appId", scope = Detect.class)
    public JAXBElement<String> createDetectAppId(String value) {
        return new JAXBElement<String>(_GetLanguagesAppId_QNAME, String.class, Detect.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://api.microsofttranslator.com/v1/soap.svc", name = "text", scope = Detect.class)
    public JAXBElement<String> createDetectText(String value) {
        return new JAXBElement<String>(_TranslateText_QNAME, String.class, Detect.class, value);
    }

}
