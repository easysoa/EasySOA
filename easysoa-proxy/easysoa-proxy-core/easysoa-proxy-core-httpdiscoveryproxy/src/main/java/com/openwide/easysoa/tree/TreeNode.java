package com.openwide.easysoa.tree;

import java.util.Enumeration;

public interface TreeNode {
    /**
     *  
     */
    TreeNode getChildAt(int childIndex);

    /**
     * 
     */
    int getChildCount();

    /**
     * 
     */
    TreeNode getParent();

    /**
     * 
     */
    int getIndex(TreeNode node);

    /**
     * 
     */
    boolean getAllowsChildren();

    /**
     * 
     */
    boolean isLeaf();

    /**
     * 
     */
    Enumeration<?> children();
}