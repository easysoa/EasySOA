package org.easysoa.api.exceptions;


public class SchemaNotFoundException extends RuntimeException {

    public SchemaNotFoundException() {
        super();
    }
    
    public SchemaNotFoundException(String string) {
        super(string);
    }

    public SchemaNotFoundException(Throwable e) {
        super(e);
    }
    
    public SchemaNotFoundException(String string, Throwable e) {
        super(string, e);
    }

    private static final long serialVersionUID = 1L;
    
}