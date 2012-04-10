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

package org.easysoa.runtime.maven;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.DomainInit;
import org.easysoa.runtime.api.DeployableDescriptorProvider;
import org.easysoa.runtime.api.DeployableProvider;

/**
 * Maven repository access.
 * 
 * @author mkalam-alami
 *
 */
public class MavenRepository implements DeployableProvider<MavenDeployable>, 
		DeployableDescriptorProvider<MavenDeployableDescriptor, MavenID> {

	private static final String JAR_EXT = "jar";
	
	private static final String POM_EXT = "pom";

	private static final String DEFAULT_REPOSITORY = "http://search.maven.org/remotecontent?filepath=";

    private static Log log = LogFactory.getLog(DomainInit.class);
    
	private URL baseUri;
	
	public MavenRepository() {
		try {
			this.baseUri = new URL(DEFAULT_REPOSITORY);
		} catch (MalformedURLException e) {
			log.error("Default repository URL is invalid", e);
		}
	}
	
	public MavenRepository(URL url) {
		this.baseUri = url;
	}

	@Override
	public MavenDeployable fetchDeployable(Object id) throws IOException {
		
		// Translate ID
		MavenID mavenId = null;
		if (id instanceof MavenID) {
			mavenId = (MavenID) id;
		}
		else if (id instanceof String) {
			// Supports MavenIDs in string format: "group:artifact:version"
			String[] mavenIdTokens = ((String) id).split("\\:");
			if (mavenIdTokens.length == 3) {
				mavenId = new MavenID(mavenIdTokens[0], mavenIdTokens[1], mavenIdTokens[2]);
			}
		}
		
		// Fetch deployable
		if (mavenId != null) {
			URL jarUrl = getUrl(mavenId, JAR_EXT);
			if (jarUrl != null) {
				URL pomUrl = getUrl(mavenId, POM_EXT);
				if (pomUrl != null) {
					try {
						return new MavenDeployable(mavenId, jarUrl.openStream(), pomUrl.openStream());
					}
					catch (FileNotFoundException e) {
						// Deployable not available on this repository
						return null;
					}
				}
			}
		}
		
		return null;
	}
	
	public MavenDeployableDescriptor fetchDeployableDescriptor(MavenID id) throws IOException {
		// Use POM as a DeployableDescriptorProvider to find deployable's dependencies
		InputStreamReader inputStreamReader;
		try {
			URL pomUrl = getUrl(id, POM_EXT);
			if (pomUrl != null) {
				inputStreamReader = new InputStreamReader(pomUrl.openStream());
			}
			else {
				return null;
			}
		}
		catch (IOException e) {
			throw new IOException("Could not download POM for specified artifact", e);
		}
		return new MavenPom(inputStreamReader).getDeployableDescriptor();
	}
	
	private URL getUrl(MavenID id, String extension) {
		try {
			if (id.getVersion() == null) { // XXX Managed versions are unsupported
				log.warn("Ignoring artifact " + id.toString() + " as its version number is missing");
				return null;
			}
			return new URL(baseUri.toString() 
					+ id.getGroupId().replace('.', '/')
					+ "/" + id.getArtifactId()
					+ "/" + id.getVersion()
					+ "/" + id.getArtifactId() + "-" + id.getVersion() + "." + extension);
		} catch (MalformedURLException e) {
			throw new InvalidParameterException("Cannot build URL using specified Maven ID (" + e.getMessage() + ")");
		}
	}

}
