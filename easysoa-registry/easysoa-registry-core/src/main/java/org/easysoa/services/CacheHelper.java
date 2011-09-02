package org.easysoa.services;

import java.util.List;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CacheHelper {
    
    public static final String VCS_POOL_JMX_NAME = "jboss.jca:name=NXRepository/default,service=ManagedConnectionPool";

    private static Log log = LogFactory.getLog(VocabularyHelper.class);
    
    /**
     * Empties the "Visible Content Store" cache.
     * Source: https://doc.nuxeo.com/display/KB/How+to+invalidate+the+VCS+cache+programmatically
     * More on the VCS: https://doc.nuxeo.com/display/NXDOC/VCS+Architecture
     */
    public static void invalidateVcsCache() {
        ObjectName poolName;
        try {
            poolName = ObjectName.getInstance(VCS_POOL_JMX_NAME);
            List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(null);
            for (MBeanServer server : servers) {
                if (server.isRegistered(poolName)) {
                    server.invoke(poolName, "flush", new Object[0], new String[0]);
                    return;
                }
            }
            throw new UnsupportedOperationException(String.format(
                    "'%s' pool could not be found through local JMX lookup",
                    poolName));
        } catch (Exception e) {
            log.warn("Failed to invalidate VCS cache: "+e.getMessage());
        }
        
    }
}
