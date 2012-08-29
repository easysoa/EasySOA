/**
 * 
 */
package org.easysoa.json;

/**
 * @author jguillemotte
 *
 */
public class MockInMessage {

    private String rawContent;
    
    /**
     * 
     * @param rawContent
     */
    public void setRawContent(String rawContent){
        this.rawContent = rawContent;
    }
    
    /**
     * 
     * @return
     */
    public String getRawContent(){
        return this.rawContent;
    }
    
}
