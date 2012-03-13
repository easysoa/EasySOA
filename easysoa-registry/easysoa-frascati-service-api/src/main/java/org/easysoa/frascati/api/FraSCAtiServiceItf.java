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
package org.easysoa.frascati.api;

import java.net.URL;
import java.util.List;

import org.easysoa.frascati.FraSCAtiServiceException;
import org.eclipse.stp.sca.Composite;
import org.osoa.sca.annotations.Service;

@Service
public interface FraSCAtiServiceItf
{

    //Define FraSCAti's ProcessingModes as integers
    public static final int parse = 0;
    public static final int check = 1;
    public static final int generate = 2;
    public static final int compile = 3;
    public static final int instantiate = 4;
    public static final int complete = 5;
    public static final int start = 6;
    public static final int all = 7;

    /**
     * Return the Composite which name is passed on as a parameter
     * 
     * @param compositeName
     *            the name of the composite
     * @return the composite
     * @throws FraSCAtiServiceException
     *             if the composite doesn't exist
     */
    Composite getComposite(String compositeName)
            throws FraSCAtiServiceException;

    /**
     * Process a contribution ZIP archive.
     * 
     * @param contribution
     *            name of the contribution file to load.
     * @param processingMode
     *            the ProcessingModeProxy constant to use to build a FraSCAti
     *            ProcessingContext
     * @param urls
     *            a set of URLs to build the FraSCAti ProcessingContext
     * @return an array of loaded components' name.
     * @throws FraSCAtiServiceException
     *             if no component can be found in the contribution file
     */
    String[] processContribution(String contribution, int processingMode,
            URL... urls) throws FraSCAtiServiceException;

    /**
     * Process a contribution ZIP archive, using the ProcessingMode.all mode
     * 
     * @param contribution
     *            name of the contribution file to load.
     * @return an array of FraSCAtiCompositeItf embedded loaded composites.
     * @throws FraSCAtiServiceException
     *             if no component can be found in the contribution file
     */
    String[] processContribution(String contribution)
            throws FraSCAtiServiceException;

    /**
     * Loads an SCA composite and create the associate FraSCAti composite
     * instance.
     * 
     * @param composite
     *            name of the composite file to load.
     * @param processingMode
     *            the ProcessingModeProxy constant to use to build a FraSCAti
     *            ProcessingContext
     * @param urls
     *            a set of URLs to build the FraSCAti ProcessingContext
     * @return the FraSCAtiCompositeItf embedded resulting FraSCAti composite
     *         instance.
     * @throws FraSCAtiServiceException
     *             if the composite can not be loaded
     */
    String processComposite(String composite, int processingMode, URL... urls)
            throws FraSCAtiServiceException;

    /**
     * Load an SCA composite which path is passed on as a parameter
     * 
     * @param composite
     *            name of the composite file to load
     * @return the name of the loaded component.
     * @throws FraSCAtiServiceException
     *             if the component cannot be loaded
     */
    String processComposite(String composite) throws FraSCAtiServiceException;

    /**
     * Return the state of the component which name is passed on as a
     * parameter 
     * 
     * @param componentName
     *            the name of the component to start
     * @return
     *          "STARTED" if the component is started
     *          "STOPPED" if the component is stopped
     */
    String state(String compositeName);
    
    /**
     * Start the component which name is passed on as parameter
     * 
     * @param componentName
     *            the name of the component to start
     */
    void start(String compositeName);

    /**
     * Stop the component which name is passed on as parameter
     * 
     * @param componentName
     *            the name of the component to stop
     */
    void stop(String compositeName);

    /**
     * Remove from FraSCAti the component which name is passed on as parameter
     * 
     * @param componentName
     *            the name of the component to remove
     * @throws FraSCAtiServiceException
     *             if the component doesn't exist
     */
    void remove(String compositeName) 
            throws FraSCAtiServiceException;

    /**
     * Return the service associated to the component which name is passed on as
     * parameter, with the name and class type also passed on as parameter
     * 
     * @param compositeName
     *            the name of the composite to look the service in
     * @param serviceName
     *            the service name
     * @param serviceClass
     *            the service class
     * @return the service instance
     * @throws FraSCAtiServiceException
     *             if the service has not been found
     */
    <T> T getService(String compositeName, 
            String serviceName, Class<T> serviceClass) 
                    throws FraSCAtiServiceException;


    /**
     * Return the number of errors which occurred during 
     * the processing process
     * 
     * @return 
     *          the number of errors
     */
    int getErrors();

    /**
     * Return the number of warnings which occurred during 
     * the processing process
     * 
     * @return 
     *          the number of warnings
     */
    int getWarnings();
    
    /**
     * Returns the list of warning messages registered during 
     * the processing process
     * 
     * @return the list of warning messages
     */
    List<String> getWarningMessages();

    /**
     * Returns the list of error messages registered during 
     * the processing process
     * 
     * @return the list of error messages
     */
    List<String> getErrorMessages();

    /**
     * Define the ScaImporterRecipientItf object for processing process
     * If a not null ScaImporterRecipientItf is defined, sca import at runtime 
     * is activated
     * 
     * @param recipient
     *          the ScaImporterRecipientItf object to use
     */
    void setScaImporterRecipient(ScaImporterRecipientItf recipient);
    
}
