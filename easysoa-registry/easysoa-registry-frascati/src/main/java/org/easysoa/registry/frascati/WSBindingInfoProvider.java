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


package org.easysoa.registry.frascati;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.sca.frascati.FraSCAtiScaImporter;
import org.eclipse.stp.sca.WebServiceBinding;

public class WSBindingInfoProvider extends FrascatiBindingInfoProviderBase {

	// Logger
	private static Log log = LogFactory.getLog(WSBindingInfoProvider.class);	
	
	/**
	 * 
	 * @param frascatiScaImporter
	 */
	public WSBindingInfoProvider(FraSCAtiScaImporter frascatiScaImporter) {
		super(frascatiScaImporter);
	}

	@Override
	public boolean isOkFor(Object object) {
		log.debug("Object to check : " + object);
		if (object instanceof WebServiceBinding) {
			return true;
		}
		return false;
	}

	@Override
	public String getBindingUrl() {
		return frascatiScaImporter.getBindingUrl();
	}

}
