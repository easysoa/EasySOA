/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.core.event.implementations;

/**
 *
 * @author fntangke
 */
public interface ActionInterface {
        /* Return true if your account is not negative*/
    public boolean accountState();
    
    /* Return all your account's money */
    public int getAll();
    
    /* Add some money to your bank account */
    public boolean addMoney(int money);
        
    void print(String msg);
    
}
