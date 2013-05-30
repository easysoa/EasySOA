/**
 * EasySOA Proxy
 * Copyright 2011-2013 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.proxy.core.api.monitoring;

import org.easysoa.message.InMessage;
import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.configuration.EasySOAGeneratedAppConfiguration;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.core.api.exchangehandler.EasySOAv1SOAPDiscoveryMessageHandler;
import org.easysoa.records.Exchange;
import org.easysoa.records.ExchangeRecord;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Specific unit test for the monitoring handlers
 *
 * @author jguillemotte
 */
public class MessageHandlersTest {

    // TODO : Both tests uses the Real Nuxeo registry V1 rest service
    // If this service is not started before the test, the test will fail

    @Test
    public void DiscoveryMessageHandlerTest() throws Exception {

        EasySOAv1SOAPDiscoveryMessageHandler handler = new EasySOAv1SOAPDiscoveryMessageHandler();

        // proxy configuration
        ProxyConfiguration proxyConf = new ProxyConfiguration();
        proxyConf.addParameter(EasySOAGeneratedAppConfiguration.ENVIRONMENT_PARAM_NAME, "Production");
        proxyConf.addParameter(EasySOAGeneratedAppConfiguration.PROJECTID_PARAM_NAME, "/default-domain/Intégration DPS - DCV/Realisation_v");

        // build an Exchange message for a WSDL get request
        ExchangeRecord record = new ExchangeRecord();
        record.setExchange(new Exchange());
        record.setInMessage(new InMessage());
        record.setOutMessage(new OutMessage());

        record.getInMessage().setProtocol("http");
        record.getInMessage().setMethod("get");
        record.getInMessage().setPath("/data/info.wso?WSDL");
        record.getInMessage().setServer("footballpool.dataaccess.eu");

        // Call handler
        handler.setHandlerConfiguration(proxyConf);
        handler.handleMessage(record.getInMessage(), record.getOutMessage());
    }

}
