/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.core.event.implementations;

/**
 *
 * @author fntangke
 */
public class Distributeur implements ActionInterface {

    
    private int accountMoney = 100;
    
    @Override
    public boolean accountState() {
        System.out.println("\nDistributeur: Je verifie votre solde");
        return (this.accountMoney<0);
    }

    @Override
    public int getAll() {
        
        System.out.println("\nDistributeur: Je vous rend tout votre argent");
        int a = this.accountMoney;
        this.accountMoney=0;
        return a;
    }

    @Override
    public boolean addMoney(int money) {
        System.out.println("\nDistributeur: Je credite votre compte de ".concat(String.valueOf(money)));
        this.accountMoney+=money;
        System.out.println("\nDistributeur: votre solde est dorenavant de  ".concat(String.valueOf(this.accountMoney)));
        return true;
    }

    @Override
    public void print(String msg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
