/**
 * 
 */
package org.easysoa.wsdl.twitter_test_run_wsdl;

import org.easysoa.twitter_test_run.ReqTemplateRecord1Request;
import org.easysoa.twitter_test_run.ReqTemplateRecord1Response;
import org.easysoa.twitter_test_run.ReqTemplateRecord2Request;
import org.easysoa.twitter_test_run.ReqTemplateRecord2Response;
import org.easysoa.twitter_test_run.ResTemplateRecord1Request;
import org.easysoa.twitter_test_run.ResTemplateRecord1Response;
import org.easysoa.twitter_test_run.ResTemplateRecord2Request;
import org.easysoa.twitter_test_run.ResTemplateRecord2Response;

/**
 * @author jguillemotte
 *
 */
public class TwitterTestRunPortTypeImpl implements TwitterTestRunPortType {

	/* (non-Javadoc)
	 * @see org.easysoa.wsdl.twitter_test_run_wsdl.TwitterTestRunPortType#resTemplateRecord2(org.easysoa.twitter_test_run.ResTemplateRecord2Request)
	 */
	@Override
	public ResTemplateRecord2Response resTemplateRecord2(ResTemplateRecord2Request body) {
		ResTemplateRecord2Response response = new ResTemplateRecord2Response();
		response.setResponse("No response for the request");
		return response;
	}

	/* (non-Javadoc)
	 * @see org.easysoa.wsdl.twitter_test_run_wsdl.TwitterTestRunPortType#reqTemplateRecord2(org.easysoa.twitter_test_run.ReqTemplateRecord2Request)
	 */
	@Override
	public ReqTemplateRecord2Response reqTemplateRecord2(ReqTemplateRecord2Request body) {
		ReqTemplateRecord2Response response = new ReqTemplateRecord2Response();
		// When returning JSON data, it must be surrounded by extra '"' to avoid the JSON data to be interpreted by display engine
		response.setResponse("\"{\"user\":\"toto\",\"tweetNumber\":\"5\",\"tweets\": [{\"tweet\": \"The last tweet\"},{\"tweet\": \"Not the first tweet but not the last\"},{\"tweet\": \"Another tweet\"},{\"tweet\": \"Maybe the last tweet\"},{\"tweet\": \"The last tweet\"},]}\"");
		return response;
	}

	/* (non-Javadoc)
	 * @see org.easysoa.wsdl.twitter_test_run_wsdl.TwitterTestRunPortType#reqTemplateRecord1(org.easysoa.twitter_test_run.ReqTemplateRecord1Request)
	 */
	@Override
	public ReqTemplateRecord1Response reqTemplateRecord1(ReqTemplateRecord1Request body) {
		ReqTemplateRecord1Response response = new ReqTemplateRecord1Response();
		response.setResponse("\"{\"user\":\"toto\",\"lastTweet\":\"This is the last tweet\"}\"");
		System.out.println("ReqTemplateRecord1Response operation returns response : " + response.getResponse());		
		return response;
	}

	/* (non-Javadoc)
	 * @see org.easysoa.wsdl.twitter_test_run_wsdl.TwitterTestRunPortType#resTemplateRecord1(org.easysoa.twitter_test_run.ResTemplateRecord1Request)
	 */
	@Override
	public ResTemplateRecord1Response resTemplateRecord1(ResTemplateRecord1Request body) {
		ResTemplateRecord1Response response = new ResTemplateRecord1Response();
		response.setResponse("No response for the request");
		return response;
	}

}
