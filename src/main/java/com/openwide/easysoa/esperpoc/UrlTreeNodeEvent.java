package com.openwide.easysoa.esperpoc;

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
	
	/**
	 * 
	 * @return
	 */
	public float getRatioPartial(){
		return (float)(node.getPartialUrlcallCount()*100) / (float)(tree.getTotalUrlCount());
	}
	
	/**
	 * 
	 * @return
	 */
	public float getRatioComplete(){
		return (float)(node.getCompleteUrlcallCount()*100) / (float)(tree.getTotalUrlCount());	
	}

	/**
	 * 
	 * @return
	 */
	public float getRatioChilds(){
		float ratioChilds;
		if(node.getPartialUrlcallCount() > 0){
			ratioChilds = ((float)(node.getChildCount()*100) / (float)node.getPartialUrlcallCount());
		} else {
			ratioChilds = 0;
		}
		return ratioChilds;
	}

	/**
	 * 
	 * @return
	 */
	public int getRatioAllChilds(){
		return getChildsNumberRecursive(this.node);
	}
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	private int getChildsNumberRecursive(UrlTreeNode node){
		UrlTreeNode child;
		int nodeChildNumber = node.getChildCount();
		int childNumber = 0;
		if(nodeChildNumber > 0){
			for(int i=0; i < nodeChildNumber; i++){
				child = ((UrlTreeNode)(node.getChildAt(i)));
				childNumber = childNumber + getChildsNumberRecursive(child);
			}
		}
		return nodeChildNumber + childNumber;
	}	
	
}
