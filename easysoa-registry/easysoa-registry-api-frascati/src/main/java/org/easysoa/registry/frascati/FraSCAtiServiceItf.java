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
import java.net.URL;
import java.util.Set;

import org.easysoa.sca.visitors.BindingVisitorFactory;
import org.eclipse.stp.sca.Composite;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.util.FrascatiException;

public interface FraSCAtiServiceItf {

	/**
	 * Get an SCA composite.
	 * 
	 * @param composite the composite to get.
	 * @return the composite.
	 */
	public abstract Object getComposite(String composite) throws Exception;

	/**
	 * Added for convenience in a first time. TODO To improve usability and
	 * modularity, calls to its methods should be replaced by calls to
	 * FraSCAtiService methods calling them.
	 * 
	 * @return
	 * @throws FrascatiException
	 */
	public abstract FraSCAti getFraSCAti();

	/**
	 * 
	 */
	public abstract ParsingProcessingContext newParsingProcessingContext(URL... urls) throws Exception;

	/**
	 * 
	 * @param urls
	 * @return
	 * @throws FrascatiException
	 */
	public abstract DiscoveryProcessingContext newDiscoveryProcessingContext(URL... urls) throws Exception;

	/**
	 * 
	 * @param compositeUrl
	 * @param scaZipUrls
	 * @return
	 * @throws Exception
	 */
	public abstract Composite readComposite(URL compositeUrl, URL... scaZipUrls) throws Exception;

	/**
	 * Reads a zip (or jar), parses and returns the composites within. Classes
	 * they references are resolved within the zip. Only known (i.e. in the
	 * classpath / maven) extensions can be parsed (impls, bindings...).
	 * 
	 * TODO pbs : processContribution() and processComposite() return null in
	 * check mode, so has to use a separate ProcessingContext for each read (and
	 * alter each one's classloader)
	 * 
	 * @param scaZipFile
	 * @return
	 */
	public abstract Set<Composite> readScaZip(File scaZipFile) throws Exception;

	/**
	 * Returns the default impl of FraSCAtiRuntimeScaImporterItf (for this impl of FraSCAtiServiceItf)
	 * @return
	 * @throws Exception
	 */
	public abstract FraSCAtiRuntimeScaImporterItf newRuntimeScaImporter() throws Exception;

}