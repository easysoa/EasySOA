package org.easysoa.runtime;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.FileUtils;
import org.easysoa.runtime.api.Deployable;
import org.easysoa.runtime.api.RuntimeDeploymentService;
import org.easysoa.runtime.api.event.OnDeployListener;
import org.easysoa.runtime.api.event.OnUndeployListener;
import org.easysoa.runtime.copypaste.CopyPasteServer;
import org.easysoa.runtime.copypaste.CopyPasteServerEventService;
import org.easysoa.runtime.utils.FileDeployable;
import org.junit.Before;
import org.junit.Test;

public class CopyPasteTest {

	//private static Logger logger = Logger.getLogger(CopyPasteTest.class);
	
	private static final String ROOT = "target/test-classes/";
	private static final String SERVER_FOLDER = ROOT + "server/";
	private static final String DEPLOYABLE_NAME = "hello.jar";
	private static final String DEPLOYABLE_PATH = ROOT + DEPLOYABLE_NAME;

	@Before
	public void clearServer() throws IOException {
		FileUtils.deleteDirectory(SERVER_FOLDER);
	}
	
	@Test
	public void testDeploymentAndEvents() throws IOException {
		
		// Prepare server and deployable
		CopyPasteServer copyPasteServer = new CopyPasteServer(new File(SERVER_FOLDER));
		FileDeployable deployable = new FileDeployable(new File(DEPLOYABLE_PATH));
		File deployableTarget = new File(SERVER_FOLDER + DEPLOYABLE_NAME);

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
		assertEquals(deployable.getFile().length(), deployableTarget.length());
		verify(onDeployListener, times(1)).onDeploy((Deployable<?>) any());
		
		// Undeploy
		deploymentService.undeploy(deployable);
		assertFalse(deployableTarget.exists());
		verify(onUndeployListener, times(1)).onUndeploy((Deployable<?>) any());
		
	}
	
}
