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

import org.easysoa.runtime.api.Deployable;
import org.easysoa.runtime.api.event.OnDeployListener;
import org.easysoa.runtime.api.event.OnUndeployListener;
import org.easysoa.runtime.copypaste.CopyPasteServer;
import org.easysoa.runtime.utils.FileDeployable;
import org.junit.Before;
import org.junit.Test;
import org.nuxeo.common.utils.FileUtils;

public class CopyPasteTest {

	//private static Logger logger = Logger.getLogger(CopyPasteTest.class);
	
	private static final String ROOT = "target/test-classes/";
	private static final String SERVER_FOLDER = ROOT + "server/";
	private static final String DEPLOYABLE_NAME = "hello.jar";
	private static final String DEPLOYABLE_PATH = ROOT + DEPLOYABLE_NAME;

	@Before
	public void clearServer() throws IOException {
		FileUtils.deleteTree(new File(SERVER_FOLDER));
	}
	
	@Test
	public void testDeploymentAndEvents() throws IOException {
		
		// Prepare server and deployable
		CopyPasteServer copyPasteServer = new CopyPasteServer(new File(SERVER_FOLDER));
		FileDeployable deployable = new FileDeployable(DEPLOYABLE_PATH);
		File deployableTarget = new File(SERVER_FOLDER + DEPLOYABLE_NAME);

		// Add mock listeners
		OnDeployListener onDeployListener = mock(OnDeployListener.class);
		OnUndeployListener onUndeployListener = mock(OnUndeployListener.class);
		copyPasteServer.addOnDeployListener(onDeployListener);
		copyPasteServer.addOnUndeployListener(onUndeployListener);
	
		// Deploy
		copyPasteServer.deploy(deployable);
		assertTrue(deployableTarget.exists());
		assertEquals(1, copyPasteServer.getDeployedDeployables().size());
		assertEquals(deployable.getFile().length(), deployableTarget.length());
		verify(onDeployListener, times(1)).onDeploy((Deployable<?>) any());
		
		// Undeploy
		copyPasteServer.undeploy(deployable);
		assertFalse(deployableTarget.exists());
		assertEquals(0, copyPasteServer.getDeployedDeployables().size());
		verify(onUndeployListener, times(1)).onUndeploy((Deployable<?>) any());
		
	}
	
}
