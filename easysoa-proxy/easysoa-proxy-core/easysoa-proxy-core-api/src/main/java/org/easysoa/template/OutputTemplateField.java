/**
 * 
 */
package org.easysoa.template;

/**
 * @author jguillemotte
 *
 */
public class OutputTemplateField extends AbstractTemplateField {
    
    /**
     * 
     */
    public OutputTemplateField(){
        super();
    }

    @Override
    public int getPathParamPosition() {
        // Unused method in case of output field
        return 0;
    }

    @Override
    public void setPathParamPosition(int pathParamPosition) {
        // Unused method in case of output field        
    }
    
}
