/**
 * 
 */
package org.easysoa.records.replay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.ExchangeRecordStore;
import org.easysoa.records.RecordCollection;
import org.easysoa.records.StoreCollection;
import org.easysoa.records.assertions.AssertionEngine;
import org.easysoa.records.assertions.AssertionSuggestionService;
import org.easysoa.records.assertions.AssertionSuggestions;
import org.easysoa.records.persistence.filesystem.ProxyExchangeRecordFileStore;
import org.easysoa.template.TemplateEngine;
import org.easysoa.template.TemplateFieldSuggestions;
import org.easysoa.template.TemplateRenderer;
import org.easysoa.template.setters.CustomParamSetter;
import org.easysoa.template.setters.RestFormParamSetter;
import org.easysoa.template.setters.RestPathParamSetter;
import org.easysoa.template.setters.RestQueryParamSetter;
import org.easysoa.template.setters.WSDLParamSetter;
import org.osoa.sca.annotations.Reference;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.util.RequestForwarder;

/**
 * Contains only the replay code. Other functionalities as assertions engine or template engine are plugged in the replay engine
 * Replay service implementation must use replay engine methods.
 * 
 * @author jguillemotte
 *
 */
public class ReplayEngineImpl implements ReplayEngine {
   
    // Add code here to organize the work of template engine and assertion engine
    // Hum ... maybe not .. it is possible to generate the template with out to replay
    // On the contrary, it is not possible to execute assertion if the replay has not been replayed 
    
    // SCA reference to assertion engine
    @Reference
    AssertionEngine assertionEngine;
    
    // SCA reference to template engine
    @Reference
    TemplateEngine templateEngine;
    
    // SCA Reference to log engine
    //@Reference
    //LogEngine logEngine;
    
    // Logger
    private static Logger logger = Logger.getLogger(ReplayEngineImpl.class.getName());

    // TODO : To move in template engine
    // Param setter list
    private List<CustomParamSetter> paramSetterList = new ArrayList<CustomParamSetter>();    

    /**
     * Constructor
     * Set the param setter list
     */
    public ReplayEngineImpl(){
        paramSetterList.add(new RestFormParamSetter());
        paramSetterList.add(new RestPathParamSetter());
        paramSetterList.add(new RestQueryParamSetter());
        paramSetterList.add(new WSDLParamSetter());
    }    
    
    @Override
    public RecordCollection getExchangeRecordlist(String exchangeRecordStoreName) throws Exception {
        logger.debug("getExchangeRecordlist method called for store : " + exchangeRecordStoreName);
        List<ExchangeRecord> recordList = new ArrayList<ExchangeRecord>();
        try {
            ProxyExchangeRecordFileStore erfs = new ProxyExchangeRecordFileStore();
            recordList = erfs.getExchangeRecordlist(exchangeRecordStoreName);
        } catch (Exception ex) {
            logger.error("An error occurs during the listing of exchanges records", ex);
            throw new Exception("An error occurs during the listing of exchanges records", ex);
        }
        logger.debug("recordedList size = " + recordList.size());
        return new RecordCollection(recordList);
    }    
    
    @Override
    public ExchangeRecord getExchangeRecord(String exchangeRecordStoreName, String exchangeID) throws Exception {
        logger.debug("getExchangeRecord method called for store : " + exchangeRecordStoreName + " and exchangeID : " + exchangeID);
        ExchangeRecord record = null;
        try {
            ProxyExchangeRecordFileStore erfs = new ProxyExchangeRecordFileStore();
            record = erfs.load(exchangeRecordStoreName, exchangeID);
        } 
        catch (Exception ex) {
            logger.error("An error occurs during the list", ex);
            throw new Exception("An error occurs during the loading of exchange record with id " + exchangeID, ex);
        }
        return record;
    }
    
    @Override
    public StoreCollection getExchangeRecordStorelist() throws Exception {
        logger.debug("getExchangeRecordStorelist method called ...");
        List<ExchangeRecordStore> storeList = new ArrayList<ExchangeRecordStore>();
        try{
            ProxyExchangeRecordFileStore erfs = new ProxyExchangeRecordFileStore();
            storeList = erfs.getExchangeRecordStorelist();
        }
        catch(Exception ex){
            logger.error("An error occurs during the listing of exchanges record stores", ex);
            throw new Exception("An error occurs during the listing of exchanges record stores", ex); 
        }
        return new StoreCollection(storeList);
    }
    
