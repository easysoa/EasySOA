package org.easysoa.api;

import org.easysoa.api.EasySOAApiSession;
import org.easysoa.impl.EasySOALocalApi;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * 
 * @author mkalam-alami
 *
 */
public class EasySOALocalApiFactory  {

    public static EasySOAApiSession createLocalApi(CoreSession session) throws Exception {
        return new EasySOALocalApi(session);
    }

}
