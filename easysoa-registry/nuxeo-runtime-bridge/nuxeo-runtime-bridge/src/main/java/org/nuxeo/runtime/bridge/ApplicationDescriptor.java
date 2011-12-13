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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;
import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
@XObject("application")
public class ApplicationDescriptor {

    @XNode("@name")
    protected String name;

    @XNode("@factory")
    protected String factory;

    @XNode("@isolated")
    protected boolean isolated;

    /**
     * Used only if isolated is true to build the isolated class loader.
     */
    @XNodeList(value="classpath/entry", type=ArrayList.class, componentType=String.class, nullByDefault = true)
    protected List<String> classpath;

    @XNodeMap(value="properties/property", key="@name", type=HashMap.class, componentType=String.class, nullByDefault = true)
    protected Map<String,String> properties;

    private Bundle bundle;

    public String getName() {
        return name;
    }

    public String getFactory() {
        return factory;
    }

    public boolean isIsolated() {
        return isolated;
    }

    public List<String> getClasspath() {
        return classpath;
    }

    public Map<String,String> getProperties() {
        return properties;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public void setIsolated(boolean isolated) {
        this.isolated = isolated;
    }

    public void setResources(Map<String,String> properties) {
        this.properties = properties;
    }

    public void setClasspath(List<String> classpath) {
        this.classpath = classpath;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
