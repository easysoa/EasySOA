package org.easysoa.samples.axxx;

import java.io.IOException;
import java.util.HashSet;

import org.easysoa.discovery.mock.MockRepository;
import org.easysoa.discovery.rest.client.DiscoveryRequest;
import org.easysoa.discovery.rest.model.Deliverable;
import org.easysoa.discovery.rest.model.Endpoint;
import org.easysoa.discovery.rest.model.Environment;
import org.easysoa.discovery.rest.model.Service;
import org.easysoa.discovery.rest.model.ServiceImpl;
import org.easysoa.discovery.rest.model.SoaNode;
import org.easysoa.discovery.rest.model.SoaNodeType;
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
        
        // TODO disco (ours) in dev then "apply to others (staging)", disco (his) in staging then "apply to others (dev)" 
        // TODO add wsdl requirements on all envs when wsdl known (OR on Service ?!?)
        // TODO "design phase" (or just "phase") means that both collaborating organisations will rely on an approved, fixed version ; and that changing it simply means asking for another such phase  
        
        // DCV : CRM developer does a web discovery of ClientService
        // (in axxx staging env ("where is it ?"), axxxDcvCrm physical application by default ("what provides it ?") & collaboration "logical" target / SOA => services serve to collaboration ("what is it useful for ?"))
        // NB. must say what is its target ("what is it useful for ?") to create services from endpoints NOOOOOOOOOOOOO its application
        stagingEnvironment = new Environment("staging", "0.1");
        clientServiceWsdl = "<wsdl/>";
        clientServiceEndpoint = new Endpoint(stagingEnvironment, "http://vmcrm/WS/ClientService.asmx", clientServiceWsdl, "0.1");
        stagingEnvironment.addRelation(clientServiceEndpoint); // TODO add default deployedDeliverable between
        axxxDcvCrmApplicationSystem = new SoaSystem("AXXX DCV CRM Application", "0.1"); // TODO provided by DCV CRM
        ////axxxDcvCrmApplicationSystem.addRelation(clientServiceEndpoint); // set in web discovery ui ; TODO rather production endpoint ?!
        // axxxDcvCrmApplicationSystem.setProvider(DCVOrganisation);
        
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
        // clientService.setProvider(DCVOrganisation); // or auto from axxxDcvCrmApplicationSystem
        // clientServiceEndpoint.setProvider(DCVOrganisation); // or auto from axxxDcvCrmApplicationSystem & service
        // (NB. endpoint & service have different organisation in target env only for callback service)
        
        // approves disco'd model by (minor) version / snapshot (or may be autosnapshotted before next disco)

        // UI :
        // target env (staging) plays the role of requirements / expected model
        // so to see completion progress of collab, display your work in the context of your forked collab ("what's not there yet in dps (dcv)")
        // ex. for each endpoint : impl (can be seen in code, tests), (dev or ci) test envs of this impl (can be seen by web disco...), its depl in other envs, its depl here (web disco)...
        
        request.addDiscoveryNotification(stagingEnvironment, clientServiceEndpoint, axxxDcvCrmApplicationSystem, clientService);
        System.out.println("\ndcvCrmDeveloperDoesWebDisco:");
    }
    
    @Test
    public void dcpOrDpsCompletesDesign() throws IOException {
        // DCP (or DPS) completes design, MAYBE also of consumption
        collaborationBusinessRequirementsSystem = new SoaSystem("AXXX DPS DCV Collaboration BusinessProcess", "0.1"); // also add business requirements documents in it
        collaborationArchitectureRequirementsSystem = new SoaSystem("AXXX DPS DCV Collaboration Architecture", "0.1"); // also add architecture requirements documents in it
        dcvCrmSoftware = new Software("DCV CRM", "0.1");
        collaborationArchitectureRequirementsSystem.addRelation(dcvCrmSoftware); // dcvCrmSoftware belongs to architecture
        collaborationBusinessRequirementsSystem.addRelation(dcvCrmSoftware); // dcvCrmSoftware contributes to collaborationBusinessProcessSystem
        // (NB. Systems that are shared between requirements and actual are as much as possible about services (?) so they are easier to share, or else about target env (endpoints)) 

        collaborationArchitectureRequirementsSystem.addRelation(clientService); // ?
        dcvCrmSoftware.addRelation(clientService); // TODO rather architectureRequirements
        
        // publishes (TODO major version of) model to SOA collaboration space (or / and gives read rights to DPS) : v1
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
        // (NB. creates endpoint and sets seenInRequirements, linked to business & architecture systems...)
        tdrServiceEndpoint = new Endpoint(stagingEnvironment, "http://vmapv/cxf/TdrService", null, "0.1");
        stagingEnvironment.addRelation(tdrServiceEndpoint); // TODO deployedDeliverable between

        // service auto creation (rather than "there is no (matched) service yet, do you want to create them ?")
        tdrService = new Service("TdrService", "0.1");
        tdrService.addRelation(tdrServiceEndpoint); // TODO add default serviceimpl between
        
        axxxDpsApvApplicationSystem = new SoaSystem("AXXX DPS APV Application", "0.1"); // TODO provided by DPS
        axxxDpsApvApplicationSystem.addRelation(tdrService); // set in web discovery ui
        // axxxDpsApvApplicationSystem.setProvider(DPSOrganisation);
        // clientService.setProvider(DPSOrganisation); // or auto from axxxDpsApvApplicationSystem
        // clientServiceEndpoint.setProvider(DPSOrganisation); // or auto from axxxDpsApvApplicationSystem & service
        
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
        
        // publishes (TODO major version of) model to SOA collaboration space (or / and gives read rights to DPS) : v2
        // TODO LATER say to those referencing the previous one that they have to update / auto update 
        
        request.addDiscoveryNotification(tdrServiceEndpoint, tdrService, axxxDpsApvApplicationSystem, dpsApvSoftware,
                collaborationBusinessRequirementsSystem, collaborationArchitectureRequirementsSystem,
                collaborationSoaSystem, defaultDpsApvDeliverable);
        System.out.println("\ndpsDesignsConsumptionAndExposition:");
    }
    
    @Test
    public void dcvDesignsConsumption() throws IOException {
        // TOOD LATER DCV updates / get collaboration model latest (major) version : v2
        
        // DCV : maybe only now design consumption of CRM Client Service : download, modify and reimport sca (not code discovery because .NET) 
        defaultDcvCrmDeliverable = new Deliverable("defaultDcvCrmDeliverable", "defaultDcvCrmDeliverable", "0.1", null);
        dcvCrmSoftware.addRelation(defaultDcvCrmDeliverable); // modeling implementation
        defaultDcvCrmDeliverable.addRelation(tdrService); // modeling architectural consumption
        
        // publishes (TODO major version of) model to SOA collaboration space (or / and gives read rights to DPS) : v3
        // ENDS DESIGN PHASE (i.e. requirements ; the remaining additions to staging are mere enrichments of ACTUAL), may be explicitly locked by button...
        // TODO LATER say to those referencing the previous one that they have to update / auto update 

        request.addDiscoveryNotification(defaultDcvCrmDeliverable, dcvCrmSoftware);
        System.out.println("\ndcvDesignsConsumption:");
    }
    
    @Test
    public void dpsDiscoversCodeThenWebInDevThenStaging() throws IOException {
        // TOOD LATER DPS updates / get collaboration model latest (major) version : v3
        
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

        // DPS : (ESB) APV developer creates a new dev environment, using staging as model
        dpsDevTest1Environment = new Environment("dpsDevTest1", "0.1"); // with vmcrm & vmapv params since belongs to AxxxSoa
        // TODO get & auto create things from staging (endpoints) with !seen and rules ("if ours, should appear not in test") 
        
        // DPS : (ESB) APV developer does a web discovery of TdrService
        // (in dpsDevTest1 env ("where is it ?"), axxxDpsApv physical application by default ("what provides it ?") & collaboration "logical" target / SOA => services serve to collaboration ("what is it useful for ?"))
        // NB. must say what is its target ("what is it useful for ?") to create services from endpoints NOOOOOOOOOOOOO its application
        tdrServiceWsdl = "<wsdl/>";
        // (NB. by setting endpoint.seen rather than creating it since already modeled in target env)
        dpsDevTest1TdrServiceEndpoint = new Endpoint(dpsDevTest1Environment, "http://${vmapv}/cxf/TdrService", tdrServiceWsdl, "0.1"); // auto prop'd with known params
        dpsDevTest1Environment.addRelation(dpsDevTest1TdrServiceEndpoint); // TODO add known deployedDeliverable between
        // since tdrService belongs to axxxDpsApvApplicationSystem which has been set in web discovery ui, and since WSDL matches with target env's (expected one), service can be auto found :
        tdrService.addRelation(dpsDevTest1TdrServiceEndpoint); // TODO add known serviceimpl between
        
        // TODO contribute (staging) requirements from known :
        tdrServiceEndpoint.getRequirements().add("wsdl=" + dpsDevTest1TdrServiceEndpoint.getWsdl()); 
        
        // web disco in staging :
        tdrServiceEndpoint.setWsdl(tdrServiceWsdl); // TODO store actual wsdl in another endpoint (ex. in "design" environment ??)
        
        // TODO DPS impl mock clientService, matches expected (gotten from target env) wsdl in dev (see next)

        // publishes (TODO major version of) model to SOA collaboration space (or / and gives read rights to DPS) : v4
        // * show validation dashboardfffffffffffffffffffffffff
        // ((* merge : for each thing not ours ((changed)), suggest getting it ; show validation table with services, conflicts and missing))
        // * for each ((changed)) thing of ours (endpoint, impl, ((systems, service))), suggest putting it in downstream envs down to target env (staging)
        // (OR only in the next downstream one, where publishing has also to be done) and remember when not (ex. dpsDev-only impl : tag it with "test" System and don't give read rights)
        // TODO LATER say to those referencing the previous one that they have to update / auto update
        // i.e. DCV updates its dev from staging
        
        request.addDiscoveryNotification(dpsApvTdrDeliverable, dpsApvTdrServiceImpl, dpsDevTest1Environment, dpsDevTest1TdrServiceEndpoint);
        System.out.println("\ndpsDiscoversCode:");
    }
    
    @Test
    public void dcvChangesServiceImpl() throws IOException {
        // TOOD LATER DCV updates / get collaboration model latest (major) version : v4

        // DCV : changes code (not discovered) and therefore actual service interface, detected in (staging) web discovery (auto or not)
        
        // case 1 : same service, different wsdl, LATER other correlation,kinds
        // clientServiceEndpoint = findEndpoint(serviceName)
        String anotherClientServiceWsdl = "<wsdl><!-- another ClientService wsdl --></wsdl>";
        clientServiceEndpoint.setWsdl(anotherClientServiceWsdl);
        clientServiceEndpoint.setVersion("0.2");
        // on revalidation, alerts DPS about inconsistency : (TODO Q how does the DCV change propagate to DPS ??)
        // code discovery :
        // code alt 1 : if it doesn't compile anymore (assuming dpsApvTdrServiceImpl's consumed ClientService interface is generated from wsdl auto by maven TODO), error("doesn't compile anymore ; changes : ClientService.java changed")
        // code alt 2 : if (it's not a backward compatible change and) dpsApvTdrServiceImpl's consumed ClientService.java interface doesn't change, error("should have changed")
        // if (dpsApvTdrServiceImpl.getRequirements().get(0).contains("")) {}
        // web discovery (in staging) :
        if (!tdrServiceEndpoint.getWsdl().equals(tdrServiceEndpoint.getWsdl())) { System.out.println("Error : web disco'd impl wsdl differs from the design one");} // TODO another endpoint (ex. in "design" environment ??)
        
        // case 2 : different service (i.e. can't be correlated)
        // reinit webdisco in staging :
        for (String endpointNodeId : stagingEnvironment.getRelations().keySet()) { /*((Endpoint) endpointNode).setSeen(false);*/stagingEnvironment.getRelations().put(endpointNodeId, null); }
        // discovers still existing :
        stagingEnvironment.getRelations().put(tdrServiceEndpoint.getId(), tdrServiceEndpoint.getSoaNodeType());
        // discovers also the "new" endpoint :
        clientServiceEndpoint2 = new Endpoint(stagingEnvironment, "http://vmcrm/WS/AnotherClientService.asmx", anotherClientServiceWsdl , "0.1");
        stagingEnvironment.addRelation(clientServiceEndpoint2); // TODO deployedDeliverable between
        axxxDcvCrmApplicationSystem.addRelation(clientServiceEndpoint2); // set in web discovery ui ; TODO rather production endpoint ?!
        //if (clientService has no more endpoint in staging) { error("Staging : no (more) endpoint for clientService in staging. Not ready to version.")}
        for (String endpointId : stagingEnvironment.getRelations().keySet()) { if (stagingEnvironment.getRelations().get(endpointId)/*.getSeen();*/ == null) System.out.println("Staging : no (more) endpoint for " + endpointId); }
        //if (clientServiceEndpoint2 has no related service) { warn("New service, do you want : to link it with existing [suggestions], or to put it in SOAs [SOA systems] ?"); }
        
        // no auto publish, but alerts : first (concerned) users must look up what these inconsistencies mean
        // and manual publish once it's found to be true / won't be rolled back / is justified & doc'd : v5
        // (NB. with auto publish, dev env could try things on it, but not really useful... though even if published, could be rolled back)
        
        request.addDiscoveryNotification(clientServiceEndpoint, clientServiceEndpoint2, axxxDcvCrmApplicationSystem);
        System.out.println("\ndcvChangesServiceImpl:");
    }
    
    @Test
    public void dpsRediscoversOldClientServiceInDevSoapui() throws IOException {
        // TOOD LATER DPS updates / get collaboration model latest (major) version : v5

        // DPS : (ESB) APV developer does a code discovery of Client Service consumption & exposition
        
        // (putting things back in order for case 1)
        stagingEnvironment.getRelations().put(clientServiceEndpoint.getId(), clientServiceEndpoint.getSoaNodeType());
        stagingEnvironment.getRelations().remove(clientServiceEndpoint2.getId());
        // (NB. in case 2 (or other further divergence), TODO Q either dev endpoint should have been ajusted auto (or on staging disco save / publish / approve ??) as soon as staging's did,
        // or inconsistency appears BETWEEN environments : "one endpoint is has gone missing (compared to previous state) in dev compared to staging")
        
        // web disco of mock old endpoint in dev env
        dpsDevTest1Environment = new Environment("devDps", "0.1");
        clientServiceMockDevDpsEndpoint = new Endpoint(dpsDevTest1Environment, "http://vmcrm/WS/ClientService.asmx", clientServiceWsdl, "0.1");
        clientServiceMockDevDpsEndpoint.addRelation(dpsDevTest1Environment); // TODO deployedDeliverable between
        dpsTestSoapuiSystem = new SoaSystem("dpsTestSoapui", "0.1");
        dpsTestSoapuiSystem.addRelation(clientServiceMockDevDpsEndpoint); // set in web discovery ui ; TODO rather production endpoint ?!

        // on revalidation, alerts DPS about inconsistency :
        // web discovery (in dev) : redisco'd dev env wsdl differs from reference TODO code NO or design (is old one, found by hash) 
        if (!clientServiceMockDevDpsEndpoint.getWsdl().equals(clientServiceEndpoint.getWsdl())) { System.out.println("Error : web disco'd test wsdl differs from the TODO design one");}
        
        request.addDiscoveryNotification(dpsDevTest1Environment, clientServiceMockDevDpsEndpoint, dpsTestSoapuiSystem);
        System.out.println("\ndpsRediscoversOldClientServiceInDev:");
    }
    
    @After
    public void postTrace() throws IOException  {
        request.send();
        repository.traceRepository();
    }
    
}
