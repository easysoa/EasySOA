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

package org.easysoa.records;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StoreCollection {

    private Collection<ExchangeRecordStore> stores;

    public StoreCollection() {
    	this.stores = new ArrayList<ExchangeRecordStore>();    	
    }

    public StoreCollection(Collection<ExchangeRecordStore> stores) {
        this.stores = stores;
    }

    @XmlElement(name="ExchangeRecordStore")
    @XmlElementWrapper(name="stores")
    public Collection<ExchangeRecordStore> getStores() {
        return stores;
    }

}