    @Override
    public TemplateFieldSuggestions getTemplateFieldSuggestions(String storeName, String recordID) throws Exception {
        ProxyExchangeRecordFileStore erfs = new ProxyExchangeRecordFileStore();
        TemplateFieldSuggestions templateFieldSuggest = erfs.getTemplateFieldSuggestions(storeName, recordID);
        logger.debug(templateFieldSuggest.getTemplateFields().size());
        return templateFieldSuggest;
    }    
    
    /**
     * Replay a record without any modifications
     */
    @Override
    public OutMessage replay(String exchangeRecordStoreName, String exchangeRecordId) throws Exception{

        // call remote service using chosen record :
        // see how to share monit.forward(Message) code (extract it in a Util class), see also scaffolder client
        // NB. without correlated asserts, test on response are impossible,
        // however diff is possible (on server or client)
        // ex. on server : http://code.google.com/p/java-diff-utils/
        logger.debug("Replaying store : " + exchangeRecordStoreName + ", specific id : " + exchangeRecordId);
        OutMessage outMessage = new OutMessage();
        try {
            //Collection<ExchangeRecord> recordList;
            ProxyExchangeRecordFileStore erfs = new ProxyExchangeRecordFileStore();
            // get the record
            // TODO : if the record is not found, get all the record from the store
            // So need to return a Map of response with record ID as key
            if(exchangeRecordId == null || "".equals(exchangeRecordId) || exchangeRecordStoreName==null || "".equals(exchangeRecordStoreName)){
                throw new Exception("Store and record ID must not be null !");
            }
            ExchangeRecord record = erfs.load(exchangeRecordStoreName, exchangeRecordId);
            /*if(exchangeRecordId==null || "".equals(exchangeRecordId) ){
                recordList = getExchangeRecordlist(exchangeRecordStoreName).getRecords();
            } else {*/
                //recordList = new ArrayList<ExchangeRecord>();
                //recordList.add(erfs.load(exchangeRecordStoreName, exchangeRecordId));
            /*}*/
            RequestForwarder requestForwarder;
            //logger.debug("records number to replay : " + recordList.size());
            //for(ExchangeRecord record : recordList){
            // Send the request
                requestForwarder = new RequestForwarder();
                outMessage = requestForwarder.send(record.getInMessage());
                logger.debug("Response of original exchange : " + record.getOutMessage().getMessageContent().getContent());
                logger.debug("Response of replayed exchange : " + outMessage.getMessageContent().getContent());
                //responseMap.put(record.getExchange().getExchangeID(), outMessage);
                
                // Assertion Engine
                // TODO : make the assertion engine call configurable
                // How to change the type of used assertion ?
                
                // Maybe the solution is to pass the pre-configured assertionEngine as parameter (implementing an 'engine' interface)
                // and then only have to call the 'execute' method...
                // With this solution, we can execute one or several engines in a predefined order
                // problem : execute method and parameters .... not the same params for the differents engines ...
                
                // How to work with fields in fld files
                // Properties by properties => need to specify a property (field in fld files) and to find the corresponding prop in the response ...

                // TODO : Do not create a new assertion instance here, use generated asr files instead with AssertionEngine
                //StringAssertion assertion = new StringAssertion("assertion_" + record.getExchange().getExchangeID());
                
                // Call assertioSuggestionService not only when a template and fields are available but also to compare replay without modifications)
                
                AssertionSuggestionService assertionSuggestionService = new AssertionSuggestionService();
                
                // Get default assertions
                AssertionSuggestions assertionSuggestions = assertionSuggestionService.suggestAssertions();
                
                // Problem with this method when response is big ... treatment is very very long ....
                // Think there is a bug with this method, more of 5 minutes for a long xml string
                //assertion.setMethod(StringAssertionMethod.DISTANCE_LEHVENSTEIN);
                assertionEngine.executeAssertions(assertionSuggestions, record.getOutMessage(), outMessage);
                // End
            //}
            return outMessage;                
        }
        catch(Exception ex){
            logger.warn("A problem occurs during the replay of exchange record  with id " + exchangeRecordId, ex);
            throw new Exception("A problem occurs during the replay, see logs for more informations !", ex);
        }
    }
    
