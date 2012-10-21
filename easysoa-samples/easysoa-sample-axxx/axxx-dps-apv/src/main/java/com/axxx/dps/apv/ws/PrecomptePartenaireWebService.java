package com.axxx.dps.apv.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;


@WebService(name="PrecomptePartenaireService", targetNamespace="http://www.axxx.com/dps/apv")
public interface PrecomptePartenaireWebService {

	@WebMethod
	public void creerPrecompte(@WebParam(name="precomptePartenaire") PrecomptePartenaire precomptePartenaire);

}
