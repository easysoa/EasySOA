package com.openwide.easysoa.tree;

public interface UrlTreeModel {

    /**
     * 
     */
    public Object getRoot();

    /**
     * 
     */
    public Object getChild(Object parent, int index);


    /**
     * 
     */
    public int getChildCount(Object parent);


    /**
     * 
     */
    public boolean isLeaf(Object node);

    /**
     * 
     */
    public int getIndexOfChild(Object parent, Object child);

}