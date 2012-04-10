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

package org.easysoa;

import javax.security.auth.login.LoginContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.demo.UserInit;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

/**
 * Component loaded on Nuxeo startup
 *
 * @author mkalam-alami
 *
 */
public class EasySOAInitComponent implements BundleActivator, FrameworkListener {

    private static Log log = LogFactory.getLog(EasySOAInitComponent.class);

    protected void doInit() throws Exception {

        LoginContext lc = Framework.login();
        TransactionHelper.startTransaction();
        try {
            RepositoryManager repoService = Framework.getLocalService(RepositoryManager.class);

            Repository defaultRepository = repoService.getDefaultRepository();

            // Init default domain
            try {
                new DomainInit(defaultRepository.getName()).runUnrestricted();
            } catch (Exception e) {
                log.warn("Failed to access default repository for initialization: " + e.getMessage());
                TransactionHelper.setTransactionRollbackOnly();
            }

            try {
                new UserInit(defaultRepository.getName()).runUnrestricted(); // Demo: Init users
            } catch (Exception e) {
                log.warn("Failed to initialize groups: " + e.getMessage());
                TransactionHelper.setTransactionRollbackOnly();
            }
        }
        finally {
            TransactionHelper.commitOrRollbackTransaction();
            lc.logout();
        }
    }

    @Override
    public void frameworkEvent(FrameworkEvent event) {

        //if (event.getType() == FrameworkEvent.STARTED) {
        //    try {
        //        doInit();
        //    } catch (Exception e) {
        //        log.error("Unable to start EasySOA init", e);
        //    }
        //}
    }

    @Override
    public void start(BundleContext context) throws Exception {
        context.addFrameworkListener(this);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        context.removeFrameworkListener(this);
    }

}