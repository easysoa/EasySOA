/**
 * EasySOA: OW2 FraSCAti in Nuxeo
 * Copyright (C) 2011 INRIA, University of Lille 1
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * Contact: frascati@ow2.org
 *
 * Author: Philippe Merle
 *
 * Contributor(s):
 *
 */
package org.easysoa.frascati.nuxeo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.runtime.model.ComponentName;
import org.nuxeo.runtime.model.DefaultComponent;

import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.util.FrascatiException;

public class FraSCAtiService extends DefaultComponent
{
    public static final ComponentName NAME = new ComponentName(
    		"org.easysoa.frascati.nuxeo.FraSCAtiServiceComponent");

	private static Log log = LogFactory.getLog(FraSCAtiService.class);

	private FraSCAti frascati;

	public FraSCAtiService() throws FrascatiException
	{
		// Instantiate OW2 FraSCAti.
		frascati = FraSCAti.newFraSCAti();
	}

	/**
	 * Get an SCA composite.
	 * @param composite the composite to get.
	 * @return the composite.
	 */
	public Object getComposite(String composite) throws FrascatiException
	{
		return frascati.getComposite(composite);
	}

	//
	// Here add other methods according to your needs.
	//
}
