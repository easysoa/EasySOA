package org.easysoa.api;

import org.easysoa.api.EasySOAApi;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * 
 * @author mkalam-alami
 *
 */
public class EasySOALocalApiFactory  {

    public static EasySOAApi createLocalApi(CoreSession session) throws Exception {
        return new EasySOALocalApi(session);
    }

}
