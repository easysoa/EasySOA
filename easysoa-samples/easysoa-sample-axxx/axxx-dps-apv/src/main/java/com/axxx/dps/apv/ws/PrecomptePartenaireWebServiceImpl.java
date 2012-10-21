package com.axxx.dps.apv.ws;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;


@WebService(name="PrecomptePartenaire",
	targetNamespace="http://www.axxx.com/dps/apv",
	serviceName="PrecomptePartenaireServiceImpl")
@Service("com.axxx.dps.apv.ws.precomptePartenaireWebServiceImpl")
public class PrecomptePartenaireWebServiceImpl implements PrecomptePartenaireWebService {

   	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public void creerPrecompte(PrecomptePartenaire precomptePartenaire) {
		log.debug("creerPrecompte start");
	}

	
}
