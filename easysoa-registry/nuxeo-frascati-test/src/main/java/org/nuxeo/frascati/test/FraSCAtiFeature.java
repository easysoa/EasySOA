package org.nuxeo.frascati.test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.frascati.api.FraSCAtiServiceItf;
import org.nuxeo.runtime.test.WorkingDirectoryConfigurator;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.RuntimeFeature;
import org.nuxeo.runtime.test.runner.RuntimeHarness;
import org.nuxeo.runtime.test.runner.SimpleFeature;

@Features(RuntimeFeature.class)
@Deploy({
	"org.nuxeo.runtime.bridge",
	"org.nuxeo.frascati"
})
public class FraSCAtiFeature extends SimpleFeature implements WorkingDirectoryConfigurator {
	
	 	protected static final Logger log = Logger.getLogger(
	 			FraSCAtiFeature.class.getCanonicalName());
	    
	    protected FraSCAtiServiceItf frascatiService;	   
		
	    public void initialize(FeaturesRunner runner) {
	    	runner.getFeature(RuntimeFeature.class).getHarness().addWorkingDirectoryConfigurator(this);	    	
	    }

		@Override
		public void configure(RuntimeHarness harness, File workingDir)
				throws Exception {
			
	    	char sep = File.separatorChar;	
	    	String frascatiTestLibsPath = null;
	    	try{
		    	String testPath = workingDir.getAbsolutePath();			    	
		    	frascatiTestLibsPath = new StringBuilder(testPath).append(sep).append(
		    			"frascati").append(sep).append("lib").toString();
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	File frascatiTestLibsDir = new File(frascatiTestLibsPath);
	    	
	    	if(!frascatiTestLibsDir.exists()){
	        	
	    		frascatiTestLibsDir.mkdirs();

		    	String home = workingDir.getAbsolutePath();	
		    	log.info("Default environment home path: " + home);
		    	
		    	File frascatiConfigDir = new File(new StringBuilder(home).append(sep).append("config").toString());
		    	frascatiConfigDir.mkdir();	
		    	
		    	File configFileSrc = new File("src/test/resources/frascati_boot.properties");
		    	File configFileDst = new File(new StringBuilder(frascatiConfigDir.getAbsolutePath()).append(
		    					sep).append("frascati_boot.properties").toString());

		    	try {
					FileUtils.copy(configFileSrc,configFileDst);
				} catch (IOException e) {
					e.printStackTrace();
				}
		    	
		    	File frascatiLibsDir = new File("./src/test/resources/frascati/lib");
		    	File[] libs = frascatiLibsDir.listFiles();	
		    	
		    	for(File srclib : libs){
		    		String libName = srclib.getName();
		    		File destlib = new File(new StringBuilder(frascatiTestLibsPath).append(
		    				sep).append(libName).toString());
		    		try {
						FileUtils.copy(srclib,destlib);
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	}		    	
	    	}  		
		}
	 	    
}
