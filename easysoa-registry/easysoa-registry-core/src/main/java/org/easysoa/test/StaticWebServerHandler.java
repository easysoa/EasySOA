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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.handler.AbstractHandler;


public class StaticWebServerHandler extends AbstractHandler {
    
    private String pathToFileRoot;
    
    public StaticWebServerHandler(String pathToFileRoot) {
        this.pathToFileRoot = pathToFileRoot;
        if (pathToFileRoot.endsWith(File.separator)) {
            this.pathToFileRoot = pathToFileRoot.substring(0, pathToFileRoot.length() - 1);
        }
    }

    @Override
    public void handle(String target, HttpServletRequest request,
            HttpServletResponse response, int dispatch) throws IOException,
            ServletException {
        
        // Root page
        if (target.equals(File.separator)) {
            response.setHeader("Content-Type", "text/html");
            response.getWriter().write("<h1>Jetty test server</h1><pre>Currently mounted on: " + System.getProperty("user.dir") + File.separator + pathToFileRoot + "</pre>");
        }
        
        // Statically served file
        else {
            
            File f = new File(pathToFileRoot + target);
            
            if (!f.exists()) {
                response.setStatus(404);
            }
            else {
                response.setHeader("Content-Type", getMimeType(target));
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.getWriter().write(line);
                }
                response.getWriter().flush();
            }
        
        }
        
        response.getWriter().close();
    }

    public static String getMimeType(String fileName) throws java.io.IOException {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(fileName);
        return type;
    }

}
