package org.easysoa.runtime.frascati;

import java.io.InputStream;
import java.util.List;

import org.easysoa.runtime.api.Deployable;
import org.easysoa.runtime.api.DeployableDescriptor;

public class FraSCAtiDeployable implements Deployable<String> {

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DeployableDescriptor<?>> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean areAllDependenciesKnown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InputStream getInputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}

}
