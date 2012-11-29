package org.easysoa.proxy.core.event.implementations;

import java.util.ArrayList;
import java.util.List;

//import org.easysoa.proxy.core.event.generated1.PureAirFlowersService;
import org.easysoa.samples.paf.PureAirFlowersService;
import org.osoa.sca.annotations.Reference;

/** A print service client. */
public class Client implements Runnable {
    
    @Reference 
    private ActionInterface printService;
    @Reference 
    private ActionInterface distrib;
    @Reference
    private PureAirFlowersService tw;
    
    /* how much money the client get on him  */
    private int portefeuille;
    
    
    /** Default constructor. */
    public Client() {
        this.portefeuille = 30;
        System.out.println("CLIENT created.");
    }
    
    /** Run the client. */
    @Override
    public void run() {
        this.accountstate();
        this.distrib.accountState();
        this.giveAll2theBank();
        this.accountstate();
       
      // this.addMoneyinBankAccount(28);
       // printService.print("Hello World!");
    }
    
    public void accountstate(){
        System.out.println("Client: je possede ".concat(String.valueOf(this.portefeuille)));
    }

    
     public void give2theBank(int cash){
        if(this.portefeuille<0){
            System.out.println("Client: Je n'ai pas assez d'argent");
        }
        else {
            this.distrib.addMoney(cash);
            this.portefeuille-=cash;
        }
    }
    
    public void giveAll2theBank(){
        
        this.distrib.addMoney(portefeuille);
        this.portefeuille=0;
    }
}