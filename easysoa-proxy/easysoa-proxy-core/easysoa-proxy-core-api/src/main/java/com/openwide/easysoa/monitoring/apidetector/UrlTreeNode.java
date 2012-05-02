/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package com.openwide.easysoa.monitoring.apidetector;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;

@SuppressWarnings("serial")
public class UrlTreeNode extends DefaultMutableTreeNode{

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(UrlTreeNode.class.getName());
	
	/**
	 * 
	 */
	private String nodeName;
	
	/**
	 * An ArrayDeque to store the received messages corresponding to the node url
	 */
	//private ArrayDeque<Message> msgs;
	private ArrayDeque<ExchangeRecord> msgs;
	
	/**
	 * 
	 */
	private int partialUrlCallCount;
	
	/**
	 * 
	 */
	private int completeUrlCallCount;

	private String partialUrl;
	
	/**
	 * 
	 */
	private boolean registered;
	
	/**
	 * @param urlPart
	 * @param counter
	 */
	public UrlTreeNode(String nodeName, String partialUrl) {
		super();
		this.nodeName = nodeName;
		this.partialUrlCallCount = 0;
		this.completeUrlCallCount = 0;
		//this.msgs = new ArrayDeque<Message>();
		this.msgs = new ArrayDeque<ExchangeRecord>();
		this.registered = false;
		this.setPartialUrl(partialUrl);
	}

	/**
	 * 
	 * @return
	 */
	public String getNodeName() {
		return nodeName;
	}

	/**
	 * 
	 * @return
	 */
	public int getPartialUrlcallCount() {
		return partialUrlCallCount;
	}

	/**
	 * 
	 * @return
	 */
	public int getCompleteUrlcallCount() {
		return completeUrlCallCount;
	}	
	
	/**
	 * Increase the counter by 1
	 */
	public void increasePartialUrlCounter() {
		logger.debug("[increasePartialUrlCounter()] Increase partialUrlCallCount !!");
		this.partialUrlCallCount++;
	}
	
	/**
	 * Increase the counter by 1
	 */
	public void increaseCompleteUrlCounter() {
		logger.debug("[increasePartialUrlCounter()] Increase completeUrlCallCount !!");
		this.completeUrlCallCount++;
	}	
	
	/**
	 * 
	 */
	public boolean isRegistered(){
		return registered;
	}
	
	/**
	 * 
	 */
	public void setRegistered(){
		this.registered = true;
	}
	
	/**
	 * Add a message in the message list 
	 */
	//public void addMessage(Message msg){
	public void addMessage(ExchangeRecord msg){
		this.msgs.add(msg);
	}
	
	/**
	 * 
	 * @return
	 */
	//public Deque<Message> getMessages(){
	public Deque<ExchangeRecord> getMessages(){
		return this.msgs;
	}
	
	@Override
	public String toString(){
		StringBuffer st = new StringBuffer();
		st.append("[");
		st.append("node name : ");
		st.append(nodeName);
		st.append(", node partial url call count : ");
		st.append(partialUrlCallCount);
		st.append(", node complete url call count : ");
		st.append(completeUrlCallCount);		
		st.append("]");
		return st.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public float getRatioPartial(UrlTree tree){
		return (float)(this.getPartialUrlcallCount()*100) / (float)(tree.getTotalUrlCount());
	}
	
	/**
	 * 
	 * @return
	 */
	public float getRatioComplete(UrlTree tree){
		return (float)(this.getCompleteUrlcallCount()*100) / (float)(tree.getTotalUrlCount());	
	}

	/**
	 * 
	 * @return
	 */
	public float getRatioChilds(){
		if(this.getPartialUrlcallCount() > 0){
			return ((float)(this.getChildCount()*100) / (float)this.getPartialUrlcallCount());
		} else {
			return 0;
		}
	}	
	
	/**
	 * 
	 * @return
	 */
	public float getRatioChildsToAncestor(){
		if(this.getPartialUrlcallCount() > 0){
			return ((float)(getChildsNumberRecursive(this)*100) / (float)this.getPartialUrlcallCount());
		} else {
			return 0;
		}		
	}

	/**
	 * 
	 * @return
	 */
	public int getTotalChildsNumber(){
		return getChildsNumberRecursive(this);
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

	public void setPartialUrl(String partialUrl) {
		this.partialUrl = partialUrl;
	}

	public String getPartialUrl() {
		return partialUrl;
	}	

}
