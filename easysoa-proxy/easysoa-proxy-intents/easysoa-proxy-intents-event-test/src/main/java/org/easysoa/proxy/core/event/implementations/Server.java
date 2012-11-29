package org.easysoa.proxy.core.event.implementations;
/** The print service implementation. */
public class Server implements ActionInterface {    
    private String header = "->";
    private int count = 1;
    
    /** Default constructor. */
    public Server() {
        System.out.println("SERVER created.");
    }
    
    /** PrintService implementation. */
    public void print(final String msg) {
        System.out.println("SERVER: begin printing...");
        for (int i = 0; i < count; ++i) {
            System.out.println(header + msg);
        }
        System.out.println("SERVER: print done.");
    }

    /** Set the Header property. */
    public void setHeader(final String header) {
        System.out.println("SERVER: setting header to '" + header + "'.");
        this.header = header;
    }
    
    /** Set the count property. */
    public void setCount(final int count) {
        System.out.println("SERVER: setting count to '" + count + "'.");
        this.count = count;
    }

    @Override
    public boolean accountState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAll() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addMoney(int money) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}