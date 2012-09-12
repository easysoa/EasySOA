package org.easysoa.discovery.code;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.maven.plugin.logging.Log;

public class LogOutputStream extends OutputStream {

    private StringBuffer buffer = new StringBuffer();
    
    private final Log log;
    
    public LogOutputStream(Log log) {
        this.log = log;
        
    }
    
    @Override
    public void write(int b) throws IOException {
        buffer.append((char) b);
    }
    
    @Override
    public void close() throws IOException {
        super.close();
        log.info(buffer.toString());
    }

}
