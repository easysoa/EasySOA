package org.easysoa.samples.axxx;

import java.io.IOException;

import org.easysoa.discovery.mock.MockRepository;
import org.easysoa.discovery.rest.client.DiscoveryRequest;
import org.easysoa.discovery.rest.model.Deliverable;
import org.easysoa.discovery.rest.model.Endpoint;
import org.easysoa.discovery.rest.model.Environment;
import org.easysoa.discovery.rest.model.ServiceImpl;
import org.easysoa.discovery.rest.model.SoaNode;
import org.easysoa.discovery.rest.model.SoaSystem;
import org.easysoa.discovery.rest.model.Software;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AxxxDpsDcvCollaborationTest {
    
    private static MockRepository repository;
    private static DiscoveryRequest request;
    
    private static Environment stagingEnvironment;
    private static Environment devDpsEnvironment;
    
    private static Software dcvCrmSoftware;
    private static Software dpsApvSoftware;

    private static SoaSystem axxxDcvCrmApplicationSystem;
    private static SoaSystem collaborationSoaSystem;
    private static SoaSystem collaborationBusinessRequirementsSystem;
    private static SoaSystem collaborationArchitectureRequirementsSystem;
    private static SoaNode dpsTestSoapuiSystem;
    
    private static Endpoint clientServiceEndpoint;
    private static Endpoint tdrServiceEndpoint;
    private static Endpoint clientServiceEndpoint2;
    private static Endpoint clientServiceMockDevDpsEndpoint;

    private static String clientServiceWsdl;
    private static String tdrServiceWsdl;

    private static Deliverable defaultDpsApvDeliverable;
    private static Deliverable defaultDcvCrmDeliverable;
    private static Deliverable dpsApvTdrDeliverable;
    private static ServiceImpl dpsApvTdrServiceImpl;
    
    
    @BeforeClass
    public static void setup() {
        repository = new MockRepository();
        request = new DiscoveryRequest(repository);
    }


    @Test
    public void dcvCrmDeveloperDoesWebDisco() throws IOException {
        
        // DCV : CRM developer does a (code) web discovery of ClientService
        stagingEnvironment = new Environment("staging", "0.1");
        clientServiceWsdl = "<wsdl/>";
        clientServiceEndpoint = new Endpoint(stagingEnvironment, "http://vmcrm/WS/ClientService.asmx", clientServiceWsdl, "0.1");
        clientServiceEndpoint.addRelation(stagingEnvironment); // TODO deployedDeliverable between
        axxxDcvCrmApplicationSystem = new SoaSystem("AXXX DCV CRM Application", "0.1");
        axxxDcvCrmApplicationSystem.addRelation(clientServiceEndpoint); // set in web discovery ui ;TODO rather production endpoint ?!
        // also serviceImpl / deployable, deployedDeployable ??
        // save model...
        // version ?
        request.addDiscoveryNotification(stagingEnvironment, clientServiceEndpoint, axxxDcvCrmApplicationSystem);
        System.out.println("\ndcvCrmDeveloperDoesWebDisco:");
    }
    
    @Test
    public void dcpOrDpsCompletesDesign() throws IOException {
        // DCP (or DPS) completes design, MAYBE also of consumption
        collaborationBusinessRequirementsSystem = new SoaSystem("AXXX DPS DCV Collaboration BusinessProcess", "0.1");
        // also add requirements...
        collaborationArchitectureRequirementsSystem = new SoaSystem("AXXX DPS DCV Collaboration Architecture", "0.1");
        dcvCrmSoftware = new Software("DCV CRM", "0.1");
        collaborationArchitectureRequirementsSystem.addRelation(dcvCrmSoftware); // dcvCrmSoftware belongs to architecture
        collaborationBusinessRequirementsSystem.addRelation(dcvCrmSoftware); // dcvCrmSoftware contributes to collaborationBusinessProcessSystem

        collaborationArchitectureRequirementsSystem.addRelation(clientServiceEndpoint);
        dcvCrmSoftware.addRelation(clientServiceEndpoint); // TODO rather architectureRequirements
        
        collaborationSoaSystem = new SoaSystem("AXXX DPS DCV Collaboration SOA", "0.1");
        collaborationSoaSystem.addRelation(clientServiceEndpoint); // TODO rather production endpoint ?!
        
        request.addDiscoveryNotification(collaborationBusinessRequirementsSystem, collaborationArchitectureRequirementsSystem,
                dcvCrmSoftware, collaborationSoaSystem);
        System.out.println("\ndcpOrDpsCompletesDesign:");
    }
    
    @Test
    public void dpsDesignsConsumptionAndExposition() throws IOException {
        
        // DPS : models consumption and exposition of ClientService by (ESB) APV by (jwt process &) sca import i.e. reqs, software, endpoints
        //Environment productionEnvironment = new Environment("prod", "0.1"); // TODO later copy to prod env
        tdrServiceWsdl = "<wsdl/>";
        tdrServiceEndpoint = new Endpoint(stagingEnvironment, "http://vmapv/cxf/TdrService", tdrServiceWsdl, "0.1");
        tdrServiceEndpoint.addRelation(stagingEnvironment); // TODO deployedDeliverable between
        
        dpsApvSoftware = new Software("DPS APV", "0.1");
        collaborationArchitectureRequirementsSystem.addRelation(dpsApvSoftware); // dpsApvSoftware belongs to architecture
        collaborationBusinessRequirementsSystem.addRelation(dpsApvSoftware); // dpsApvSoftware contributes to collaborationBusinessProcessSystem

        collaborationArchitectureRequirementsSystem.addRelation(tdrServiceEndpoint);
        dpsApvSoftware.addRelation(tdrServiceEndpoint); // TODO rather architectureRequirements
        
        collaborationSoaSystem.addRelation(tdrServiceEndpoint); // TODO rather production endpoint ?!
        
        defaultDpsApvDeliverable = new Deliverable("defaultDpsApvDeliverable", "defaultDpsApvDeliverable", "0.1", null);
        dpsApvSoftware.addRelation(defaultDpsApvDeliverable); // modeling implementation
        defaultDpsApvDeliverable.addRelation(clientServiceEndpoint); // modeling architectural consumption
        
        request.addDiscoveryNotification(tdrServiceEndpoint, dpsApvSoftware,
                collaborationBusinessRequirementsSystem, collaborationArchitectureRequirementsSystem,
                collaborationSoaSystem, defaultDpsApvDeliverable);
        System.out.println("\ndpsDesignsConsumptionAndExposition:");
    }
    
    @Test
    public void dcvDesignsConsumption() throws IOException {
        // DCV : maybe only now design consumption of CRM Client Service : download, modify and reimport sca (not code discovery because .NET) 
        defaultDcvCrmDeliverable = new Deliverable("defaultDcvCrmDeliverable", "defaultDcvCrmDeliverable", "0.1", null);
        dcvCrmSoftware.addRelation(defaultDcvCrmDeliverable); // modeling implementation
        defaultDcvCrmDeliverable.addRelation(tdrServiceEndpoint); // modeling architectural consumption

        request.addDiscoveryNotification(defaultDcvCrmDeliverable, dcvCrmSoftware);
        System.out.println("\ndcvDesignsConsumption:");
    }
    
    @Test
    public void dpsDiscoversCodeThenWeb() throws IOException {
        
        // DPS : (ESB) APV developer does a code discovery of Client Service consumption & exposition
        
        // update existing default deliverable, fill with discovered serviceimpl
        dpsApvTdrDeliverable = defaultDpsApvDeliverable; // TODO case of more than 1 actual deliverable
        dpsApvTdrDeliverable.setId("Deliverable=org.easysoa.sample.axxx.dps:easysoa-sample-axxx-apv-tdr");
        dpsApvTdrDeliverable.setName("0.1-SNAPSHOT");
        dpsApvTdrDeliverable.setVersion("0.2");
        dpsApvTdrServiceImpl = new ServiceImpl(dpsApvTdrDeliverable, "JAX-WS", "org.easysoa.sample.axxx.dps.apv.TdrServiceImpl", "TdrServiceImpl");
        dpsApvTdrDeliverable.addRelation(dpsApvTdrServiceImpl);
        dpsApvTdrDeliverable.addRequirement("Consumes WS of JAX-WS interface org.easysoa.samples.axxx.dcv.ClientService");
        
        // create / find service & link them
        // TODO both services with same wsdl (ClientService's), or TdrService is different but where comes his wsdl from (java2wsdl ?) ?
        
        // web disco
        tdrServiceEndpoint.setWsdl(tdrServiceWsdl); // TODO store actual wsdl in another endpoint (ex. in "design" environment ??)

        request.addDiscoveryNotification(dpsApvTdrDeliverable, dpsApvTdrServiceImpl);
        System.out.println("\ndpsDiscoversCode:");
    }
    
    @Test
    public void dcvChangesServiceImpl() throws IOException {

        // DCV : changes code (not discovered) and therefore actual service interface, detected in web discovery 
        
        // case 1 : same service, different wsdl, LATER other correlation,kinds
        // clientServiceEndpoint = findEndpoint(serviceName)
        String clientService2Wsdl = "<wsdl>another</wsdl>";
        clientServiceEndpoint.setWsdl(clientService2Wsdl);
        clientServiceEndpoint.setVersion("0.2");
        // on revalidation, alerts DPS about inconsistency : (TODO Q how does the DCV change propagate to DPS ??)
        // code discovery :
        // code alt 1 : if it doesn't compile anymore (assuming dpsApvTdrServiceImpl's consumed ClientService interface is generated from wsdl auto by maven TODO), error("doesn't compile anymore ; changes : ClientService.java changed")
        // code alt 2 : if (it's not a backward compatible change and) dpsApvTdrServiceImpl's consumed ClientService.java interface doesn't change, error("should have changed")
        // if (dpsApvTdrServiceImpl.getRequirements().get(0).contains("")) {}
        // web discovery (in staging) :
        if (!tdrServiceEndpoint.getWsdl().equals(tdrServiceEndpoint.getWsdl())) { System.out.println("Error : web disco'd impl wsdl differs from the design one");} // TODO another endpoint (ex. in "design" environment ??)
        
        // case 2 : different service (i.e. can't be correlated)
        // webdisco.reinit(); // TODO !
        clientServiceEndpoint2 = new Endpoint(stagingEnvironment, "http://vmcrm/WS/ClientService.asmx", clientServiceWsdl, "0.1");
        clientServiceEndpoint2.addRelation(stagingEnvironment); // TODO deployedDeliverable between
        axxxDcvCrmApplicationSystem.addRelation(clientServiceEndpoint); // set in web discovery ui ;TODO rather production endpoint ?!
        //if (clientService has no more endpoint in staging) { error("Staging : no (more) endpoint for clientService in staging. Not ready to version.")}
        //if (clientServiceEndpoint2 has no related service) { warn("New service, do you want : to link it with existing [suggestions], or to put it in SOAs [SOA systems] ?"); }

        request.addDiscoveryNotification(clientServiceEndpoint, clientServiceEndpoint2, axxxDcvCrmApplicationSystem);
        System.out.println("\ndcvChangesServiceImpl:");
    }
    
    @Test
    public void dpsRediscoversOldClientServiceInDevSoapui() throws IOException {
        devDpsEnvironment = new Environment("devDps", "0.1");
        clientServiceMockDevDpsEndpoint = new Endpoint(devDpsEnvironment, "http://vmcrm/WS/ClientService.asmx", clientServiceWsdl, "0.1");
        clientServiceMockDevDpsEndpoint.addRelation(devDpsEnvironment); // TODO deployedDeliverable between
        dpsTestSoapuiSystem = new SoaSystem("dpsTestSoapui", "0.1");
        dpsTestSoapuiSystem.addRelation(clientServiceMockDevDpsEndpoint); // set in web discovery ui ;TODO rather production endpoint ?!

        // on revalidation, alerts DPS about inconsistency :
        // web discovery (in dev) :
        if (!clientServiceMockDevDpsEndpoint.getWsdl().equals(clientServiceEndpoint.getWsdl())) { System.out.println("Error : web disco'd test wsdl differs from the design one");}
        
        request.addDiscoveryNotification(devDpsEnvironment, clientServiceMockDevDpsEndpoint, dpsTestSoapuiSystem);
        System.out.println("\ndpsRediscoversOldClientServiceInDev:");
    }
    
    @After
    public void postTrace() throws IOException  {
        request.send();
        repository.traceRepository();
    }
    
}
