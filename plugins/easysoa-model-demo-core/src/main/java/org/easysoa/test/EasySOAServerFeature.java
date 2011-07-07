package org.easysoa.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.runtime.test.WorkingDirectoryConfigurator;
import org.nuxeo.runtime.test.runner.Defaults;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;
import org.nuxeo.runtime.test.runner.RuntimeFeature;
import org.nuxeo.runtime.test.runner.RuntimeHarness;
import org.nuxeo.runtime.test.runner.SimpleFeature;


/**
 * Allows for easy testing of EasySOA Core non-UI code
 * see http://doc.nuxeo.com/display/CORG/Unit+Testing
 * 
 * Use it by annotating a junit test class, ex. :
 * 
 * @RunWith(FeaturesRunner.class)
 * @Features(EasySOAServerFeature.class)
 * @Jetty(port=9980)
 * //@Jetty(config="/home/mdutoo/dev/easysoa/nuxeo-dm-5.3.2-jetty/config/jetty.xml") //,port=9980
 * 
 * @author mdutoo
 *
 */
@Deploy({
	"org.nuxeo.runtime.jetty"
})
@Features(EasySOAFeatureBase.class)
public class EasySOAServerFeature extends SimpleFeature implements WorkingDirectoryConfigurator {

    @Override
    public void initialize(FeaturesRunner runner) throws Exception {
    	super.initialize(runner);
    	
        Jetty jetty = FeaturesRunner.getScanner().getFirstAnnotation(runner.getTargetTestClass(), Jetty.class);
        if (jetty == null) {
            jetty = Defaults.of(Jetty.class);
        }
        int p = jetty.port();
        if (p > 0) {
            System.setProperty("jetty.port", Integer.toString(p));
        }
        String v = jetty.host();
        if (v.length() > 0) {
            System.setProperty("jetty.host", jetty.host());
        }
        v = jetty.config();
        if (v.length() > 0) {
            System.setProperty("org.nuxeo.jetty.config", jetty.config());
        }
        runner.getFeature(RuntimeFeature.class).getHarness().addWorkingDirectoryConfigurator(this);
    }

    public void configure(RuntimeHarness harness, File workingDir) throws IOException {
        File dest = new File(workingDir, "config");
        dest.mkdirs();

        InputStream in = getResource("jetty/default-web.xml").openStream();
        dest = new File(workingDir + "/config", "default-web.xml");
        try {
            FileUtils.copyToFile(in, dest);
        } finally {
            in.close();
        }

        in = getResource("jetty/jetty.xml").openStream();
        dest = new File(workingDir + "/config", "jetty.xml");
        try {
            FileUtils.copyToFile(in, dest);
        } finally {
            in.close();
        }
    }

    private static URL getResource(String resource) {
        //return Thread.currentThread().getContextClassLoader().getResource(resource);
        return Jetty.class.getClassLoader().getResource(resource);
    }


}
