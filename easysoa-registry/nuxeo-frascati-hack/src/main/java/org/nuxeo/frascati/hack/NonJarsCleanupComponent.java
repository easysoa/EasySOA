package org.nuxeo.frascati.hack;

import java.io.File;

import org.apache.log4j.Logger;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * 
 * @author mkalam-alami
 *
 */
public class NonJarsCleanupComponent extends DefaultComponent {

    private static final Logger logger = Logger.getLogger(NonJarsCleanupComponent.class);
    
    @Override
    public void activate(ComponentContext context) throws Exception {

        File[] foldersToClean = new File[] {
                new File("./nxserver/bundles"),
                new File("./nxserver/lib"),
                new File("./nxserver/plugins")
        };

        File trashFolder = new File("./nxserver/trash");
        if (!trashFolder.exists()) {
            trashFolder.mkdir();
        }

        for (File folderToClean : foldersToClean) {
            File[] filesToTest = folderToClean.listFiles();
            for (File fileToTest : filesToTest) {
                if (!fileToTest.getPath().endsWith(".jar")) {
                    File dest = new File(trashFolder.toString() + File.separator + fileToTest.getName());
                    if (dest.exists()) {
                        dest.delete();
                        logger.info("Overriding '" + dest + "'...");
                    }
                    if (fileToTest.renameTo(dest)) {
                        logger.info("Moved away file '" + fileToTest + "'");
                    }
                    else {
                        logger.warn("Failed to move away file '" + fileToTest + "', FraSCAti in Nuxeo may fail");
                    }
                }
            }
        }
    }

}
