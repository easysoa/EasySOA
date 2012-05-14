/**
 * EasySOA - FraSCAti
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
package org.nuxeo.frascati;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.easysoa.sca.frascati.RemoteFraSCAtiServiceProvider;
import org.nuxeo.runtime.bridge.Application;


/**
 * NuxeoFraSCAti is a RemoteFraSCAtiServiceProvider in a Nuxeo Context
 */
public class NuxeoFraSCAti extends RemoteFraSCAtiServiceProvider implements Application
{
    Logger log = Logger.getLogger(NuxeoFraSCAti.class.getCanonicalName());
    private static File EMPTY_LIB_DIRECTORY; 
    static
    {
        try
        {
            File tmpDir = File.createTempFile("nuxeo_frascati", ".tmp");
            tmpDir.delete();
            tmpDir.getParentFile();
            EMPTY_LIB_DIRECTORY = new File(tmpDir.getAbsolutePath() + 
                    File.separator + "nuxeo_frascati");
            EMPTY_LIB_DIRECTORY.mkdir();
        } catch (IOException e)
        {
            e.printStackTrace();
        }        
    }
    
    /**
     * Constructor
     * @throws Exception 
     */
    public NuxeoFraSCAti() throws Exception
    {
        super(EMPTY_LIB_DIRECTORY);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.nuxeo.runtime.bridge.Application#getService(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> type)
    {
        if (type.isAssignableFrom(getClass()))
        {
            return (T) this;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.nuxeo.runtime.bridge.Application#destroy()
     */
    public void destroy()
    {
        try
        {
            super.stopFraSCAtiService();
        } catch (Exception e)
        {
            log.log(Level.WARNING,e.getMessage(),e);
        }
    }
}
