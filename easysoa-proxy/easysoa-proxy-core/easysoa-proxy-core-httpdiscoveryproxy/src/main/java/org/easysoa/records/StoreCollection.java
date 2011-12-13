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