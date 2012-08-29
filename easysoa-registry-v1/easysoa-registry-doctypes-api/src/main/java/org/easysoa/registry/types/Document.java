package org.easysoa.registry.types;

import java.io.Serializable;

public interface Document {

    public static final String XPATH_TITLE = "dc:title";

    String getTitle() throws Exception;

    void setTitle(String title) throws Exception;

    Object getProperty(String xpath) throws Exception;

    void setProperty(String xpath, Serializable value) throws Exception;
    
}
