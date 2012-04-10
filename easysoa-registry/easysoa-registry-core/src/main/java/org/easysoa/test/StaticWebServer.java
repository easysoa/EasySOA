/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

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
