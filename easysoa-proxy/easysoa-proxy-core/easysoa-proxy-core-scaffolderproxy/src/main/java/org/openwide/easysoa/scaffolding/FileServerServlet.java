/**
 * EasySOA Proxy
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
 * Contact : easysoa-dev@groups.google.com
 */

package org.openwide.easysoa.scaffolding;

import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.osoa.sca.annotations.Property;

public class FileServerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(FileServerServlet.class.getClass());   

    /**
     * A configurable property 'name'.
     */
    @Property(name = "webfolder")
    private String webFolder;
    
    @Override
    public final void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        
        logger.info("Fetching: "+request.getPathInfo());
        
        if (!webFolder.endsWith("/")) {
            webFolder += '/';
        }
        
        // Fetch file contents
        FileInputStream is = new FileInputStream(webFolder + request.getPathInfo());
        ServletOutputStream os = response.getOutputStream();
        
        // Write input to ouput
        int c;
        while ((c = is.read()) != -1) {
            os.write(c);
        }
        
        // Close
        is.close();
        os.close();
        
    }

}