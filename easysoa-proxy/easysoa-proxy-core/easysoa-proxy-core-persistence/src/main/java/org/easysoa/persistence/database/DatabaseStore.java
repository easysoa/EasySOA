/**
 * EasySOA Proxy
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

/**
 * 
 */
package org.easysoa.persistence.database;

import java.util.List;

import org.easysoa.persistence.StoreItf;
import org.easysoa.persistence.StoreResource;

/**
 * Not used at the moment.
 * 
 * @author jguillemotte
 *
 */
public class DatabaseStore implements StoreItf {

    @Override
    public void createStore(String storeName) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public String save(StoreResource resource) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StoreResource load(String resourceName, String store) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getStoreList(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<StoreResource> getResourceList(String store) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void createRecordingLock(String storeName) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeRecordingLock(String storeName) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean checkRecordingLock(String storeName) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void waitForRecordingLock(String storeName, long timeout) throws Exception {
        // TODO Auto-generated method stub
    }

}
