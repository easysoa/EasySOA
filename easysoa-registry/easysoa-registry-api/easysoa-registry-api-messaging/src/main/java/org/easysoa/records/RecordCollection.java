package org.easysoa.records;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RecordCollection {

    private Collection<ExchangeRecord> records;

    public RecordCollection() {
    	this.records = new ArrayList<ExchangeRecord>();
    }

    public RecordCollection(Collection<ExchangeRecord> records) {
        this.records = records;
    }

    @XmlElement(name="ExchangeRecord")
    @XmlElementWrapper(name="records")
    public Collection<ExchangeRecord> getRecords() {
        return records;
    }
}