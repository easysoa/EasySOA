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
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Generic component to integrate a bridged application as a set of Nuxeo services.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ApplicationComponent extends DefaultComponent {

    protected String name;

    public String getName() {
        return name;
    }

    @Override
    public void activate(ComponentContext context) throws Exception {
        this.name = (String)context.getPropertyValue("name");
        if (this.name == null) {
            throw new IllegalStateException("No application name was specified in application component descriptor. Bundle: "+context.getRuntimeContext().getBundle());
        }
    }

    @Override
    public <T> T getAdapter(Class<T> adapter) {
        Application app = ApplicationLoader.getApplication(name);
        return app != null ? app.getService(adapter): null;
    }

}
