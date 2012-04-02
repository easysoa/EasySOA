/**
 * 
 */
package org.easysoa.template;

/**
 * @author jguillemotte
 *
 */
public class InputTemplateField extends AbstractTemplateField {

    private int pathParamPosition;
    // Indicate if the field have to be processed by the assertion engine
    
    public InputTemplateField(){
        super();
        pathParamPosition = 0;
    }
    
    /**
     * 
     * @return
     */
    public int getPathParamPosition() {
        return pathParamPosition;
    }

    /**
     * Number to define the parameter position in url path (eg : for http://localhost:8088/1/users/show/FR3Aquitaine.xml, the param user correspond to number 4 (FR3Aquitaine.xml)), the first '/' represent the root of the path. 
     * @param pathParamPosition
     */
    public void setPathParamPosition(int pathParamPosition) {
        this.pathParamPosition = pathParamPosition;
    }

}
