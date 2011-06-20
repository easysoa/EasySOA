package com.openwide.easysoa.monitoring.apidetector;

import java.util.HashMap;
import java.util.StringTokenizer;
import javax.swing.tree.DefaultTreeModel;
import org.apache.log4j.Logger;
import com.openwide.easysoa.monitoring.Message;

@SuppressWarnings("serial")
public class UrlTree extends DefaultTreeModel {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(UrlTree.class.getName());	
	
	/**
	 * Fast access to a complete url node
	 */
	private HashMap<String, UrlTreeNode> nodeIndex;
	
	/**
	 * 
	 * @param root
	 */
	public UrlTree(UrlTreeNode root) {
		super(root);
		nodeIndex = new HashMap<String, UrlTreeNode>();
	}

	/**
	 * Add a node in the tree
	 * If the node exists, 
	 * @param url The url to add in the tree
	 */
	public void addUrlNode(Message msg){
		// TODO check if it is necessary to remove the http prefix
		// Problem is that a special node http is created in nuxeo, consequence of StringTokenizer use with '/' delimiter
		String url = msg.getUrl().substring(msg.getUrl().indexOf("//")+2);
		logger.debug("url = " +  url);
		//String url = msg.getUrl();
		
		UrlTreeNode urlNode;
		StringBuffer path = new StringBuffer();
		String token;
		// Cut the url in tokens
		StringTokenizer st = new StringTokenizer(url, "/");
		// For each token
		logger.debug("[addUrlNode()] Complete URL : " + url);
		while(st.hasMoreTokens()){
			token = st.nextToken();
			logger.debug("[addUrlNode()] node : " + token);
			if(path.length() > 0){
				path.append("/");
			}
			path.append(token);
		
			// Find the node if exist in index
			logger.debug("[addUrlNode()] -----");
			logger.debug("[addUrlNode()] Partial URL : " + path.toString());
			urlNode = findNode(path.toString());
			
			// if node already seen, increase counter value
			if(urlNode != null){
				logger.debug("[addUrlNode()] Node found, counter ++");
			}
			// else add a new node in the tree
			else {
				logger.debug("[addUrlNode()] Add node in tree");
				urlNode = new UrlTreeNode(token);
				// Get the parent node and add a new child
				// if path = token => parent node = root
				if(path.toString().equals(token)){
					((UrlTreeNode)(this.getRoot())).add(urlNode);
				}
				// else parent node = path - token
				else {
					logger.debug("[addUrlNode()] Node to get : " + path.toString().subSequence(0, path.toString().lastIndexOf('/')));
					nodeIndex.get(path.toString().subSequence(0, path.toString().lastIndexOf('/'))).add(urlNode);
				}
				logger.debug("[addUrlNode()] New node index : " + path.toString());
				nodeIndex.put(path.toString(), urlNode);
			}
			
			// local computes :
			if(path.toString().equalsIgnoreCase(url)){
				urlNode.increaseCompleteUrlCounter();
			} else {
				urlNode.increasePartialUrlCounter();
			}
			
			// Add the msg in the node list if the last node is reached
			//TODO Problem : the informations contained in the message are not available for other nodes
			if(!st.hasMoreTokens()){
				urlNode.addMessage(msg);
			}
			// Send a event NO at the end of the run now
			//if(urlNode != null){
			//	EsperEngineSingleton.getEsperRuntime().sendEvent(new UrlTreeNodeEvent(urlNode, this));
			//}
		}
		// Increase the root for each url to obtain the total number of url
		((UrlTreeNode)(this.getRoot())).increasePartialUrlCounter();
	}

	/**
	 * Return the UrlTreeNode corresponding to the url, null if the UrlTreeNode is not in the tree
	 * @param url
	 * @return
	 */
	public UrlTreeNode findNode(String url){
		logger.debug("[findNode()] Finding node for : " + url);
		if(nodeIndex.containsKey(url)){
			return nodeIndex.get(url);
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public HashMap<String, UrlTreeNode> getNodeIndex(){
		return nodeIndex;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getTotalUrlCount(){
		return ((UrlTreeNode)(this.getRoot())).getPartialUrlcallCount();
	}
	
}
