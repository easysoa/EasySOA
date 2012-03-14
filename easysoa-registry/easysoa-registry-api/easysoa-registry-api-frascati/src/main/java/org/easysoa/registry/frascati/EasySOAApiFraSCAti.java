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

package org.easysoa.registry.frascati;

import java.io.File;

import org.easysoa.sca.IScaRuntimeImporter;
import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.frascati.ApiFraSCAtiScaImporter;
import org.easysoa.sca.frascati.ApiRuntimeFraSCAtiScaImporter;
import org.easysoa.sca.visitors.RemoteBindingVisitorFactory;

/**
 * 
 * @author mkalam-alami, jguillemotte
 * 
 */
public class EasySOAApiFraSCAti extends FraSCAtiRegistryServiceBase
{
    /**
     * the EasySOAApiFraSCAti singleton
     */
    private static EasySOAApiFraSCAti instance = null;

    /**
     * Return the singleton instance of EasySOAApiFraSCAti
     * 
     * @return the EasySOAApiFraSCAti singleton
     */
    public static final EasySOAApiFraSCAti getInstance()
    {
        if (instance == null)
        {
            instance = new EasySOAApiFraSCAti();
        }
        return instance;
    }

    /**
     * Hidden constructor Use {@link EasySOAApiFraSCAti#getInstance()} static
     * method instead
     */
    protected EasySOAApiFraSCAti()
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.registry.frascati.FraSCAtiRegistryServiceItf#newRuntimeScaImporter()
     */
    public IScaRuntimeImporter newRuntimeScaImporter() throws Exception
    {
        return newRemoteRuntimeScaImporter();
    }

    /**
     * @param compositeFile
     * @return
     * @throws Exception
     */
    public IScaImporter newScaImporter(File compositeFile) throws Exception
    {
        return newRemoteScaImporter(compositeFile);
    }

    /**
     * @return
     * @throws Exception
     */
    public IScaRuntimeImporter newRemoteRuntimeScaImporter() throws Exception
    {
        RemoteBindingVisitorFactory apiBindingVisitorFactory = new RemoteBindingVisitorFactory();

        ApiRuntimeFraSCAtiScaImporter runtimeImporter = new ApiRuntimeFraSCAtiScaImporter(
                apiBindingVisitorFactory, null, this);

        return runtimeImporter;
    }

    /**
     * @param compositeFile
     * @return
     * @throws Exception
     */
    public IScaImporter newRemoteScaImporter(File compositeFile)
            throws Exception
    {
        RemoteBindingVisitorFactory apiBindingVisitorFactory = new RemoteBindingVisitorFactory();
        ApiFraSCAtiScaImporter apiFraSCAtiScaImporter = new ApiFraSCAtiScaImporter(
                apiBindingVisitorFactory, compositeFile, this);
        return apiFraSCAtiScaImporter;
    }

}