    /**
     * To replay a customized exchange record using template, field suggestions and assertions files generated before.
     * @param formData
     * @param exchangeStoreName
     * @param exchangeRecordID
     * @param templateName
     * @return
     * @throws Exception
     */
    // Remove template name, use hte one generated from the record
    @Override
    public OutMessage replayWithTemplate(Map<String, List<String>> formData, String exchangeStoreName, String exchangeRecordID) throws Exception {
        // Load template, assertion file .... 
        ProxyExchangeRecordFileStore erfs = new ProxyExchangeRecordFileStore();
        // Field suggestions
        TemplateFieldSuggestions fieldSuggestions = erfs.getTemplateFieldSuggestions(exchangeStoreName, exchangeRecordID);
        // Template
        
        // Assertions
        AssertionSuggestions assertionSuggestions = erfs.getAssertionSuggestions(exchangeStoreName, exchangeRecordID);
        
        // TODO passing original record ??? Maybe just the the record ID ?
        // RecordID
        ExchangeRecord record = erfs.load(exchangeStoreName, exchangeRecordID);
        
        // if template or asr files not found, throws an exception
        // Call the proxy velocity to render and execute the replay with custom params
        OutMessage replayedResponse = templateEngine.renderTemplateAndReplay(exchangeStoreName, record, formData);
        // Call assertion engine to execute assertions
        assertionEngine.executeAssertions(assertionSuggestions, record.getOutMessage(), replayedResponse);
        
        return replayedResponse;
    }

    @Override
    public TemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    @Override
    public AssertionEngine getAssertionEngine() {
        return this.assertionEngine;
    }
    
    /*@Override
    @Deprecated
    public String replayWithTemplate(MultivaluedMap<String, String> formData, String exchangeStoreName, String exchangeRecordID, String templateName) throws Exception {
        // get the exchange record inMessage
        logger.debug("storeName : " + exchangeStoreName);
        logger.debug("recordID " + exchangeRecordID);
        try {
            ExchangeRecord record = getExchangeRecord(exchangeStoreName, exchangeRecordID);
            //
            TemplateFieldSuggestions templateFieldSUggestions = getTemplateFieldSuggestions(exchangeStoreName, templateName);
            // get the new parameter values contained in the received request form, How to get unknow parameters ?
            if(record != null){
                logger.debug("Original message URL : " + record.getInMessage().buildCompleteUrl());
                String content = record.getInMessage().getMessageContent().getContent();
                logger.debug("Original message content : " + content);
                // Call a method to replace custom values in request
                // Make a copy of the InMessage and set the custom parameters
                InMessage customInMessage = getExchangeRecord(exchangeStoreName, exchangeRecordID).getInMessage();

                // Replace the the values for the fields specified in the template
                // The value to replace can be in PathParam, FormParams or QueryParams, check the paramType in template file
                // TODO : move this method setCustomParams in TemplateEngine ...
                setCustomParams(templateFieldSUggestions, customInMessage, formData);
                
                //templateEngine.renderTemplate();
                
                logger.debug("Modified path = " + customInMessage.getPath());
                
                // Send the new request and returns the response
                RequestForwarder requestForwarder = new RequestForwarder();
                OutMessage outMessage = requestForwarder.send(customInMessage);
                
                // Calling assertion engine
                // TODO : make the assertion engine call configurable               
                StringAssertion assertion = new StringAssertion("assertion_" + record.getExchange().getExchangeID());
                // Using default length method because Lehvenstein method can be very slow if message is long 
                //assertion.setMethod(StringAssertionMethod.DISTANCE_LEHVENSTEIN);
                // TODO : executing assertions in all cases ???
                assertionEngine.executeAssertion(assertion, record.getOutMessage(), outMessage);
                
                // TODO : returns only the message content, maybe it's a good idea to return the complete out message
                return outMessage.getMessageContent().getContent();
            }
        }
        catch(Exception ex){
            logger.error("An error occurs during the replay of record " + exchangeStoreName + "/" + exchangeRecordID + " with template " + templateName, ex);
            throw new Exception("An error occurs during the replay of templated record", ex);
        }
        return null;
    }*/
    
    /**
     * Method used in first solution .... Now have to use Custom ProxyImplementationVelocity or ServletImplementationVelocity
     * @param template
     * @param message
     * @param mapParams
     */
    // TODO : Move this method in TemplateEngine => in renderer, builder ... ??? 
    /*@Deprecated
    private void setCustomParams(TemplateFieldSuggestions templateFieldSuggestions, InMessage inMessage, MultivaluedMap<String, String> mapParams){
        // Write the code to set custom parameters
        // For each templateField described in the template and For each Custom Param setter in the paramSetter list,
        // call the method isOKFor and if ok call the method setParams
        for(TemplateField templateField : templateFieldSuggestions.getTemplateFields()){
            for(CustomParamSetter paramSetter : paramSetterList){
                if(paramSetter.isOkFor(templateField)){
                    paramSetter.setParam(templateField, inMessage, mapParams);
                }
            }
        }
    }*/
    
}
