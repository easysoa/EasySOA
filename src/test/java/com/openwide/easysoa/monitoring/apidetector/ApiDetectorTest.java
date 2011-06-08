package com.openwide.easysoa.monitoring.apidetector;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.openwide.easysoa.esperpoc.HttpProxyImpl;
import com.openwide.easysoa.esperpoc.esper.Message;
import com.openwide.easysoa.monitoring.apidetector.UrlTree;
import com.openwide.easysoa.monitoring.apidetector.UrlTreeNode;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ApiDetectorTest 
    extends TestCase
{
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(getInvokingClassName());
	
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ApiDetectorTest( String testName )
    {
        super( testName );
    }
    
    public static String getInvokingClassName() {
    	return Thread.currentThread().getStackTrace()[1].getClassName();
    }
    

	/**
	 * To add => ratios to detect application / service
	 * Notion de profondeur dans l'objet UrlTreeNode ??
	 * Plus la profondeur est grande, plus le ratio nodeCount / totalUrl count doit etre elevé
	 * Moins la profondeur est grande, moins le ratio doit etre elevé
	 * Cas spécial : On appelle tj le même url => pas moyen de déduire l'application / service avec le ratio puique tous les noeuds auront le même count
	 * 
	 * Ratio => entre 0 et 1 (ratio = nb total d'url / count de chaque node)
	 * 
	 * Dans exemple : application => profondeur 1 & 2, counter = 5, ratio = 1
	 * services => profondeur 3, counter = 2 et 3, ratios = 0.4 et 0.6
	 * Parametres dynamiques : profondeur 4, counter = 1, ratios = 0.2
	 * 
	 *  exemple ratio (ok pour exemple mais fausse ailleurs, ca ne fonctionne que si 1 seule application):
	 *  - entre 0.7 et 1 => application
	 *  - entre 0.3 et 0.7 => services
	 *  - entre 0 et 0.3 => resources (paramètres dynamiques)
	 * 
	 * To add => hard coded/scripted scenarios (nuxeo / github introspection)
	 * A etudier si implem en Java ou en node.js ou autre ...
	 * Nuxeo / Github => https
	 * github => api.github.com
	 * nuxeo => 
	 * 
	 * PB pas de liens dans les réponses renvoyées par ces API => comment introspecter ???
	 * D'apres le richardson maturity model => nuxeo n'est pas de niveau 4
	 * 
	 * To add => mode validé / detection.
	 * 
	 * Esper : comment l'integrer ? Quand declencher la creation de messages / services...
	 */
	public void testUrlDetection(){
		// Test regular expressions
		//app.testRegex();
    	// Test Webservice detection HashSet => Tree
		
		System.out.println( "Test URL detection start");
		UrlTree urlTree = new UrlTree(new UrlTreeNode("root"));
		UrlMock urlMock;
		Iterator<String> iter;
		String urlString;
		//for(int i=0; i<1000;i++){
		for(int i=0; i<1;i++){
			urlMock = new UrlMock();
			//iter = urlMock.getUrlData().iterator();
			iter = urlMock.getTwitterUrlData().iterator();
			while(iter.hasNext()){
				urlString = iter.next();
				System.out.println("**** New URL :" + urlString);
				try{
					URL url = new URL(urlString);
					Message msg = new Message(url.getProtocol(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), "REST");
					urlTree.addUrlNode(urlString, msg);
				}
				catch(Exception ex){
					logger.error("**** problem spotted  :" + ex.getMessage());	
				}
			}
		}
		System.out.println("Test URL detection stop");
		System.out.println("Printing tree node index ***");
		System.out.println("Total url count : " + urlTree.getTotalUrlCount());
		String key;
		HashMap<String, UrlTreeNode> index = urlTree.getNodeIndex();
		Iterator<String> iter2 = index.keySet().iterator();
		UrlTreeNode parentNode;
		float ratioPartial;
		float ratioComplete;
		float ratioChilds;
		float ratioChildsRecursive;
		while(iter2.hasNext()){
			key = iter2.next();
			if(index.get(key).getLevel() <= 3){
				parentNode = (UrlTreeNode)(index.get(key).getParent());
				System.out.println("complete = " + index.get(key).getCompleteUrlcallCount() + ",partial = " + index.get(key).getPartialUrlcallCount() + ",total = " + urlTree.getTotalUrlCount());
				ratioPartial = (float)(index.get(key).getPartialUrlcallCount()*100) / (float)(urlTree.getTotalUrlCount());
				ratioComplete = (float)(index.get(key).getCompleteUrlcallCount()*100) / (float)(urlTree.getTotalUrlCount());
				// Direct childs
				if(index.get(key).getPartialUrlcallCount() > 0){
					ratioChilds = ((float)(index.get(key).getChildCount()*100) / (float)index.get(key).getPartialUrlcallCount());
				} else {
					ratioChilds = 0;
				}
				// Recursive childs
				if(index.get(key).getPartialUrlcallCount() > 0){
					ratioChildsRecursive = ((float)(getChildsNumberRecursive(index.get(key))*100) / (float)index.get(key).getPartialUrlcallCount());
				} else {
					ratioChildsRecursive = 0;
				}
				
				//System.out.println("Total Child number for the node : " + getChildsNumberRecursive(index.get(key)));
				System.out.println(key + " -- " + index.get(key).toString() + ", parent node => " + parentNode.getNodeName() + ", Depth => " + index.get(key).getDepth() + ", Level => " + index.get(key).getLevel()); 
				System.out.println(", direct node childs => " + index.get(key).getChildCount() + "Total childs number => " + getChildsNumberRecursive(index.get(key)));
				System.out.println(", ratioPartial => " + ratioPartial + "%, ratioComplete => " + ratioComplete + "%" + ", Ratio childs => " + ratioChilds + "%" + ", Ratio childs recursive => " + ratioChildsRecursive + "%");
			}
		}
	}
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	private int getChildsNumberRecursive(UrlTreeNode node){
		UrlTreeNode child;
		//System.out.println("Node name => " + node.getNodeName());
		int nodeChildNumber = node.getChildCount();
		//System.out.println("Node name => " + node.getChildCount());
		int childNumber = 0;
		if(nodeChildNumber > 0){
			//System.out.println("getting all childs");
			for(int i=0; i < nodeChildNumber; i++){
				//System.out.println("child number " + i);
				child = ((UrlTreeNode)(node.getChildAt(i)));
				childNumber = childNumber + getChildsNumberRecursive(child);
			}
		}
		return nodeChildNumber + childNumber;
	}
	
    /**
     * Test a regex expression
     */
    public void testRegex(){
        System.out.println( "Test regex web services start");
        String wsRequest = "http://www.google.com/search?client=ubuntu&channel=fs&q=java";
        String regex = ".*?.*=.*";
        if(wsRequest.matches(regex)){
        	System.out.println("It works !!");
        } else {
        	System.out.println("try again !!");
        }
        System.out.println( "Test regex web services end");
    }
}
