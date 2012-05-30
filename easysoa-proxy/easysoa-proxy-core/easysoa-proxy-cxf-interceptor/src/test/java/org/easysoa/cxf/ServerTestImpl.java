/**
 * 
 */
package org.easysoa.cxf;

/**
 * @author jguillemotte
 *
 */
public class ServerTestImpl implements ServerTest {

    /* (non-Javadoc)
     * @see org.easysoa.cxf.ServerTest#testMethod()
     */
    @Override
    public String testMethod() {
        return "This is a  test";
    }

}
