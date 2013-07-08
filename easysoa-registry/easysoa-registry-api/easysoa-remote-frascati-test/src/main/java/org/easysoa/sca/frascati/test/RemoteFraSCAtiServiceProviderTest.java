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
package org.easysoa.sca.frascati.test;

import java.io.File;
import java.io.FileFilter;
import java.util.logging.Logger;

import org.easysoa.sca.frascati.RemoteFraSCAtiServiceProvider;

public class RemoteFraSCAtiServiceProviderTest {
    protected final Logger log = Logger
            .getLogger(getClass().getCanonicalName());

    protected File remoteFrascatiLibDir;

    /**
     * Define the 'org.easysoa.remote.frascati.libraries.basedir' system
     * property using the easysoa-frascati-remote's resources directory
     */
    protected void configure()
    {
        char sep = File.separatorChar;
        File dot = new File("src");
        dot = dot.getAbsoluteFile().getParentFile();

        remoteFrascatiLibDir = searchDirectory(dot, null,"easysoa-remote-frascati");

        if (!remoteFrascatiLibDir.exists())
        {
            log.warning("Enable to retrieve the 'easysoa-remote-frascati' directory");
            remoteFrascatiLibDir = null;
            return;
        }

        log.info("easysoa-remote-frascati directory found : "
                + remoteFrascatiLibDir.getAbsolutePath());

        StringBuilder libs = new StringBuilder(
                remoteFrascatiLibDir.getAbsolutePath()).append(sep).append(
                        "resources").append(sep).append("frascati").append(
                                sep).append("lib");

        String remoteFrascatiLibDirPath = libs.toString();
        remoteFrascatiLibDir = new File(remoteFrascatiLibDirPath);

        log.info("Remote FraSCAti libraries directory : "
                + remoteFrascatiLibDirPath );

        System.setProperty(
                RemoteFraSCAtiServiceProvider.REMOTE_FRASCATI_LIBRARIES_BASEDIR,
                remoteFrascatiLibDirPath);
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
    private File searchDirectory(File basedir, final File caller,
            final String directoryName)
    {
        File[] children = basedir.listFiles(new FileFilter()
        {

            public boolean accept(File f)
            {

                if (f.isDirectory()
                        && (caller == null || !(caller.getAbsolutePath()
                                .equals(f.getAbsoluteFile().getAbsolutePath()))))
                {
                    return true;
                }
                return false;
            }
        });
        if(children == null){
            children = new File[0];
        }
        for (File child : children)
        {
            if (directoryName.equals(child.getName()))
            {
                return child.getAbsoluteFile();
            }
            File c = searchDirectory(child.getAbsoluteFile(), basedir,
                    directoryName);
            if (c != null)
            {
                return c.getAbsoluteFile();
            }
        }
        if (caller == null
                || !caller.getAbsolutePath().equals(
                        basedir.getAbsoluteFile().getParentFile()
                                .getAbsolutePath()))
        {
            File c = searchDirectory(basedir.getParentFile(), basedir,
                    directoryName);
            if (c != null)
            {
                return c.getAbsoluteFile();
            }
        }
        return null;
    }

}
