package org.easysoa.runtime.maven;

import java.io.Reader;

public class MavenApplicationPOMDescriptorProvider extends MavenAbstractPOMDescriptorProvider {

	public MavenApplicationPOMDescriptorProvider(Reader pomReader) {
		super(pomReader, false);
	}

}
