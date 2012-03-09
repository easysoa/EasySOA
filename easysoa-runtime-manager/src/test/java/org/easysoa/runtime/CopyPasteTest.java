package org.easysoa.runtime;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.easysoa.runtime.api.Deployable;
import org.easysoa.runtime.api.RuntimeDeploymentService;
import org.easysoa.runtime.api.event.OnDeployListener;
import org.easysoa.runtime.api.event.OnUndeployListener;
import org.easysoa.runtime.copypaste.CopyPasteServer;
import org.easysoa.runtime.copypaste.CopyPasteServerEventService;
import org.easysoa.runtime.utils.FileDeployable;
import org.junit.Test;

public class CopyPasteTest {

	//private static Logger logger = Logger.getLogger(CopyPasteTest.class);
	
	private static final String ROOT = "target/test-classes/";
	
	@Test
	public void testCopyPaste() throws IOException {
		
		// Prepare server and deployable
		CopyPasteServer copyPasteServer = new CopyPasteServer(new File(ROOT + "copyPaste"));
		FileDeployable deployable = new FileDeployable(new File(ROOT + "hello.txt"));
		File deployableTarget = new File(ROOT + "copyPaste/hello.txt");

		// Add mock listeners
		OnDeployListener onDeployListener = mock(OnDeployListener.class);
		OnUndeployListener onUndeployListener = mock(OnUndeployListener.class);
		CopyPasteServerEventService eventService = copyPasteServer.getEventService();
		eventService.addOnDeployListener(onDeployListener);
		eventService.addOnUndeployListener(onUndeployListener);
	
		// Deploy
		RuntimeDeploymentService deploymentService = copyPasteServer.getDeploymentService();
		deploymentService.deploy(deployable);
		assertTrue(deployableTarget.exists());
		verify(onDeployListener, times(1)).onDeploy((Deployable<?>) any());
		
		// Undeploy
		deploymentService.undeploy(deployable);
		assertFalse(deployableTarget.exists());
		verify(onUndeployListener, times(1)).onUndeploy((Deployable<?>) any());
		
	}
	
}
