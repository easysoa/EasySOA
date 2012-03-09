package org.easysoa.runtime.maven;

import java.io.Reader;

public class MavenPOMDescriptorProvider extends MavenAbstractPOMDescriptorProvider {

	public MavenPOMDescriptorProvider(Reader pomReader) {
		super(pomReader, false);
	}

}
