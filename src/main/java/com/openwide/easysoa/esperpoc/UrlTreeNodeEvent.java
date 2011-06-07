package com.openwide.easysoa.esperpoc;

public class UrlTreeNodeEvent {

	private UrlTreeNode node;
	private UrlTree tree;
	
	public UrlTreeNodeEvent(UrlTreeNode node, UrlTree tree){
		this.node = node;
		this.tree = tree;
	}
	
	public UrlTreeNode getEventSource(){
		return node;
	}
	
	public UrlTree getUrlTree(){
		return tree;
	}
	
	public float getRatioPartial(){
		return (float)(node.getPartialUrlcallCount()*100) / (float)(tree.getTotalUrlCount());
	}
	
	public float getRatioComplete(){
		return (float)(node.getCompleteUrlcallCount()*100) / (float)(tree.getTotalUrlCount());	
	}

	public float getRatioChilds(){
		// Etre plus precis il faudrait calculer sur n niveaux
		float ratioChilds;
		if(node.getPartialUrlcallCount() > 0){
			ratioChilds = ((float)(node.getChildCount()*100) / (float)node.getPartialUrlcallCount());
		} else {
			ratioChilds = 0;
		}
		return ratioChilds;
	}

	
	
}
