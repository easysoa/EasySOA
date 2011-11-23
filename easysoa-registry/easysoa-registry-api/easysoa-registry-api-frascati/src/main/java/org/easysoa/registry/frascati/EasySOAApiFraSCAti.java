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

package org.easysoa.registry.frascati;

import java.io.File;

import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.frascati.ApiFraSCAtiScaImporter;
import org.easysoa.sca.visitors.RemoteBindingVisitorFactory;
import org.ow2.frascati.util.FrascatiException;

/**
 * 
 * @author mkalam-alami, jguillemotte
 *
 */
public class EasySOAApiFraSCAti extends FraSCAtiServiceBase {
	
	private static EasySOAApiFraSCAti instance = null;

	public static final EasySOAApiFraSCAti getInstance() throws FrascatiException{
		if(instance == null){
			instance  = new EasySOAApiFraSCAti(); 
		}
		return instance;
	}
	
	protected EasySOAApiFraSCAti() throws FrascatiException {
		super();
	}

	@Override
	public FraSCAtiRuntimeScaImporterItf newRuntimeScaImporter() throws Exception {
		return this.newRemoteRuntimeScaImporter();
	}

	public FraSCAtiRuntimeScaImporterItf newRemoteRuntimeScaImporter() throws Exception {
		RemoteBindingVisitorFactory apiBindingVisitorFactory = new RemoteBindingVisitorFactory();
		ApiFraSCAtiScaImporter apiFraSCAtiScaImporter = new ApiFraSCAtiScaImporter(apiBindingVisitorFactory, null, this);
		return apiFraSCAtiScaImporter;
	}
	
	public IScaImporter newRemoteScaImporter(File compositeFile) throws Exception {
		RemoteBindingVisitorFactory apiBindingVisitorFactory = new RemoteBindingVisitorFactory();
		ApiFraSCAtiScaImporter apiFraSCAtiScaImporter = new ApiFraSCAtiScaImporter(apiBindingVisitorFactory, compositeFile, this);
		return apiFraSCAtiScaImporter;
	}
	
}
