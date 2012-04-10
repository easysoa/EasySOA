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

package org.easysoa.runtime.utils;

import java.io.File;
import java.io.IOException;

import org.easysoa.runtime.api.RuntimeControlService;
import org.easysoa.runtime.copypaste.CopyPasteServer;

/**
 * Useless CopyPasteServer that can be "started" and "stopped".
 * For runtime managment testing purposes.
 * 
 * @author mkalam-alami
 *
 */
public class FakeStartableCopyPasteServer extends CopyPasteServer implements RuntimeControlService {
	
	private File startedFile;
	
	public FakeStartableCopyPasteServer(String deployablesDirectory) {
		this(new File(deployablesDirectory));
	}
	
	public FakeStartableCopyPasteServer(File deployablesDirectory) {
		super(deployablesDirectory);
		startedFile = new File(deployablesDirectory.getPath() + "/STARTED");
	}
	
	@Override
	public RuntimeControlService getControlService() {
		return this;
	}

	@Override
	public RuntimeState getState() {
		if (startedFile.exists()) {
			return RuntimeState.STARTED; 
		}
		else {
			return RuntimeState.STOPPED;
		}
	}

	@Override
	public boolean start() {
		try {
			return startedFile.createNewFile();
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean stop() {
		return startedFile.delete();
	}
	
}
