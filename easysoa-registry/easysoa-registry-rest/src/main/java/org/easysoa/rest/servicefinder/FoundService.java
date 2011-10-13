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
 * Contact : easysoa-dev@groups.google.com
 */

package org.easysoa.rest.servicefinder;

public class FoundService {

    private String name;
    
    private String url;
    
    private String applicationName;

    public FoundService(String name, String url, String applicationName) {
        this(name, url);
        this.applicationName = applicationName;
    }
    
    public FoundService(String name, String url) {
        this.name = name;
        this.url = url;
    }
    
    public String getName() {
        return name;
    }
    
    public String getURL() {
        return url;
    }
    
    public String getApplicationName() {
        return applicationName;
    }

}
