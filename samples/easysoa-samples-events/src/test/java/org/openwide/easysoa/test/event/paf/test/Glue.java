/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openwide.easysoa.test.event.paf.test;

import java.util.Hashtable;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * @author fntangke
 */

@Scope("Composite")
public class Glue implements PureAirFlowersService{
    
    private Hashtable<String, Integer> inventory =  new Hashtable<String, Integer>();
  
    @Reference
    TripPortType trip;

    @Override
    public int getOrdersNumber(String clientName) {

        if (this.getIventory().get(clientName)==null)
            return 0;
        else return this.getIventory().get(clientName);
        
    }

    @Override
    public int addOrder(Integer nbOrder, String ClientName) {
        
        @SuppressWarnings("unused")
        int i =45;
        
        if(nbOrder<10){
            @SuppressWarnings("unused")
            String result = this.trip.process("SEA", " Je voudrais un cadeau car jai bien acheté ",(double) 10);
        }
        return 911;
    }
    /**
     * @return the inventory
     */
    public Hashtable<String, Integer> getIventory() {
        return inventory;
    }
    
}
