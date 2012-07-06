package org.easysoa.samples.axxx;

import java.io.IOException;

import org.easysoa.discovery.mock.MockRepository;
import org.easysoa.discovery.rest.client.DiscoveryRequest;
import org.easysoa.discovery.rest.model.Deliverable;
import org.easysoa.discovery.rest.model.Endpoint;
import org.easysoa.discovery.rest.model.Environment;
import org.easysoa.discovery.rest.model.Service;
import org.easysoa.discovery.rest.model.ServiceImpl;
import org.easysoa.discovery.rest.model.SoaNode;
import org.easysoa.discovery.rest.model.SoaSystem;
import org.easysoa.discovery.rest.model.Software;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class AxxxDpsDcvCollaborationTest {
    
    private static MockRepository repository;
    private static DiscoveryRequest request;
    
    private static Environment stagingEnvironment;
    private static Environment dpsDevTest1Environment;
    
    private static Software dcvCrmSoftware;
    private static Software dpsApvSoftware;

    private static SoaSystem axxxDcvCrmApplicationSystem;
    private static SoaSystem axxxDpsApvApplicationSystem;
    private static SoaSystem collaborationSoaSystem;
    private static SoaSystem collaborationBusinessRequirementsSystem;
    private static SoaSystem collaborationArchitectureRequirementsSystem;
    private static SoaNode dpsTestSoapuiSystem;

    private static Service clientService;
    private static Service tdrService;
    
    private static Endpoint clientServiceEndpoint;
    private static Endpoint tdrServiceEndpoint;
    private static Endpoint clientServiceEndpoint2;
    private static Endpoint dpsDevTest1TdrServiceEndpoint;
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
        // TODO new AxxxSoa for multitenancy
        
        // DCV : CRM developer does a web discovery of ClientService
        // (in axxx staging env ("where is it ?"), axxxDcvCrm physical application by default ("what provides it ?") & collaboration "logical" target / SOA => services serve to collaboration ("what is it useful for ?"))
        // NB. must say what is its target ("what is it useful for ?") to create services from endpoints NOOOOOOOOOOOOO its application
        stagingEnvironment = new Environment("staging", "0.1");
        clientServiceWsdl = "<wsdl/>";
        clientServiceEndpoint = new Endpoint(stagingEnvironment, "http://vmcrm/WS/ClientService.asmx", clientServiceWsdl, "0.1");
        clientServiceEndpoint.addRelation(stagingEnvironment); // TODO add default deployedDeliverable between
        axxxDcvCrmApplicationSystem = new SoaSystem("AXXX DCV CRM Application", "0.1"); // TODO provided by DCV CRM
        ////axxxDcvCrmApplicationSystem.addRelation(clientServiceEndpoint); // set in web discovery ui ; TODO rather production endpoint ?!
        
        // at disco end (disco save / approve) (or in validation / correlation dashboard :)
        // "some environment elements (endpoints) are not propertized, we suggest you do"
        //stagingEnvironment.setParameter("vmcrm", "vmcrm"); // TODO (or on deployedDeliverable ??)
        //AxxxSoa.addParameter("vmcrm"); // or auto when previous line
        clientServiceEndpoint.setUrl("http://${vmcrm}/WS/ClientService.asmx");
        
        // target env design (or below) : "is it the target env ?" yes ! then :
        // auto add some requirements gotten from itself (wsdl)
        clientServiceEndpoint.getRequirements().add("wsdl=" + clientServiceWsdl);
        // (NB. if disco in dcvDev env first, not the target env, so "transition / fork & design it" (or after publish, only in collab pov)
        // service auto creation in target soa (rather than "there is no (matched) service yet, do you want to create them ?")
        clientService = new Service("ClientService", "0.1");
        clientService.addRelation(clientServiceEndpoint); // TODO add default serviceimpl between
        axxxDcvCrmApplicationSystem.addRelation(clientService); // set in web discovery ui
        
        // approves disco'd model by (minor) version / snapshot (or may be autosnapshotted before next disco)

        // NB. target env (staging) plays the role of requirements / expected model
        // so to see completion progress of collab, display your work in the context of your forked collab ("what's not there yet in dps (dcv)")
        // ex. for each endpoint : impl (can be seen in code, tests), (dev or ci) test envs of this impl (can be seen by web disco...), its depl in other envs, its depl here (web disco)...
        
        request.addDiscoveryNotification(stagingEnvironment, clientServiceEndpoint, axxxDcvCrmApplicationSystem, clientService);
        System.out.println("\ndcvCrmDeveloperDoesWebDisco:");
    }
    
    @Test
    public void dcpOrDpsCompletesDesign() throws IOException {
        // TODO how to share systems between requirements and actual ??
        
        // DCP (or DPS) completes design, MAYBE also of consumption
        collaborationBusinessRequirementsSystem = new SoaSystem("AXXX DPS DCV Collaboration BusinessProcess", "0.1"); // also add business requirements documents in it
        collaborationArchitectureRequirementsSystem = new SoaSystem("AXXX DPS DCV Collaboration Architecture", "0.1"); // also add architecture requirements documents in it
        dcvCrmSoftware = new Software("DCV CRM", "0.1");
        collaborationArchitectureRequirementsSystem.addRelation(dcvCrmSoftware); // dcvCrmSoftware belongs to architecture
        collaborationBusinessRequirementsSystem.addRelation(dcvCrmSoftware); // dcvCrmSoftware contributes to collaborationBusinessProcessSystem

        collaborationArchitectureRequirementsSystem.addRelation(clientService); // ?
        dcvCrmSoftware.addRelation(clientService); // TODO rather architectureRequirements
        
        // publishes (TODO major version of) model to SOA collaboration space (or / and gives read rights to DPS)
        // TODO LATER say to those referencing the previous one that they have to update / auto update 
        collaborationSoaSystem = new SoaSystem("AXXX DPS DCV Collaboration SOA", "0.1");
        collaborationSoaSystem.addRelation(stagingEnvironment); // models staging as "target env"
        collaborationSoaSystem.addRelation(axxxDcvCrmApplicationSystem); // defines perimeter of sharing / publishing to collaboration // TODO or only softwares / collaborationArchitecture ??
        
        request.addDiscoveryNotification(collaborationBusinessRequirementsSystem, collaborationArchitectureRequirementsSystem,
                dcvCrmSoftware, collaborationSoaSystem);
        System.out.println("\ndcpOrDpsCompletesDesign:");
    }
    
    @Test
    public void dpsDesignsConsumptionAndExposition() throws IOException {
        
        // DPS : models consumption and exposition of ClientService by (ESB) APV by (jwt process &) sca import i.e. reqs, software, endpoints WITHOUT wsdl
        // (in axxx staging env & collaboration SOA)
        //Environment productionEnvironment = new Environment("prod", "0.1"); // TODO later copy to prod env
        tdrServiceEndpoint = new Endpoint(stagingEnvironment, "http://vmapv/cxf/TdrService", null, "0.1");
        tdrServiceEndpoint.addRelation(stagingEnvironment); // TODO deployedDeliverable between

        // service auto creation (rather than "there is no (matched) service yet, do you want to create them ?")
        tdrService = new Service("TdrService", "0.1");
        tdrService.addRelation(tdrServiceEndpoint); // TODO add default serviceimpl between
        
        axxxDpsApvApplicationSystem = new SoaSystem("AXXX DPS APV Application", "0.1"); // TODO provided by DPS
        axxxDpsApvApplicationSystem.addRelation(tdrService); // set in web discovery ui
        
        dpsApvSoftware = new Software("DPS APV", "0.1");
        collaborationArchitectureRequirementsSystem.addRelation(dpsApvSoftware); // dpsApvSoftware belongs to architecture
        collaborationBusinessRequirementsSystem.addRelation(dpsApvSoftware); // dpsApvSoftware contributes to collaborationBusinessProcessSystem

        collaborationArchitectureRequirementsSystem.addRelation(tdrService); // ?
        dpsApvSoftware.addRelation(tdrService); // TODO rather architectureRequirements

        defaultDpsApvDeliverable = new Deliverable("defaultDpsApvDeliverable", "defaultDpsApvDeliverable", "0.1", null);
        dpsApvSoftware.addRelation(defaultDpsApvDeliverable); // modeling implementation
        defaultDpsApvDeliverable.addRelation(clientService); // modeling architectural consumption

        collaborationSoaSystem.addRelation(axxxDpsApvApplicationSystem); // defines perimeter of sharing / publishing to collaboration // TODO or only softwares / collaborationArchitecture or even business ??
        
        // at disco end (disco save / approve) (or in validation / correlation dashboard) :
        // "some environment elements (endpoints) are not propertized, we suggest you do (and stored jwt / sca can be also)"
        //stagingEnvironment.setParameter("vmapv", "vmapv"); // TODO (or on deployedDeliverable ??)
        //AxxxSoa.addParameter("vmapv"); // or auto when previous line
        tdrServiceEndpoint.setUrl("http://${vmapv}/cxf/TdrService");
        
        // since target env :
        // auto add some requirements gotten from itself (wsdl)
        tdrServiceEndpoint.getRequirements().add("wsdl=" + "to be defined");
        
        // approves disco'd model by (minor) version / snapshot (or may be autosnapshotted before next disco)
        
        // publishes (TODO major version of) model to SOA collaboration space (or / and gives read rights to DPS)
        // TODO LATER say to those referencing the previous one that they have to update / auto update 
        
        request.addDiscoveryNotification(tdrServiceEndpoint, tdrService, axxxDpsApvApplicationSystem, dpsApvSoftware,
                collaborationBusinessRequirementsSystem, collaborationArchitectureRequirementsSystem,
                collaborationSoaSystem, defaultDpsApvDeliverable);
        System.out.println("\ndpsDesignsConsumptionAndExposition:");
    }
    
    @Test
    public void dcvDesignsConsumption() throws IOException {
        // TOOD LATER updates / get collaboration model latest (major) version
        
        // DCV : maybe only now design consumption of CRM Client Service : download, modify and reimport sca (not code discovery because .NET) 
        defaultDcvCrmDeliverable = new Deliverable("defaultDcvCrmDeliverable", "defaultDcvCrmDeliverable", "0.1", null);
        dcvCrmSoftware.addRelation(defaultDcvCrmDeliverable); // modeling implementation
        defaultDcvCrmDeliverable.addRelation(tdrService); // modeling architectural consumption
        
        // publishes (TODO major version of) model to SOA collaboration space (or / and gives read rights to DPS)
        // TODO LATER say to those referencing the previous one that they have to update / auto update 

        request.addDiscoveryNotification(defaultDcvCrmDeliverable, dcvCrmSoftware);
        System.out.println("\ndcvDesignsConsumption:");
    }
    
    @Test
    public void dpsDiscoversCodeThenWeb() throws IOException {
        // TOOD LATER updates / get collaboration model latest (major) version
        
        // DPS : (ESB) APV developer does a code discovery of Client Service consumption & exposition
        
        // update existing default deliverable, fill with discovered serviceimpl
        dpsApvTdrDeliverable = defaultDpsApvDeliverable; // TODO case of more than 1 actual deliverable
        dpsApvTdrDeliverable.setId("Deliverable=org.easysoa.sample.axxx.dps:easysoa-sample-axxx-apv-tdr");
        dpsApvTdrDeliverable.setName("0.1-SNAPSHOT");
        dpsApvTdrDeliverable.setVersion("0.2");
        dpsApvTdrServiceImpl = new ServiceImpl(dpsApvTdrDeliverable, "JAX-WS", "org.easysoa.sample.axxx.dps.apv.TdrServiceImpl", "TdrServiceImpl");
        dpsApvTdrDeliverable.addRelation(dpsApvTdrServiceImpl);
        dpsApvTdrDeliverable.addRequirement("Consumes WS of JAX-WS interface org.easysoa.samples.axxx.dcv.ClientService");
        
        // in validation / correlation dashboard, find corresponding service & link them :
        tdrService.addRelation(dpsApvTdrServiceImpl);
        //// OBSOLETE Q both services with same wsdl (ClientService's), or TdrService is different but where comes his wsdl from (java2wsdl ?) ?

        // DCV : DPS developer does a web discovery of TdrService
        // (in dpsDevTest1 env ("where is it ?"), axxxDpsApv physical application by default ("what provides it ?") & collaboration "logical" target / SOA => services serve to collaboration ("what is it useful for ?"))
        // NB. must say what is its target ("what is it useful for ?") to create services from endpoints NOOOOOOOOOOOOO its application
        tdrServiceWsdl = "<wsdl/>";
        dpsDevTest1Environment = new Environment("dpsDevTest1", "0.1"); // with vmcrm & vmapv params since belongs to AxxxSoa
        dpsDevTest1TdrServiceEndpoint = new Endpoint(dpsDevTest1Environment, "http://${vmapv}/cxf/TdrService", tdrServiceWsdl, "0.1"); // auto prop'd with known params
        dpsDevTest1TdrServiceEndpoint.addRelation(dpsDevTest1Environment); // TODO add known deployedDeliverable between
        // since tdrService belongs to axxxDpsApvApplicationSystem which has been set in web discovery ui, and since WSDL matches with target env's (expected one), service can be auto found :
        tdrService.addRelation(dpsDevTest1TdrServiceEndpoint); // TODO add known serviceimpl between
        
        // contribute requirements from known :
        tdrServiceEndpoint.getRequirements().add("wsdl=" + dpsDevTest1TdrServiceEndpoint.getWsdl()); 
        
        // web disco in staging
        tdrServiceEndpoint.setWsdl(tdrServiceWsdl); // TODO store actual wsdl in another endpoint (ex. in "design" environment ??)
        
        // TODO DPS impl mock clientService, matches expected (gotten from target env) wsdl in dev !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        // publishes (TODO major version of) model to SOA collaboration space (or / and gives read rights to DPS)
        // TODO LATER say to those referencing the previous one that they have to update / auto update
        
        request.addDiscoveryNotification(dpsApvTdrDeliverable, dpsApvTdrServiceImpl, dpsDevTest1Environment, dpsDevTest1TdrServiceEndpoint);
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
        axxxDcvCrmApplicationSystem.addRelation(clientServiceEndpoint); // set in web discovery ui ; TODO rather production endpoint ?!
        //if (clientService has no more endpoint in staging) { error("Staging : no (more) endpoint for clientService in staging. Not ready to version.")}
        //if (clientServiceEndpoint2 has no related service) { warn("New service, do you want : to link it with existing [suggestions], or to put it in SOAs [SOA systems] ?"); }

        request.addDiscoveryNotification(clientServiceEndpoint, clientServiceEndpoint2, axxxDcvCrmApplicationSystem);
        System.out.println("\ndcvChangesServiceImpl:");
    }
    
    @Test
    public void dpsRediscoversOldClientServiceInDevSoapui() throws IOException {
        dpsDevTest1Environment = new Environment("devDps", "0.1");
        clientServiceMockDevDpsEndpoint = new Endpoint(dpsDevTest1Environment, "http://vmcrm/WS/ClientService.asmx", clientServiceWsdl, "0.1");
        clientServiceMockDevDpsEndpoint.addRelation(dpsDevTest1Environment); // TODO deployedDeliverable between
        dpsTestSoapuiSystem = new SoaSystem("dpsTestSoapui", "0.1");
        dpsTestSoapuiSystem.addRelation(clientServiceMockDevDpsEndpoint); // set in web discovery ui ; TODO rather production endpoint ?!

        // on revalidation, alerts DPS about inconsistency :
        // web discovery (in dev) : redisco'd dev env wsdl differs from reference TODO code NO or design (is old one, found by hash) 
        if (!clientServiceMockDevDpsEndpoint.getWsdl().equals(clientServiceEndpoint.getWsdl())) { System.out.println("Error : web disco'd test wsdl differs from the design one");}
        
        request.addDiscoveryNotification(dpsDevTest1Environment, clientServiceMockDevDpsEndpoint, dpsTestSoapuiSystem);
        System.out.println("\ndpsRediscoversOldClientServiceInDev:");
    }
    
    @After
    public void postTrace() throws IOException  {
        request.send();
        repository.traceRepository();
    }
    
}
