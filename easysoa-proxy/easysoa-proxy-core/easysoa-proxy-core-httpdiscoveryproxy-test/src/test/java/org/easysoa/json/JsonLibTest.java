package org.easysoa.json;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JsonLibTest {

    private String meteoWsdlSample;

    @Before
    public void setUp() throws IOException {
        this.meteoWsdlSample = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("meteo_sample.wsdl"));
    }
    
    @Test
    public void testNetSfJson() throws IOException {
       
       // fill bean with wsdl field
       MockInMessage mockMessage = new MockInMessage();
       mockMessage.setRawContent(this.meteoWsdlSample);
       
       // persist bean to json
       JSONObject jsonObject = JSONObject.fromObject(mockMessage);
       File jsonFile = new File("netSfJsonSample.json");
       FileWriter jsonFileWriter = new FileWriter(jsonFile);
       jsonFileWriter.write(jsonObject.toString());
       jsonFileWriter.close();
       
       // load bean by parsing it from json
       MockInMessage loadedMessage;
       FileReader fr = new FileReader(jsonFile);
       CharBuffer buffer = CharBuffer.allocate(512);
       StringBuffer stringBuffer = new StringBuffer();
       while( fr.read(buffer) >= 0 ) {
           stringBuffer.append(buffer.flip());
           buffer.clear();         
       }
       fr.close();
       stringBuffer.trimToSize();
       HashMap<String, Class> classMap = new HashMap<String, Class>();
       loadedMessage = (MockInMessage)(JSONObject.toBean((JSONObject) JSONSerializer.toJSON(stringBuffer.toString()), MockInMessage.class, classMap));

       // fail() if it succeeds :)
       
    }

    @Test
    public void testJackson() throws IOException {
       
       // fill bean with wsdl field
       
       // persist bean to json
       
       // load bean by parsing it from json
       
        // asserts that it succeeds
    }
    
}
