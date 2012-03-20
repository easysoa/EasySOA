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
package org.easysoa.sca.frascati;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;

/**
 * @author jguillemotte
 * 
 */
public class RemoteFraSCAtiServiceProvider implements
        FraSCAtiServiceProviderItf
{
    
    public static final String REMOTE_FRASCATI_LIBRARIES_BASEDIR = 
            "org.easysoa.remote.frascati.libraries.basedir";
    
    private static Log log = LogFactory.getLog(RemoteFraSCAtiServiceProvider.class);
    private FraSCAtiServiceItf frascatiService;
    private UpdatableURLClassLoader icl;
    
    
    /**
     * Constructor 
     * 
     * launch a new FraSCAti instance which is not a Nuxeo application
     * 
     * @param librariesDirectory
     *          the directory where the FraSCAti's libraries are stored
     *          
     * @throws Exception
     */
    public RemoteFraSCAtiServiceProvider(File librariesDirectory) throws Exception
    {
        if(librariesDirectory == null)
        {
            String librariesDirectoryProp = System.getProperty(
                    REMOTE_FRASCATI_LIBRARIES_BASEDIR);
            
            if(librariesDirectoryProp == null 
                    || !(librariesDirectory = new File(
                            librariesDirectoryProp)).exists())
            {
                throw new InstantiationException("Enable to instantiate a new" +
                " remote FraSCAti instance : no libraries directory found" );
            }
        }
        File[] libraries = librariesDirectory.listFiles(
                new FilenameFilter(){
                    @Override
                    public boolean accept(File arg0, String arg1)
                    {
                        if(arg1.endsWith(".jar"))
                        {
                            return true;
                        }
                        return false;
                    }
                    
                });
        
        //Define a new URLClassLoader using a parent which allow to find shared 
        //classes
        icl = new UpdatableURLClassLoader(new URL[0],
                getClass().getClassLoader());
        
        for(File library : libraries)
        {
           icl.addURL(library.toURI().toURL()); 
        }
        
        Class<?> frascatiClass = icl.loadClass("org.ow2.frascati.FraSCAti");
        
        Object frascati = frascatiClass.getDeclaredMethod("newFraSCAti",
                new Class<?>[]{ClassLoader.class}).invoke(null,
                        new Object[]{icl});
        
        Class<?> managerClass = icl.loadClass(
                "org.ow2.frascati.assembly.factory.api.CompositeManager");

        Class<?> componentClass = icl.loadClass(
                "org.objectweb.fractal.api.Component");
        
        Object manager = frascatiClass.getDeclaredMethod(
                "getCompositeManager").invoke(frascati);
        
        Object component = managerClass.getDeclaredMethod(
                "getTopLevelDomainComposite").invoke(manager);
        
        Object factory = getComponent(component,
                "org.ow2.frascati.FraSCAti/assembly-factory");
       
        frascatiService = (FraSCAtiServiceItf)
                frascatiClass.getDeclaredMethod("getService",new Class<?>[]{
                        componentClass, 
                        String.class,
                        Class.class})
                .invoke(frascati, new Object[]{
                        factory,
                        "easysoa-frascati-service", 
                        FraSCAtiServiceItf.class});
    }
    
    
    /**
     * Find a Fractal Component using a parent of it and its path
     * 
     * @param currentComponent
     *          a parent of the search component
     * @param componentPath
     *          the path of the component
     * @return
     *          the component if it has been found. Null otherwise
     * @throws Exception
     */
    private Object getComponent(Object currentComponent,
            String componentPath) throws Exception
    {
        String[] componentPathElements = componentPath.split("/");
        String lookFor = componentPathElements[0];
        String next = null;
        
        if(componentPathElements.length>1)
        {
            int n = 1;
            StringBuilder nextSB = new StringBuilder();
            for(;n<componentPathElements.length;n++)
            {
                nextSB.append(componentPathElements[n]);
                if(n<componentPathElements.length - 1)
                {
                    nextSB.append("/");
                }
            }
            next = nextSB.toString();
        }  
       
       Class<?> componentClass = icl.loadClass(
               "org.objectweb.fractal.api.Component");
       
       Class<?> contentControllerClass = icl.loadClass(
               "org.objectweb.fractal.api.control.ContentController");
       
       Class<?> nameControllerClass = icl.loadClass(
               "org.objectweb.fractal.api.control.NameController");
      
       Object contentController = componentClass.getDeclaredMethod(
               "getFcInterface",new Class<?>[]{String.class}).invoke(
                       currentComponent,"content-controller");
     
       Object[] subComponents = (Object[]) 
               contentControllerClass.getDeclaredMethod(
                       "getFcSubComponents",(Class<?>[])null).invoke(
                               contentController,(Object[])null);
       
        if(subComponents == null)
        {
            return null;
        }
        for(Object o : subComponents)
        {
            Object nameController = componentClass.getDeclaredMethod(
               "getFcInterface",new Class<?>[]{String.class}).invoke(
                currentComponent,"name-controller");
            
            
            String name = (String) nameControllerClass.getDeclaredMethod("getFcName",
                   (Class<?>[])null).invoke(nameController,(Object[])null);
            
            if(lookFor.equals(name))
            {
                if(next == null || next.length() ==0)
                {
                    return o;
                } else 
                {
                    return getComponent(o,next);
                }
            }
        }
        return null;
    }
    
    public FraSCAtiServiceItf getFraSCAtiService() {

        return frascatiService;
    }

    
    /**
     * An URLClassLoader which allow to use the addURL method
     */
    private class UpdatableURLClassLoader extends URLClassLoader
    {
        /**
         * @param urls
         * @param parent
         * @param factory
         */
        public UpdatableURLClassLoader(URL[] urls, ClassLoader parent,
                URLStreamHandlerFactory factory)
        {
            super(urls, parent, factory);
        }

        /**
         * @param urls
         * @param parent
         */
        public UpdatableURLClassLoader(URL[] urls, ClassLoader parent)
        {
            super(urls, parent);
        }

        /**
         * @param urls
         */
        public UpdatableURLClassLoader(URL[] urls)
        {
            super(urls);
        }
        
        @Override
        public void addURL(URL url)
        {
            super.addURL(url);
        }
    }
    

}
