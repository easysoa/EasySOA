package org.easysoa.test;
import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;

/**
 * 
 * Jetty server wrapper that allows to easily create static web servers for testing purposes.
 *  
 * @author mkalam-alami
 *
 */
public class StaticWebServer {
    
    private static Logger logger = Logger.getLogger(StaticWebServer.class);
    
    private int port;
    private Server server;

    public StaticWebServer(int port) {
    	this(port, "src/test/resources/www");
    }
    
    public StaticWebServer(int port, String pathToFileRoot) {
        this.port = port;
        this.server = new Server(port);
        this.server.addHandler(new StaticWebServerHandler(pathToFileRoot));
    }

    public StaticWebServer start() throws Exception {
        this.server.start();
        logger.info("Jetty test server started on port " + this.port);
        return this;
    }

    public StaticWebServer stop() throws Exception {
        this.server.stop();
        logger.info("Jetty test server stopped");
        return this;
    }
    
}
