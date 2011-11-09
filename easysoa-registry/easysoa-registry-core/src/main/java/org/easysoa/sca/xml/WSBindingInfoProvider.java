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
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.sca.xml;

import javax.xml.namespace.QName;

public class WSBindingInfoProvider extends XMLBindingInfoProviderBase {

	public WSBindingInfoProvider(XMLScaImporter xmlScaImporter) {
		super(xmlScaImporter);
	}

	@Override
	public boolean isOkFor(Object object) {
		QName bindingQName = (QName) object;
        return bindingQName.equals(new QName(XMLScaImporter.SCA_URI, "binding.ws"));
	}

	@Override
	public String getBindingUrl() {
        // getting referenced service url
        String refUrl = xmlScaImporter.getCompositeReader().getAttributeValue(null, "uri"); // rather than "" ?! // TODO SCA_URI
        if (refUrl == null) {
            String wsdlLocation = xmlScaImporter.getCompositeReader().getAttributeValue(XMLScaImporter.WSDLINSTANCE_URI , "wsdlLocation");
            if (wsdlLocation != null) {
                refUrl = wsdlLocation.replace("?wsdl", "");
            }
        }
        return refUrl;
	}

}
