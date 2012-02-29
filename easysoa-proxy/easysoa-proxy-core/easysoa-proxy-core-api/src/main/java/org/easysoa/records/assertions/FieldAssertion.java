/**
 * 
 */
package org.easysoa.records.assertions;

import com.openwide.easysoa.message.OutMessage;

/**
 * Used to associate a field with an assertion
 * 
 * => why not add directly a field name param in the existing assertions ?
 * 
 * @author jguillemotte
 *
 */
public class FieldAssertion extends AbstractAssertion {
    
    // Field name, the target assertion will be executed on this field 
    private String fieldName;
    
    // The assertion the execute on the field
    private Class targetAssertion;
    
    public FieldAssertion(String id) {
        super(id);
    }

    @Override
    public void setConfiguration(String configurationString) {
        // TODO Auto-generated method stub
    }

    @Override
    public AssertionResult check(OutMessage originalMessage, OutMessage replayedMessage) {
        // TODO Auto-generated method stub
        
        // If message content is xml
        /*if(Message.CONTENT_TYPE is xml){
         
        }*/
        // else if message content is json
        /*else if(){
            
        }*/
        return null;
    }

}
