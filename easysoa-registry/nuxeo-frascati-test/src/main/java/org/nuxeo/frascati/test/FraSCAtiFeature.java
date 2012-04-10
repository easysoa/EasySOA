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

package org.nuxeo.frascati.test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.logging.Logger;

import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.frascati.test.FraSCAtiFeature;
import org.nuxeo.runtime.test.WorkingDirectoryConfigurator;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.RuntimeFeature;
import org.nuxeo.runtime.test.runner.RuntimeHarness;
import org.nuxeo.runtime.test.runner.SimpleFeature;

@Features(RuntimeFeature.class)
@Deploy({ "org.nuxeo.runtime.bridge", "org.nuxeo.frascati" })
public class FraSCAtiFeature extends SimpleFeature implements WorkingDirectoryConfigurator {

    protected static final Logger log = Logger.getLogger(FraSCAtiFeature.class.getCanonicalName());

    public void initialize(FeaturesRunner runner) {
        runner.getFeature(RuntimeFeature.class).getHarness().addWorkingDirectoryConfigurator(this);
    }

    @Override
    public void configure(RuntimeHarness harness, File workingDir) throws Exception {
        char sep = File.separatorChar;
        String frascatiTestLibsPath = null;
        try {
            String testPath = workingDir.getAbsolutePath();
            frascatiTestLibsPath = new StringBuilder(testPath).append(sep).append("frascati").append(sep).append("lib").toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        File frascatiTestLibsDir = new File(frascatiTestLibsPath);

        if (!frascatiTestLibsDir.exists() && !frascatiTestLibsDir.mkdirs()) {
            log.warning("Enable to create the frascati lib directory");
            return;
        }
        File dot = new File("src");
        dot = dot.getAbsoluteFile().getParentFile();

        File nuxeoFrascati = searchDirectory(dot, null, "nuxeo-frascati");

        if (!nuxeoFrascati.exists()) {
            log.warning("Enable to retrieve the 'nuxeo-frascati' directory");
            return;
        }
        log.info("nuxeo-frascati directory found : " + nuxeoFrascati.getAbsolutePath());

        String home = workingDir.getAbsolutePath();
        log.info("Default environment home path: " + home);

        File frascatiConfigDir = new File(new StringBuilder(home).append(sep).append("config").toString());

        log.info("Default frascati configuration directory path: " + frascatiConfigDir);

        if (!frascatiConfigDir.exists() && !frascatiConfigDir.mkdir()) {
            log.warning("Enable to create the config directory");
            return;
        }
        File frascatiDir = new File(new StringBuilder(nuxeoFrascati.getAbsolutePath()).append(sep).append("resources").append(sep).append("frascati").toString());

        File configFileSrc = new File(new StringBuilder(frascatiDir.getAbsolutePath()).append(sep).append("config").append(sep).append("frascati_boot.properties").toString());

        File configFileDst = new File(new StringBuilder(frascatiConfigDir.getAbsolutePath()).append(sep).append("frascati_boot.properties").toString());
        try {
            FileUtils.copy(configFileSrc, configFileDst);
            log.config(configFileDst + " copied");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File frascatiLibsDir = new File(new StringBuilder(frascatiDir.getAbsolutePath()).append(File.separatorChar).append("lib").toString());

        File[] libs = frascatiLibsDir.listFiles();

        for (File srclib : libs) {
            String libName = srclib.getName();
            File destlib = new File(new StringBuilder(frascatiTestLibsPath).append(sep).append(libName).toString());
            try {
                FileUtils.copy(srclib, destlib);
                log.config(destlib + " copied");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Search for the directory which name is passed on as a parameter. The
     * first occurrence is returned
     * 
     * @param basedir
     *            the directory from where start the research
     * @param caller
     *            the directory from where comes the research
     * @param directoryName
     *            the searched directory's name
     * @return the directory if it has been found
     */
    private File searchDirectory(File basedir, final File caller, final String directoryName) {

        File[] children = basedir.listFiles(new FileFilter() {

            public boolean accept(File f) {
                if (f.isDirectory() && (caller == null || !(caller.getAbsolutePath().equals(f.getAbsoluteFile().getAbsolutePath())))) {
                    return true;
                }
                return false;
            }
        });
        for (File child : children) {
            if (directoryName.equals(child.getName())) {
                return child.getAbsoluteFile();
            }
            File c = searchDirectory(child.getAbsoluteFile(), basedir, directoryName);
            if (c != null) {
                return c.getAbsoluteFile();
            }
        }
        if (caller == null || !caller.getAbsolutePath().equals(basedir.getAbsoluteFile().getParentFile().getAbsolutePath())) {
            File c = searchDirectory(basedir.getParentFile(), basedir, directoryName);
            if (c != null) {
                return c.getAbsoluteFile();
            }
        }
        return null;
    }

}