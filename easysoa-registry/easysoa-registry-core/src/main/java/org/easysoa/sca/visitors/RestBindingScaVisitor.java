/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
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
 * Contact : easysoa-dev@groups.google.com
 */

package org.easysoa.sca.visitors;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.XMLScaImporter;

/**
 * Visitor for REST bindings
 * Creates new services when <binding.rest> tags are found.
 * @author mkalam-alami
 *
 */
public class RestBindingScaVisitor extends ServiceBindingVisitorBase {
    
	private static Log log = LogFactory.getLog(RestBindingScaVisitor.class);	
	
    public RestBindingScaVisitor(IScaImporter scaImporter) {
        super(scaImporter);
    }
    
    public boolean isOkFor(QName bindingQName) {
        return bindingQName.equals(new QName(XMLScaImporter.SCA_URI, "binding.rest"));
    }

    @Override
    public String getBindingUrl() {
    	log.debug("getBindingUrl");
        String serviceUrl = compositeReader.getAttributeValue(XMLScaImporter.FRASCATI_URI, "uri");
        return serviceUrl;
    }

}
