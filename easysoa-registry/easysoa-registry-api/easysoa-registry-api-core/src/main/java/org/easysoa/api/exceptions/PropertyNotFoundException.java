package org.easysoa.api.exceptions;

/**
 * 
 * @author mkalam-alami
 *
 */
public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException() {
        super();
    }
    
    public PropertyNotFoundException(String string) {
        super(string);
    }

    public PropertyNotFoundException(Throwable e) {
        super(e);
    }
    
    public PropertyNotFoundException(String string, Throwable e) {
        super(string, e);
    }

    private static final long serialVersionUID = 1L;
    
}