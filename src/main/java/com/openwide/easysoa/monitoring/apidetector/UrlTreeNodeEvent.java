package com.openwide.easysoa.monitoring.apidetector;

public class UrlTreeNodeEvent {

	private UrlTreeNode node;
	private UrlTree tree;
	
	/**
	 * 
	 * @param node
	 * @param tree
	 */
	public UrlTreeNodeEvent(UrlTreeNode node, UrlTree tree){
		this.node = node;
		this.tree = tree;
	}
	
	/**
	 * 
	 * @return
	 */
	public UrlTreeNode getEventSource(){
		return node;
	}
	
	/**
	 * 
	 * @return
	 */
	public UrlTree getUrlTree(){
		return tree;
	}
	
}
