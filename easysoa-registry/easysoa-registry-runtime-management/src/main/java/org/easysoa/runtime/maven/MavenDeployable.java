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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.easysoa.runtime.api.AbstractDeployable;

/**
 * Maven artifact
 * 
 * @author mkalam-alami
 *
 */
public class MavenDeployable extends AbstractDeployable<MavenID> {

	public MavenDeployable(MavenID id, InputStream is, InputStream pomIs) throws IOException {
		super(id, is);
		
		// Fetch dependencies by exploring POM
		MavenPom mavenPom = new MavenPom(new InputStreamReader(pomIs));
		MavenDeployableDescriptor deployableDescriptor = mavenPom.getDeployableDescriptor();
		this.dependencies = deployableDescriptor.getDependencies();
		pomIs.close();
	}
	
	@Override
	public String getFileName() {
		return id.getArtifactId() + "-" + id.getVersion() + ".jar"; // TODO POM projects?
	}
	
}
