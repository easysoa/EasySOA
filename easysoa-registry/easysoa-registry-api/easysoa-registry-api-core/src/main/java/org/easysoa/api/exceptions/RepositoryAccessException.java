package org.easysoa.api.exceptions;


public class RepositoryAccessException extends RuntimeException {

    public RepositoryAccessException() {
        super();
    }
    
    public RepositoryAccessException(String string) {
        super(string);
    }

    public RepositoryAccessException(Throwable e) {
        super(e);
    }
    
    public RepositoryAccessException(String string, Throwable e) {
        super(string, e);
    }

    private static final long serialVersionUID = 1L;
    
}