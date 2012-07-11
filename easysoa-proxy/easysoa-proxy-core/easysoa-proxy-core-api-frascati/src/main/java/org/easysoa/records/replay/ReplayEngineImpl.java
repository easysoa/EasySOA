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

/**
 * 
 */
package org.easysoa.records.replay;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.easysoa.logs.LogEngine;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.ExchangeRecordStore;
import org.easysoa.records.RecordCollection;
import org.easysoa.records.StoreCollection;
import org.easysoa.records.assertions.AssertionEngine;
import org.easysoa.records.assertions.AssertionReport;
import org.easysoa.records.assertions.AssertionResult;
import org.easysoa.records.assertions.AssertionSuggestionService;
import org.easysoa.records.assertions.AssertionSuggestions;
import org.easysoa.records.persistence.filesystem.ProxyFileStore;
import org.easysoa.reports.Report;
import org.easysoa.simulation.SimulationEngine;
import org.easysoa.template.TemplateEngine;
import org.easysoa.template.TemplateFieldSuggestions;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.util.RequestForwarder;

/**
 * Contains only the replay code. Other functionalities as assertions engine or template engine are plugged in the replay engine
 * Replay service implementation must use replay engine methods.
 * 
 * @author jguillemotte
 *
 */
@Scope("composite")
public class ReplayEngineImpl implements ReplayEngine {
    
    // SCA reference to assertion engine
    @Reference
    AssertionEngine assertionEngine;
    
    // SCA reference to template engine
    @Reference
    TemplateEngine templateEngine;
    
    // SCA reference to simulation engine
    @Reference
    SimulationEngine simulationEngine;
    
    // SCA Reference to log engine
    @Reference
    LogEngine logEngine;
    
    // Logger
    private static Logger logger = Logger.getLogger(ReplayEngineImpl.class.getName());

    // TODO : To move in template engine
    // Param setter list
    //private List<CustomParamSetter> paramSetterList = new ArrayList<CustomParamSetter>();    

    // TODO : 
    private String replaySessionName;
    
    /**
     * Constructor
     */
    public ReplayEngineImpl(){
        /*paramSetterList.add(new RestFormParamSetter());
        paramSetterList.add(new RestPathParamSetter());
        paramSetterList.add(new RestQueryParamSetter());
        paramSetterList.add(new WSDLParamSetter());*/
        this.replaySessionName = null;
        
        // Register the replay engine service in Nuxeo
        /*try{
            ExchangeReplayRegister register = new ExchangeReplayRegisterImpl();
            register.registerExchangeReplayController(this);
        } 
        catch(Exception ex){
            logger.error("Unable to register the replay engine in Nuxeo", ex);
        }*/
        
    }    
    
    /**
     * Return this object as an ExchangeReplayController
     */
    /*public ExchangeReplayController getExchangeReplayController(){
        return this;
    }*/
    
    /**
     * Start a replay session. The replay session is mainly used to execute assertion and to generate a report containing assertion results.
     * @param replaySessionName
     */
    public void startReplaySession(String replaySessionName) throws Exception {
        if(replaySessionName == null || "".equals(replaySessionName)){
            throw new IllegalArgumentException("replaySessionName must not be null or empty");
        } else if(this.replaySessionName != null){
            throw new IllegalArgumentException("A replaySession is already started, please stop the existing session before starting a new one");
        } else {
            this.replaySessionName = replaySessionName;
            Report report = new AssertionReport(replaySessionName);
            logEngine.startLogSession(replaySessionName, report);
        }
    }
    
    /**
     * Stop the replay and save the assertion report
     * @throws Exception
     */
    public void stopReplaySession() throws Exception {
        if(replaySessionName != null){
            logEngine.saveAndRemoveLogSession(replaySessionName);
            replaySessionName = null;
        }
    }
    
