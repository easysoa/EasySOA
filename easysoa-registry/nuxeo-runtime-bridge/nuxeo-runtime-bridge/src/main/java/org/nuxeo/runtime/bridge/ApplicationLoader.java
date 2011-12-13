/*
 * (C) Copyright 2006-2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.runtime.bridge;

import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ApplicationLoader extends DefaultComponent {

    public static final String XP_APPS = "applications";

    private static volatile ApplicationManager mgr;

    public static ApplicationManager getApplicationManager() {
        return mgr;
    }

    public static Application getApplication(String name) {
        ApplicationManager _mgr = mgr;
        return _mgr != null ? _mgr.getApplication(name) : null;
    }


    @Override
    public void activate(ComponentContext context) throws Exception {
        mgr = new ApplicationManager();
    }

    @Override
    public void deactivate(ComponentContext context) throws Exception {
        mgr = null;
    }

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception {
        if (XP_APPS.equals(extensionPoint)) {
            ApplicationDescriptor desc = (ApplicationDescriptor)contribution;
            desc.setBundle(contributor.getContext().getBundle());
            mgr.addApplication(desc);
        }
    }

    @Override
    public void unregisterContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor)
            throws Exception {
        if (XP_APPS.equals(extensionPoint)) {
            ApplicationDescriptor desc = (ApplicationDescriptor)contribution;
            mgr.removeApplication(desc.getName());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (ApplicationManager.class == adapter) {
            return (T)mgr;
        }
        return null;
    }
}
