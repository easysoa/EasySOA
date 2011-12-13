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

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.Environment;


/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ApplicationManager {

    private static final Log log = LogFactory.getLog(ApplicationLoader.class);

    protected Map<String, Application> apps = new ConcurrentHashMap<String, Application>();

    public void addApplication(ApplicationDescriptor desc) throws Exception {
        Application app = load(desc);
        apps.put(desc.getName(), app);
    }

    public void removeApplication(String name) {
        Application app = apps.remove(name);
        if (app != null) {
            log.info("Unloading application: "+name);
            app.destroy();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Application> T getApplication(String name) {
        return (T)apps.get(name);
    }

    protected Application load(ApplicationDescriptor desc) throws Exception {
        log.info("Loading application: "+desc.getName());
        ApplicationFactory factory;
        if (desc.isIsolated()) {
            IsolatedClassLoader cl = createIsolatedClassLoader(desc);
            factory = (ApplicationFactory)cl.loadClass(desc.getFactory()).newInstance();
            Thread th = Thread.currentThread();
            ClassLoader oldcl = th.getContextClassLoader();
            th.setContextClassLoader(cl);
            try {
                return factory.createApplication(desc);
            } finally {
                th.setContextClassLoader(oldcl);
            }
        } else {
            factory = (ApplicationFactory)desc.getBundle().loadClass(desc.getFactory()).newInstance();
            return factory.createApplication(desc);
        }
    }

    private void buildClassPath(File dir, String nameSuffix, IsolatedClassLoader cl) throws Exception {
        String[] names = dir.list();
        if (names != null) {
            for (String name : names) {
                if (nameSuffix != null && !name.endsWith(nameSuffix)) {
                    continue;
                }
                cl.addURL(new File(dir, name).toURI().toURL());
            }
        }
    }

    private IsolatedClassLoader createIsolatedClassLoader(ApplicationDescriptor desc) throws Exception {
        IsolatedClassLoader cl = new IsolatedClassLoader(getClass().getClassLoader());
        List<String> classpath = desc.getClasspath();
        if (classpath != null) {
            File home = Environment.getDefault().getHome();
            for (String path : classpath) {
                int i = path.lastIndexOf("/*");
                if (i>-1) {
                    String folderPath = path.substring(0, i);
                    File dir = folderPath.startsWith("/") ? new File(folderPath) : new File(home, folderPath);
                    if (path.length() == i+2) {
                        buildClassPath(dir, null, cl);
                    } else {
                        buildClassPath(dir, path.substring(i+2), cl);
                    }
                } else {
                    File file = path.startsWith("/") ? new File(path) : new File(home, path);
                    cl.addURL(file.toURI().toURL());
                }
            }
        }
        return cl;
    }

}
