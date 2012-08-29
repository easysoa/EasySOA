/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.core.api.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * To Implement all the web services registered in the serviceToLaunch.composite
 * They will be launched automatically by frascati
 * 
 * @author fntangke
 */
@Scope("Composite")
public class SubscriptionWebserviceImpl implements ISubscriptionWebService {

	// private Configuration configuration;
	@Reference
	private IEventMessageHandler eventMessageHandler;

	private MonitoringConfiguration monitoringConfiguration;

	public SubscriptionWebserviceImpl() {
		this.monitoringConfiguration = new MonitoringConfiguration();
	}

	/**
	 * Used to return a valid json of xml result For our needs.
	 */
	public void initialize() {

		ListenedService servtolisten = new ListenedService();
		servtolisten.setUrl("http://www.google.fr");
		LaunchedService laweb = new LaunchedService();
		laweb.setUrl("http://Yahoo.fr");
		LaunchedService laweb1 = new LaunchedService();
		laweb1.setUrl("http://Yahorgeo.fr");
		// List<LaunchedService> launchwebserv = new
		// ArrayList<LaunchedService>();
		// launchwebserv.add(laweb);
		// launchwebserv.add(laweb1);
		RegexCondition regexCondition = new RegexCondition("http://mail.fr");
		Subscription subs = new Subscription();
		// subs.addRegexCondition(servtolisten);
		subs.addRegexCondition(regexCondition);
		subs.addLaunchedService(laweb);
		subs.addLaunchedService(laweb1);
		// subs.setLaunchedservices(launchwebserv);
		Configuration configuration = new Configuration();
		configuration.addSubscription(subs);
		configuration.setProxy("proxy de test http://efef/?swdl");
		// this.monitoringConfiguration....(configuration)
		this.monitoringConfiguration.setConfiguration(configuration);

	}

	/**
	 * To set subscriptions and set the
	 * 
	 * @return the subscription upgraded Sample of response <?xml version="1.0"
	 *         encoding="UTF-8"
	 *         standalone="yes"?><configuration><conditions><regexCondition
	 *         ><regex
	 *         >http://mail.fr</regex></regexCondition><launchedservices><
	 *         url>http
	 *         ://Yahoo.fr</url></launchedservices><launchedservices><url
	 *         >http://
	 *         Yahorgeo.fr</url></launchedservices></conditions></configuration>
	 */

	@Override
	public MonitoringConfiguration udpateMonitoringConfiguration(
			Configuration configuration) {
		// we get this uid service in nuxeo
		this.monitoringConfiguration.setConfiguration(configuration);
		// update listenedServiceUrlToServicesToLaunchUrlMap :
		HashMap<List<Condition>, List<String>> newListenedServiceUrlToServicesToLaunchUrlMap = new HashMap<List<Condition>, List<String>>();
		for (Subscription subscription : configuration.getSubscriptions()) {
			// TODO mettre à jour le contenu en changeant pour mettre les
			// conditions à la place des listenedservices
			newListenedServiceUrlToServicesToLaunchUrlMap.put(
					subscription.getConditions(),
					subscription.getLaunchedServiceString());
		}

		// single update because we're calling a synchronized method here :
		// TODO Absolument changer et rajouter la methode
		// setListenedServiceUrlToServicesToLaunchUrlMap(newListenedServiceUrlToServicesToLaunchUrlMap);
		// a l iterface dmessage handler et la retirer à emessagehandler

		this.eventMessageHandler
				.setListenedServiceUrlToServicesToLaunchUrlMap(newListenedServiceUrlToServicesToLaunchUrlMap);
		// return this.configuration;
		return this.monitoringConfiguration;
	}

	/**
	 * @return the Configuration
	 */
	@Override
	public MonitoringConfiguration getMonitoringConfiguration() {
		this.initialize();
		return this.monitoringConfiguration;
	}

	/*
	 * @Override
	 * 
	 * @GET
	 * 
	 * @Path("/subscription")
	 * 
	 * @Produces({ "application/xml"}) public Configuration getConf() {
	 * Configuration conf = new Configuration();
	 * conf.setProxy("proxy USA, htpsedef15.3"); List<Subscription> listcond =
	 * new ArrayList<Subscription>(); ListenedService servtolisten = new
	 * ListenedService(); servtolisten.setUrl("http://www.google.fr");
	 * LaunchedService laweb = new LaunchedService();
	 * laweb.setUrl("http://Yahoo.fr"); LaunchedService laweb1 = new
	 * LaunchedService(); laweb1.setUrl("http://Yahorgeo.fr"); RegexCondition
	 * regexCondition = new RegexCondition("http://mail.fr"); Subscription subs
	 * = new Subscription(); // subs.addRegexCondition(servtolisten);
	 * subs.addRegexCondition(regexCondition); subs.addLaunchedService(laweb);
	 * subs.addLaunchedService(laweb1);
	 * 
	 * conf.addSubscription(subs); return conf; }
	 */



}
