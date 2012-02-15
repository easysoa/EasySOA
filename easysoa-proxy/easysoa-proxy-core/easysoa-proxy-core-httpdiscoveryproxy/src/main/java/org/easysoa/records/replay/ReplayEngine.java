package org.easysoa.records.replay;

import java.util.Map;

import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.RecordCollection;
import org.easysoa.records.StoreCollection;

import com.openwide.easysoa.message.OutMessage;

public interface ReplayEngine {

    /**
     * Get the exchange record corresponding to the ID and stored in the specified store 
     * @param exchangeRecordStoreName The store where the record is stored
     * @param exchangeID The exchange ID to get
     * @return An <code>ExchangeRecord</code>
     * @throws Exception If a problem occurs
     */
    public ExchangeRecord getExchangeRecord(String exchangeRecordStoreName, String exchangeID) throws Exception;    
    
    /**
     * Get the exchange records for a store
     * @param exchangeRecordStoreName The store name
     * @return A <code>RecordCollection</code> containing all the exchange record stored in the store
     * @throws Exception If a problem occurs 
     */
    public RecordCollection getExchangeRecordlist(String exchangeRecordStoreName) throws Exception;    

    /**
     * Get the list of exchange record stores
     * @return A <code>StoreCollection</code>
     * @throws Exception If a problem occurs
     */
    public StoreCollection getExchangeRecordStorelist() throws Exception;    
    
    /**
     * Replay an exchange record or all the exchange records stored in a specified store without any modifications
     * @param exchangeRecordStoreName The store name where the exchange record is stored
     * @param exchangeRecordId The ID of the exchange record to replay, if null or empty, all the exchange records are replayed
     * @return The <code>Map</code> containing the record id as key and the associated <code>OutMessage</code> as response
     */
    public Map<String,OutMessage> replay(String exchangeRecordStoreName, String exchangeRecordId) throws Exception;


    
}