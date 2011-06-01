package com.openwide.easysoa.esperpoc;

public class UrlTreeNodeEvent {

	private UrlTreeNode node;
	
	public UrlTreeNodeEvent(UrlTreeNode node){
		this.node = node;
	}
	
	public UrlTreeNode getEventSource(){
		return node;
	}
	
}
