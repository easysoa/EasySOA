/**
 * EasySOA - Nuxeo FraSCAti
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
package org.nuxeo.frascati.factory;

import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.nuxeo.runtime.api.Framework;

/**
 * The ClassLoaderSingleton allow to keep a reference to the IsolatedClassLoader
 * used to instantiate FraSCAti
 */
public final class ClassLoaderSingleton
{

    private static ClassLoader classLoader;

    /**
     * Defines the ClassLoader instance. If the ClassLoaderSingleton is not null
     * and if the FraSCAtiServiceItf instance has been registered by Nuxeo, then
     * this method do nothing
     * 
     * @param classLoader
     *            the ClassLoader object to set
     * @return the current ClassLoader
     */
    public static final ClassLoader setClassLoader(ClassLoader classLoader)
    {

        if (ClassLoaderSingleton.classLoader == null
                || Framework.getLocalService(FraSCAtiServiceItf.class) == null)
        {
            ClassLoaderSingleton.classLoader = classLoader;
        }
        return ClassLoaderSingleton.classLoader;
    }

    /**
     * Returns the current ClassLoader instance
     * 
     * @return the current ClassLoader instance
     */
    public static final ClassLoader classLoader()
    {
        return classLoader;
    }

}
