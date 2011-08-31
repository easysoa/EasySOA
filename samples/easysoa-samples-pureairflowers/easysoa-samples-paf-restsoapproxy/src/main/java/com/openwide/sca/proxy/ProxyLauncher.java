package com.openwide.sca.proxy;


public class ProxyLauncher {

	//private Map<String, String> systemProperties;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if(args == null || args.length == 0 || args[0] == null || "".equals(args[0])){
			System.out.println("Composite file name must be passed as first parameter !");
			System.exit(-1); 
		}
		String composite = args[0];
		System.out.println("Composite to load : " + composite + "...");
	    // Configure Java system properties.
	    System.setProperty( "fscript-factory", "org.ow2.frascati.fscript.jsr223.FraSCAtiScriptEngineFactory" );
	    //if(systemProperties != null) {
	      //getLog().debug("Configuring Java system properties.");
	      //Properties oldSystemProperties = System.getProperties();
	      //Properties newSystemProperties = new Properties(oldSystemProperties);
	      //newSystemProperties.putAll(systemProperties);
	      //System.setProperties(newSystemProperties);
	      //getLog().debug(newSystemProperties.toString());
	    //}  
		try{
			System.out.println("Frascati is starting...");
			//FraSCAti frascati = FraSCAti.newFraSCAti();
			//Launcher launcher = new Launcher(composite, frascati);
			//launcher.launch();
		    System.out.println("FraSCAti is running in a server mode...");
		    System.out.println("Press Ctrl+C to quit...");
	        System.in.read();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		System.out.println("FraSCAti is stopped...");
	}

}
