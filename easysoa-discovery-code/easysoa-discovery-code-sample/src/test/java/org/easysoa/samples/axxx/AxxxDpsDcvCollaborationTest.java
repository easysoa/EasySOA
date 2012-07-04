package org.easysoa.samples.axxx;

import java.io.IOException;

import org.easysoa.discovery.mock.MockRepository;
import org.easysoa.discovery.rest.client.DiscoveryRequest;
import org.easysoa.discovery.rest.model.Deliverable;
import org.easysoa.discovery.rest.model.Endpoint;
import org.easysoa.discovery.rest.model.Environment;
import org.easysoa.discovery.rest.model.ServiceImpl;
import org.easysoa.discovery.rest.model.SoaSystem;
import org.easysoa.discovery.rest.model.Software;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AxxxDpsDcvCollaborationTest {
    
    private static MockRepository repository;
    private static DiscoveryRequest request;
    
    private static Environment stagingEnvironment;
    
    private static Software dcvCrmSoftware;
    
    private static SoaSystem collaborationSoaSystem;
    private static SoaSystem collaborationBusinessRequirementsSystem;
    private static SoaSystem collaborationArchitectureRequirementsSystem;
    
    private static Endpoint clientServiceEndpoint;
    private static Endpoint tdrServiceEndpoint;

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
        String clientServiceWsdl = "<wsdl/>";
        clientServiceEndpoint = new Endpoint(stagingEnvironment, "http://vmcrm/WS/ClientService.asmx", clientServiceWsdl, "0.1");
        clientServiceEndpoint.addRelation(stagingEnvironment); // TODO deployedDeliverable between
        SoaSystem axxxDcvCrmApplicationSystem = new SoaSystem("AXXX DCV CRM Application", "0.1");
        axxxDcvCrmApplicationSystem.addRelation(clientServiceEndpoint); // set in web discovery ui ;TODO rather production endpoint ?!
        // also serviceImpl / deployable, deployedDeployable ??
        // save model...
        // version ?
        request.addDiscoveryNotification(stagingEnvironment, clientServiceEndpoint, axxxDcvCrmApplicationSystem);
        System.out.println("\ndcvCrmDeveloperDoesWebDisco:");
        request.send();
        repository.traceRepository();
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
        request.send();
        repository.traceRepository();
    }
    
    @Test
    public void dpsDesignsConsumptionAndExposition() throws IOException {
        
        // DPS : models consumption and exposition of ClientService by (ESB) APV by (jwt process &) sca import i.e. reqs, software, endpoints
        //Environment productionEnvironment = new Environment("prod", "0.1"); // TODO later copy to prod env
        String tdrServiceWsdl = "<wsdl/>";
        tdrServiceEndpoint = new Endpoint(stagingEnvironment, "http://vmapv/cxf/TdrService", tdrServiceWsdl, "0.1");
        tdrServiceEndpoint.addRelation(stagingEnvironment); // TODO deployedDeliverable between
        
        Software dpsApvSoftware = new Software("DPS APV", "0.1");
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
        request.send();
        repository.traceRepository();
    }
    
    @Test
    public void dcvDesignsConsumption() throws IOException {
        // DCV : maybe only now design consumption of CRM Client Service : download, modify and reimport sca (not code discovery because .NET) 
        defaultDcvCrmDeliverable = new Deliverable("defaultDcvCrmDeliverable", "defaultDcvCrmDeliverable", "0.1", null);
        dcvCrmSoftware.addRelation(defaultDcvCrmDeliverable); // modeling implementation
        defaultDcvCrmDeliverable.addRelation(tdrServiceEndpoint); // modeling architectural consumption

        request.addDiscoveryNotification(defaultDcvCrmDeliverable, dcvCrmSoftware);
        System.out.println("\ndcvDesignsConsumption:");
        request.send();
        repository.traceRepository();
    }
    
    @Test
    public void dpsDiscoversCode() throws IOException {
        
        // DPS : (ESB) APV developer does a code discovery of Client Service consumption & exposition
        
        // update existing default deliverable, fill with discovered serviceimpl
        dpsApvTdrDeliverable = defaultDpsApvDeliverable; // TODO case of more than 1 actual deliverable
        dpsApvTdrDeliverable.setId("Deliverable=org.easysoa.sample.axxx.dps:easysoa-sample-axxx-apv-tdr");
        dpsApvTdrDeliverable.setName("0.1-SNAPSHOT");
        dpsApvTdrServiceImpl = new ServiceImpl(dpsApvTdrDeliverable, "JAX-WS", "org.easysoa.sample.axxx.dps.apv.TdrServiceImpl", "TdrServiceImpl");
        dpsApvTdrDeliverable.addRelation(dpsApvTdrServiceImpl);
        
        // create / find service & link them
        // TODO both services with same wsdl (ClientService's), or TdrService is different but where comes his wsdl from (java2wsdl ?) ?

        request.addDiscoveryNotification(dpsApvTdrDeliverable, dpsApvTdrServiceImpl);
        System.out.println("\ndpsDiscoversCode:");
        request.send();
        repository.traceRepository();
    }
    
    @Test
    public void dcvChangesServiceImpl() throws IOException {

        // DCV : changes serviceimpl, which changes actual service interface TODO detect api changes
        

    }
    
    @AfterClass
    public static void postTrace() throws IOException  {
        request.send();
        repository.traceRepository();
    }
    
}