    /**
     * Returns the exchange record list for a given store
     */
    @Override
    public RecordCollection getExchangeRecordlist(String exchangeRecordStoreName) throws Exception {
        logger.debug("getExchangeRecordlist method called for store : " + exchangeRecordStoreName);
        List<ExchangeRecord> recordList = new ArrayList<ExchangeRecord>();
        try {
            ProxyFileStore erfs = new ProxyFileStore();
            recordList = erfs.getExchangeRecordlist(exchangeRecordStoreName);
        }
        catch (Exception ex) {
            logger.error("An error occurs during the listing of exchanges records", ex);
            throw new Exception("An error occurs during the listing of exchanges records", ex);
        }
        logger.debug("recordedList size = " + recordList.size());
        return new RecordCollection(recordList);
    }    
    
    /**
     * Returns the exchange record corresponding to the parameters
     */
    @Override
    public ExchangeRecord getExchangeRecord(String exchangeRecordStoreName, String exchangeID) throws Exception {
        logger.debug("getExchangeRecord method called for store : " + exchangeRecordStoreName + " and exchangeID : " + exchangeID);
        ExchangeRecord record = null;
        try {
            ProxyFileStore erfs = new ProxyFileStore();
            record = erfs.loadExchangeRecord(exchangeRecordStoreName, exchangeID, false);
        }
        catch (Exception ex) {
            logger.error("An error occurs during the list", ex);
            throw new Exception("An error occurs during the loading of exchange record with id " + exchangeID, ex);
        }
        return record;
    }
    
    /**
     * Returns the store list
     */
    @Override
    public StoreCollection getExchangeRecordStorelist() throws Exception {
        logger.debug("getExchangeRecordStorelist method called ...");
        List<ExchangeRecordStore> storeList = new ArrayList<ExchangeRecordStore>();
        try{
            ProxyFileStore erfs = new ProxyFileStore();
            for(String storeName : erfs.getExchangeRecordStorelist()){
                storeList.add(new ExchangeRecordStore(storeName)) ;
            }
        }
        catch(Exception ex){
            logger.error("An error occurs during the listing of exchanges record stores", ex);
            throw new Exception("An error occurs during the listing of exchanges record stores", ex); 
        }
        return new StoreCollection(storeList);
    }
    
    @Override
    public TemplateFieldSuggestions getTemplateFieldSuggestions(String storeName, String recordID) throws Exception {
        ProxyFileStore erfs = new ProxyFileStore();
        TemplateFieldSuggestions templateFieldSuggest = erfs.getTemplateFieldSuggestions(storeName, recordID);
        logger.debug(templateFieldSuggest.getTemplateFields().size());
        return templateFieldSuggest;
    }
    
    /**
     * Replay a record without any modifications
     * @param exchangeRecordStoreName The store where the record is stored
     * @param exchangerecordId The exchange record id to replay
     */
    @Override
    public OutMessage replay(String exchangeRecordStoreName, String exchangeRecordId) throws Exception{
        logger.debug("Replaying store : " + exchangeRecordStoreName + ", specific id : " + exchangeRecordId);
        try {
            ProxyFileStore erfs = new ProxyFileStore();
            // get the record
            if(exchangeRecordId == null || "".equals(exchangeRecordId) || exchangeRecordStoreName==null || "".equals(exchangeRecordStoreName)){
                throw new Exception("Store and record ID must not be null !");
            }
            // replay
            ExchangeRecord record = erfs.loadExchangeRecord(exchangeRecordStoreName, exchangeRecordId, false);
            return this.replay(record);
        }
        catch(Exception ex){
            logger.warn("A problem occurs during the replay of exchange record  with id " + exchangeRecordId, ex);
            throw new Exception("A problem occurs during the replay, see logs for more informations !", ex);
        }
    }
    
