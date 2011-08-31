/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.openwide.easysoa.cxf;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.BusApplicationContext;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.buslifecycle.BusLifeCycleListener;
import org.apache.cxf.buslifecycle.BusLifeCycleManager;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.configuration.Configurer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;


/**
 * HACK
 * Inspired by SpringBusFactory, with an added an anti-deadlock wait
 * to prevent the deadlock described in test TODO
 * 
 * @author jguillemotte
 *
 */
public class EasySOABusFactory extends SpringBusFactory {
    
    private static final Logger LOG = LogUtils.getL7dLogger(EasySOABusFactory.class);
    
    private final ApplicationContext context;

    public EasySOABusFactory() {
        this.context = null;
    }

    public EasySOABusFactory(ApplicationContext context) {
        this.context = context;
    }
        
    /**
     * Does the same thing as SpringBusFactory.finishCreatingBus(),
     * but calls a custom myPossiblySetDefaultBus() that doesn't synchronize on threadbusses
     * @param bac
     * @return
     */
    protected Bus myFinishCreatingBus(BusApplicationContext bac) {
        final Bus bus = (Bus)bac.getBean(Bus.DEFAULT_BUS_ID);

        bus.setExtension(bac, BusApplicationContext.class);

    	myPossiblySetDefaultBus(bus);
        
        initializeBus(bus);        
        
        BusLifeCycleManager lm = bus.getExtension(BusLifeCycleManager.class);
        if (null != lm) {
            lm.registerLifeCycleListener(new BusApplicationContextLifeCycleListener(bac));
        }
        
        return bus;
    }  

    /**
     * Does the same thing as SpringBusFactory.finishCreatingBus(),but doesn't synchronize on threadbusses
     */
    public static synchronized boolean myPossiblySetDefaultBus(Bus bus) {
        //XXX deadlock hack : no synchronized on threadBusses
        //TODO LATER better : add an additional custom lock...
        // TODO tell CXF guys about it
        //synchronized (threadBusses) {
        if (threadBusses.get(Thread.currentThread()) == null) {
            threadBusses.put(Thread.currentThread(), bus);
        }
        //}
        if (defaultBus == null) {
            defaultBus = bus;
            return true;
        }
        return false;
    }

    /**
     * Does the same thing as in SpringBusFactory.finishCreatingBus(),
     * but calls a custom myFinishCreatingBus() that adds an anti-deadlock wait
     * @param bac
     * @return
     */
    public Bus createBus(String cfgFiles[], boolean includeDefaults) {
        try {
            String userCfgFile = System.getProperty(Configurer.USER_CFG_FILE_PROPERTY_NAME);
            String sysCfgFileUrl = System.getProperty(Configurer.USER_CFG_FILE_PROPERTY_URL);
            Resource r = BusApplicationContext.findResource(Configurer.DEFAULT_USER_CFG_FILE);
            if (context == null && userCfgFile == null && cfgFiles == null && sysCfgFileUrl == null 
                && (r == null || !r.exists()) && includeDefaults) {
                return new org.apache.cxf.bus.CXFBusFactory().createBus();
            }
            return myFinishCreatingBus(createApplicationContext(cfgFiles, includeDefaults));
        } catch (BeansException ex) {
            LogUtils.log(LOG, Level.WARNING, "APP_CONTEXT_CREATION_FAILED_MSG", ex, (Object[])null);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Private in extended class, so has to be rewritten
     * @author jguillemotte
     *
     */
    protected BusApplicationContext createApplicationContext(String cfgFiles[], boolean includeDefaults) {
        try {      
            return new BusApplicationContext(cfgFiles, includeDefaults, context);
        } catch (BeansException ex) {
            LogUtils.log(LOG, Level.WARNING, "INITIAL_APP_CONTEXT_CREATION_FAILED_MSG", ex, (Object[])null);
            ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            if (contextLoader != BusApplicationContext.class.getClassLoader()) {
                Thread.currentThread().setContextClassLoader(
                    BusApplicationContext.class.getClassLoader());
                try {
                    return new BusApplicationContext(cfgFiles, includeDefaults, context);        
                } finally {
                    Thread.currentThread().setContextClassLoader(contextLoader);
                }
            } else {
                throw ex;
            }
        }
    }

    /**
     * Package private in extended class, so has to be rewritten
     * @author jguillemotte
     *
     */
    protected static class BusApplicationContextLifeCycleListener implements BusLifeCycleListener {
        private BusApplicationContext bac;

        BusApplicationContextLifeCycleListener(BusApplicationContext b) {
            bac = b;
        }

        public void initComplete() {
        }

        public void preShutdown() {
        }

        public void postShutdown() {
            bac.close();
        }
        
    }
}
