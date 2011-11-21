package org.easysoa.registry.frascati;

import java.io.File;
import java.net.URL;
import java.util.Set;

import org.eclipse.stp.sca.Composite;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.util.FrascatiException;

public interface FraSCAtiServiceItf {

	public abstract Component[] startScaApp(URL scaAppUrl) throws FrascatiException;

	/**
	 * Get an SCA composite.
	 * 
	 * @param composite the composite to get.
	 * @return the composite.
	 */
	public abstract Object getComposite(String composite) throws FrascatiException;

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
	public abstract ParsingProcessingContext newParsingProcessingContext(URL... urls) throws FrascatiException;

	/**
	 * 
	 * @param urls
	 * @return
	 * @throws FrascatiException
	 */
	public abstract DiscoveryProcessingContext newDiscoveryProcessingContext(URL... urls) throws FrascatiException;

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

}