package org.openwide.easysoa.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

@Deprecated
public class SoapClientProxySelector extends ProxySelector {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(SoapClientProxySelector.class.getName());	
	
	@Override
	public List<Proxy> select(URI uri) {
		ArrayList<Proxy> list = new ArrayList<Proxy>();
		Proxy proxy;
		
		logger.info(uri.getUserInfo());
		logger.info(uri.getHost());
		logger.info(uri.getPath());
		logger.info(uri.getPort());
		logger.info(uri.getQuery());
		logger.info(uri.getAuthority());
		logger.info(uri.getScheme());
		logger.info(uri.getSchemeSpecificPart());
		logger.info(uri.getFragment());
		
		if(uri.getPort() != 8085 || "wsdl".equals(uri.getQuery())){
			proxy = Proxy.NO_PROXY;
			logger.info("No proxy returned");
		} else {
			proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("localhost", 8082));
			logger.info("localhost:8082 returned as proxy");
		}
		list.add(proxy);		
		return list;		
	}

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		System.err.println("Connection to " + uri + " failed.");
	}

}