    /**
     * Replay a record without any modifications
     * @param record The <code>ExchangeRecord</code> to replay
     */
    public OutMessage replay(ExchangeRecord exchangeRecord) throws Exception{
        OutMessage outMessage = new OutMessage();
        try {
            // check not null
            if(exchangeRecord == null){
                throw new Exception("record must not be null !");
            }
            RequestForwarder requestForwarder;
            // Send the request
            requestForwarder = new RequestForwarder();
            outMessage = requestForwarder.send(exchangeRecord.getInMessage());
            logger.debug("Response of original exchange : " + exchangeRecord.getOutMessage().getMessageContent().getRawContent());
            logger.debug("Response of replayed exchange : " + outMessage.getMessageContent().getRawContent());

            // How to work with fields in fld files
            // Properties by properties => need to specify a property (field in fld files) and to find the corresponding prop in the response ...
            // Call assertioSuggestionService not only when a template and fields are available but also to compare replay without modifications)
            AssertionSuggestionService assertionSuggestionService = new AssertionSuggestionService();
            
            // Get default assertions
            AssertionSuggestions assertionSuggestions = assertionSuggestionService.suggestAssertions();
            // Execute assertions
            List<AssertionResult> assertionResults = assertionEngine.executeAssertions(assertionSuggestions, exchangeRecord.getOutMessage(), outMessage);
            // Recording assertion result for reporting
            if(replaySessionName != null){
                AssertionReport report = (AssertionReport) logEngine.getLogSession(replaySessionName).getReport();
                if(report != null){
                    report.AddAssertionResult(assertionResults);
                }
            }
            return outMessage;
        }
        catch(Exception ex){
            logger.warn("A problem occurs during the replay of exchange record  with id " + exchangeRecord.getExchange().getExchangeID(), ex);
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
        ProxyFileStore proxyFileStore = new ProxyFileStore();
        
        // Load exchange record
        ExchangeRecord record = proxyFileStore.loadExchangeRecord(exchangeStoreName, exchangeRecordID, true);        
        
        TemplateFieldSuggestions fieldSuggestions = templateEngine.suggestFields(record, exchangeStoreName, true);
        templateEngine.generateTemplate(fieldSuggestions, record, exchangeStoreName, true);
        OutMessage replayedResponse = templateEngine.renderTemplateAndReplay(exchangeStoreName, record, formData, false);
        // Call assertion engine to execute assertions
        AssertionSuggestions assertionSuggestions = assertionEngine.suggestAssertions(fieldSuggestions, record.getExchange().getExchangeID(), exchangeStoreName);
        List<AssertionResult> assertionResults = assertionEngine.executeAssertions(assertionSuggestions, record.getOutMessage(), replayedResponse);
        // Record assertion result for reporting
        if(replaySessionName != null){
            AssertionReport report = (AssertionReport) logEngine.getLogSession(replaySessionName).getReport();
            if(report != null){
                report.AddAssertionResult(assertionResults);
            }
        }
        return replayedResponse;
    }

    /**
     * Returns the template engine
     */
    @Override
    public TemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    /**
     * Returns the assertion engine
     */
    @Override
    public AssertionEngine getAssertionEngine() {
        return this.assertionEngine;
    }
    
    /**
     * Returns the simulation engine
     */
    @Override
    public SimulationEngine getSimulationEngine() {
        return this.simulationEngine;
    }
   
    /**
     * Replay a complete store
     */
    @Override
    public void replayRunRecords(String runName, String environmentName) throws InvalidParameterException, IOException {
        // Call the replay method for the specified run
        if(runName == null || "".equals(runName)){
            throw new InvalidParameterException("the parameter runName must not be null, nor empty");
        }
        try{
            // Get the list of the records contained in the specified store
            RecordCollection recordCollection = getExchangeRecordlist(runName);
            Collection<ExchangeRecord> recordList = recordCollection.getRecords();
            for(ExchangeRecord record : recordList){
                replay(record);                
            }
        }
        catch(Exception ex){
            throw new IOException("An error occurs during the replay of run : " + runName ,ex);
        }
    }

    /**
     * Returns a list with all the run name (or store name)
     */
    @Override
    public String[] getAllRunNamesArray() {
        List<String> storeList = new ArrayList<String>();
        try {
            ProxyFileStore erfs = new ProxyFileStore();
            storeList = erfs.getExchangeRecordStorelist();
        }
        catch (Exception ex) {
            logger.error("An error occurs in the getAllRunNames method", ex);
        }
        String[] runList = new String[0];
        return storeList.toArray(runList);
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
