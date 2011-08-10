
package fr.inria.galaxy.j1.scenario1;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.2.1
 * Tue May 26 17:11:22 CEST 2009
 * Generated source version: 2.2.1
 * 
 */

public final class TripPortType_TripPort_Client {

    private static final QName SERVICE_NAME = new QName("http://scenario1.j1.galaxy.inria.fr/", "Trip");

    private TripPortType_TripPort_Client() {
    }

    public static void main(String args[]) throws Exception {
        URL wsdlURL = Trip.WSDL_LOCATION;
        if (args.length > 0) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        Trip ss = new Trip(wsdlURL, SERVICE_NAME);
        TripPortType port = ss.getTripPort();  
        
        {
        System.out.println("Invoking process...");
        java.lang.String _process_arg0 = "";
        java.lang.String _process_arg1 = "";
        double _process_arg2 = 0.0;
        java.lang.String _process__return = port.process(_process_arg0, _process_arg1, _process_arg2);
        System.out.println("process.result=" + _process__return);


        }

        System.exit(0);
    }

}
