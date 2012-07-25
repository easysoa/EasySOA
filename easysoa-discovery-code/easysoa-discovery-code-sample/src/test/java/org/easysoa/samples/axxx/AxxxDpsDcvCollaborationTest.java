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
    private static Environment dcvStagingEnvironment;
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
    private static Service altWithClientItfTdrService;

    private static String clientServiceWsdl;
    private static String tdrServiceWsdl;

    private static Deliverable defaultDpsApvDeliverable;
    private static Deliverable defaultDcvCrmDeliverable;
    private static Deliverable dpsApvTdrDeliverable;
    private static Deliverable dpsTest1SoapuiConfDeliverable;

    private static ServiceImpl dpsApvTdrServiceImpl;
    private static ServiceImpl dpsTest1SoapuiClientServiceImpl;
    
    private static Endpoint clientServiceEndpoint;
    private static Endpoint tdrServiceEndpoint;
    private static Endpoint altWithClientItfTdrServiceMockEndpoint;
    private static Endpoint clientServiceEndpoint2;
    private static Endpoint dpsDevTest1TdrServiceEndpoint;
    private static Endpoint clientServiceMockDevDpsEndpoint;
    
    
    @BeforeClass
    public static void setup() {
        repository = new MockRepository();
        request = new DiscoveryRequest(repository);
    }



    @Test
    public void dcvCrmDeveloperDoesWebDisco() throws IOException {
        // TODO classification systems : BusinessDomainSystem, ServiceLayerSystem, DeliverableGroupSystem...
        // TODO Deliverables (impl) consume services (references) : ServiceImpl => ProviderImpl, + ConsumerImpl
        // TODO MODEL code interface (in dev env, similar to endpoint wsdl in depl env vs service wsdl)
        // TODO MODEL DEV ENV source (maven, if not scm) _project_, and LATER maven _repo_ and link to them (similar to an env) SHOULD BE THE SAME, and LATER CI
        // TODO source code disco probe : must say from which dev env (at least top-level source project extracted from pom & if possible organization (or user)), TODO further
        // TODO WSDL in source discovery, @generatedFrom/To(wsdl) on p/c/iimpls
        // TODO new AxxxSoa for future multitenancy
        // TODO everything can link to service (& ref (& process)) ?!?
        
        // business & archi : (LATER v2)
        
        // TODO Software => (Software)Component of Software(System) ; ApplicationSystem => Software(System)
        // TODO SoftwareComponents requirements : expose and consume services (references NO rather subSoftwareComponent)
        // TODO to reqs = add rules ; but consumeServiceRule must target a service !! OR service.wsdl is req wsdl and if the dev wants another temporary service.wsdl he can use another service and even another design (and map to his future self teap-like...)
        // TODO model service exposition & consuming in archi ; sure (jwt) process models finer consumption than mere linking, but linking is true also, and unless process are modeled it is the only one available !

        // TODO service.consumer(Role)Reqs (a service has a single consumer role) ; roles also / rather than organizations & users (may allow consumers to get instanciated version of the schemas with themselves in role...)
        
        // TODO validation dashboard for env according to orga ("profiles") = probes + rules, conf'd by the SOA's admin
        // TODO confble / instanciable probes : name, role ; ex. Source("default dev"), Source("dps ci"), Web(explicit, "dpsArchitect staging")... ; in v1 not confble ; disco probe params go to (extended) model elts (soa, env ; also SystemApp), their params (source project ; or else modeled), or else Systems, or else at least queriable fields on DiscoLogEntry
        // TODO DiscoLogService, or even DiscoNotifService(overridePreviousWithSameId) vs DiscoHistoryService ; context (orga, env, biz / archi / tech systems) + soaElementsLinked = seen (or even also = required ?? NO rather requirements are seen)
        // TODO cio quick wins : WSDL in source : as discoLog (or even model wsdl portType, or even binding (vs itf), or even types !! or even maven sources & repo, servers...) correlated with Service.wsdl(req)'s name(space), hash (trimmed including CRLF, but pbs xml & binding), xmlhash or diff (normalized spacing & endings, with & without service/port (endpoint url) or even binding)

        // TODO OK sched val : autoprobes using a previous probing conf (history), comparison with old version (or record / fork if simpler), changes not approved can't be published to other people (collab or end users), and are notified to responsible & impacted people (including UI "service" with a "end user" orga ??)
        
        // even LATER
        // TODO fine conf of disco entry param to model field ; in source : to test, SoftwareComponent...), & helped by maven pom props (or Manifest entries...) à la @api("doc")
        // TODO dev quick wins : from SOAPUI ex. artifacts generation (jboss, axis, cxf, also .NET)
        // TODO universal tunnel proxy for easysoa-integrated tools (clients) ex. soapui, scaffolder : embedded in easysoa, & can be local agent outputting json discos (or also http proxy using soapui headers, or with wsa...) ; BUT must be able to simulate wsdl (and changed port / endpoint url) ?? ; in soapui settings (in user home) <con:setting id="ProxySettings@host">localhost</con:setting><con:setting id="ProxySettings@port">38081</con:setting> or sample request "</con:request><con:wsaConfig mustUnderstand="NONE" version="200508" action="http://fr.ancv.pivotal/Client"/><con:wsrmConfig version="1.2"/></con:call><con:call name="PivotalClientRequest1"><con:settings><con:setting id="com.eviware.soapui.impl.wsdl.WsdlRequest@request-headers">&lt;entry key="testheader" value="zzz" xmlns="http://eviware.com/soapui/config"/></con:setting></con:settings>"
        // TODO instanciable / param's services : discoNotif apply to all services (with same endpoint...), save if context specified (disco of process) or extracted (exchange param / rule) 
        // TODO f next : use pmerle's additions, try cxf proxy ; integrate f studio using f in n, see if it can instanciate "proxies" ; else do universal http tunnel proxy

        // TODO how to hide parts of its architecture to a collaborating organization ? do your full architecture in another SOA and live proxy / publish versioned parts of it only in this SOA.
        
        // DCV : CRM developer does a web discovery of ClientService
        // (in axxx (shared / collab & target (SOA)) staging env ("where is it ?"), axxxDcvCrm physical application by default ("what provides it, & who (organisation) ?") => services serve to collaboration ("what is it useful for ?"))
        // NB. must say what is its target ("what is it useful for ?") to create services from endpoints NOOOOOOOOOOOOO its application
        stagingEnvironment = new Environment("staging", "0.1");
        clientServiceWsdl = "<wsdl/>";
        clientServiceEndpoint = new Endpoint(stagingEnvironment, "http://vmcrm/WS/ClientService.asmx", clientServiceWsdl, "0.1");
        stagingEnvironment.addRelation(clientServiceEndpoint); // TODO add default deployedDeliverable between
        axxxDcvCrmApplicationSystem = new SoaSystem("AXXX DCV CRM Application", "0.1"); // TODO provided by DCV CRM
        ////axxxDcvCrmApplicationSystem.addRelation(clientServiceEndpoint); // set in web discovery ui ; TODO rather production endpoint ?!
        // axxxDcvCrmApplicationSystem.setProvider(DCVOrganisation);
        
        // at disco end (disco save / approve), in validation / correlation dashboard :
        // "some environment elements (endpoints) are not propertized, we suggest you do"
        //stagingEnvironment.setParameter("vmcrm", "vmcrm"); // TODO (or on deployedDeliverable ??)
        //AxxxSoa.addParameter("vmcrm"); // or auto when previous line
        clientServiceEndpoint.setUrl("http://${vmcrm}/WS/ClientService.asmx");
        
        // target env (& SOA workflow) design (or below) :
        // "is it the (collaborationSoaSystem's) target env ?" yes :
        // collaborationSoaSystem.setTarget(staging); // & TODO set order of (display of) other envs as well
        // each (exchange, endpoint, deplDeliv, deliv, implProv&Cons) should be seen according to the env capacity, which should be as much as possibly is and maximal in dev envs 
        // targetEnvRules("impl (amongDeplDel) : no mock, all JAXWS / @inject seen & with unit tests & build (compile / tests) ok (in a dev env), no mock, all maven no SNAPSHOT, doc'd, unit tested", "seen all SOAP endpoints", "all impls tested (i.e. depl'd in a test env wth all other rules except no mock impl)");
        // targetEnvRulesOptional("sla : req, platform conf, met / health", "seen in monit all SOAP exchanges, or at least in dev envs on both sides (of provider & consumer)");
        // targetEnvRulesConsistency("when (each) service (itf...) changes (compared to last major version / snapshot), (each) known consumer depl.version (or depl trace) should also");
        // targetEnvRulesHealth("ci : build quality (compile history in jenkins), code quality (sonar)", "internal state history (number of rules matched on elements)");
        // targetEnvRulesSanityAlerts("alert (responsible & impacted people) (& send scheduled reports)",  "if consistency error", "if health / state drops below a given threshold : ci, internal state (i.e. too much rules that once matched don't anymore, or matching level dropped beyond a threshold ex. doc quality)"););

        // TODO env has also info of which (software / (depl)del) (mock instead of real one in env) (req / should be / say to) deploy !!??
        // ex. in dpsDev, not only "not mock" but "those owned by dps (DpsOrganization or (Dps(Apv))Software) should not be mock"
        // TODO THAT'S NOT A (DEPL) ENV BUT A _DEV_ ENV => could be a part of Software (top level) ; or Organization ???
        // dev env = from where (source code) changes / evols come...
        // and where conf of dev wiki, swagger, tos, jwt / soa, ci info ? (ci is just another dev env, which can provide additional info ex. build sanity & code quality)
        // TODO similar to depl env conf = sla vs req
        
        // devEnvRules("all target rules except no mock (endpoint, impl)", "seen all SOAP providers in JAXRS and consumers in CBI @inject");
        // devEnvRules("'what it devs' should not be mock", "and should belong to Organization")
        // dpsDevEnvRules("'what it devs' = axxxDpsApvSoftware")
        
        // designPhaseRules("SOAP services must have WSDL reqs", "requirementsVersioningApprovalWorkflow");
        // designPhaseRulesOptional("if unknown services i.e. not in business process, CIO dashboard alert");
        // (NB. if disco in dcvDev env first, not the target env, so "transition / fork & design it" (or after publish, only in collab pov)
        // service auto creation in target soa (rather than "there is no (matched) service yet, do you want to create them ?")
        clientService = new Service("ClientService", "0.1");
        clientService.addRelation(clientServiceEndpoint); // TODO add default serviceimpl between
        axxxDcvCrmApplicationSystem.addRelation(clientService); // set in web discovery ui
        // clientService.setProvider(DCVOrganisation); // or auto from axxxDcvCrmApplicationSystem
        // clientServiceEndpoint.setProvider(DCVOrganisation); // or auto from (deliverable or) axxxDcvCrmApplicationSystem & service
        // (NB. endpoint & service have different organisation in target env only for callback service)
        // auto add some requirements gotten from itself (wsdl) : "make requirements out of it" (or even auto / suggested since target env) OR later by business process ; i.e. services with wsdl, (prop'd) endpoints, defaut impl & (depl)del, "no less & no more services (... TODO) in this buiness (app) context" 
        clientService.getRequirements().add("wsdl=" + clientServiceWsdl);
        // (NB. "service" means valid for all endpoints, therefore required wsdl should be added on service (and not endpoint(s)))
        ////clientServiceEndpoint.getRequirements().add("wsdl=" + clientServiceWsdl);

        // ALT TdR service that reuses the same Client itf provided by DCV
        // in another web disco, with mapping context not "current SOA" but excluding axxxDcvCrmSoftware(ApplicationSystem) by being in different Software (OR "create new services / don't automap services" OR outside of SOA) :
        // (LATER this also allows discovering calls (by monit) that are in (from) specific process steps i.e. SubSoftware(System)s)
        dcvStagingEnvironment = new Environment("dcvStaging", "0.1"); // in another env because endpoint is mock and therefore should not go in design ; else "same endpoint and different services, one endpoint has to be mock (& its impl mock _of_ another)"
        altWithClientItfTdrServiceMockEndpoint = new Endpoint(dcvStagingEnvironment, "http://vmcrm/WS/ClientService.asmx", clientServiceWsdl, "0.1");
        dcvStagingEnvironment.addRelation(altWithClientItfTdrServiceMockEndpoint); // TODO add default deployedDeliverable between
        // contributing it to SOA (design, req wsdl) :
        altWithClientItfTdrService = new Service("AltWithClientItfTdrService", "0.1"); 
        altWithClientItfTdrService.getRequirements().add("wsdl=" + clientServiceWsdl);
        altWithClientItfTdrService.addRelation(altWithClientItfTdrServiceMockEndpoint); // TODO add default serviceimpl between
        ////axxxDpsApvApplicationSystem.addRelation(altWithClientItfTdrService); // or later by DPS
        
        // approves disco'd model by (minor) version / snapshot (or may be autosnapshotted before next disco)

        // UI :
        // target env (staging) plays the role of requirements / expected model
        // so to see completion progress of collab, display your work in the context of your forked collab ("what's not there yet in dps (dcv)")
        // (conf, state / health)
        // ex. for each service exposition & consumption in target env, classified by (& with overview summary for) BP & archi / softwareComponents :
        // * endpoint : seen once or not (with right wsdl & other reqs) by web & al.(monit disco, exchanges) , (currently there or not i.e. auto / manual ping & further monit ex. health)
        // * consumption : seen by code, monit disco
        // * deployedDeployable : conf in this env (which are to be deployed ex. mockimpldepl or trueimpldepl, env conf overrides ex. consumptions), seen once or not by cp disco or platform), ((currently there or not, health))
        // * deployable : (once / last TODO) seen in source project (& maven repo), (with deployedDeployables seen in other envs, which are complete though have mocks)
        // * serviceproviderimpl : seen in source (main & itf in test) (et al.), with (deplDel &) endpoints (& exchanges) seen in other envs, which are complete (though have mocks i.e. tested)
        // * (serviceconsumerimpl : seen in source (main & itf in test) (et al.), with ddeplDel &) exchanges seen in other envs, which are complete (though have mocks i.e. tested))
        // * bp : has reqs (docs, mapping to archi), has sample exchange records
        // * service : has reqs (docs, mapping to archi, wsdl), has sample (t'd) replayable exchanges (or other samples) for each operation, (has mock impls)
        // impl (can be seen in code, tests), (dev or ci) test envs of this impl (can be seen by web disco...), its depl in other envs, its depl here (web disco)...
        
        request.addDiscoveryNotification(stagingEnvironment, clientServiceEndpoint, axxxDcvCrmApplicationSystem, clientService, dcvStagingEnvironment, altWithClientItfTdrServiceMockEndpoint, altWithClientItfTdrService);
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
        // tdrService.setProvider(DPSOrganisation); // or auto from axxxDpsApvApplicationSystem
        // tdrServiceEndpoint.setProvider(DPSOrganisation); // or auto from axxxDpsApvApplicationSystem & service
        // altWithClientItfTdrService.setProvider(DPSOrganisation); // or auto from axxxDpsApvApplicationSystem & service
        axxxDpsApvApplicationSystem.addRelation(altWithClientItfTdrService);
        
        dpsApvSoftware = new Software("DPS APV", "0.1");
        collaborationArchitectureRequirementsSystem.addRelation(dpsApvSoftware); // dpsApvSoftware belongs to architecture
        collaborationBusinessRequirementsSystem.addRelation(dpsApvSoftware); // dpsApvSoftware contributes to collaborationBusinessProcessSystem

        collaborationArchitectureRequirementsSystem.addRelation(tdrService); // ?
        dpsApvSoftware.addRelation(tdrService); // TODO rather architectureRequirements
        collaborationArchitectureRequirementsSystem.addRelation(altWithClientItfTdrService); // ?
        dpsApvSoftware.addRelation(altWithClientItfTdrService); // TODO rather architectureRequirements

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
        
        request.addDiscoveryNotification(tdrServiceEndpoint, tdrService, altWithClientItfTdrService, axxxDpsApvApplicationSystem, dpsApvSoftware,
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
        defaultDcvCrmDeliverable.addRelation(altWithClientItfTdrService); // modeling architectural consumption (or at first)
        
        // publishes (TODO major version of) model to SOA collaboration space (or / and gives read rights to DPS) : v3
        // ENDS DESIGN PHASE (i.e. requirements ; the remaining additions to staging are mere enrichments of ACTUAL), may be explicitly versioned / locked by button...
        // "design phase" (or just "phase") means that both collaborating organisations will rely on an approved, fixed version ; and that changing it simply means asking for another such phase
        // TODO LATER say to those referencing the previous one that they have to update / auto update 

        request.addDiscoveryNotification(defaultDcvCrmDeliverable, dcvCrmSoftware);
        System.out.println("\ndcvDesignsConsumption:");
    }
    
    @Test
    public void dpsDiscoversCodeThenWebInDevThenStaging() throws IOException {
        // TOOD LATER DPS updates / get collaboration model latest (major) version : v3
        
        // DPS : (ESB) APV developer does a code discovery of Client Service consumption & exposition
        // the idea is : disco (ours) in dev then "apply to others (staging)"
        
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
        // TODO get & auto create (expected) things from staging (endpoints) with !seen and rules ("if ours, should appear not in test"...)
        
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

        // TODO DPS adds manual doc, samples... : (on services, or endpoints if env-specific NOO rather test apps)
        //tdrService.addDocItem(collaborationSoaSystem, "This allows to"); // simple way to share
        //tdrService.addDocItem(dpsSoaSystem, "This allows to").liveProxy(collaborationSoaSystem);  // more complex way to share
        // TODO LATER to not share docItems (or other things services), create them locally rather than in collab OR have a local space "doc to share (recopy there)" OR live proxy local them in collab
        //tdrService.addDocItem(dpsSoaSystem, "Help on our Mock"); // internal doc
        // samples are seen on an endpoint, and contributed on service :
        //tdrService.addSourceSampleItem(collaborationSoaSystem, "/** arg0 = 1 */"); // samples, done by source autodoc
        //tdrService.addUnitSampleItem(collaborationSoaSystem, sourceImplReference); // samples from unit tests
        //tdrService.addRestSampleItem(collaborationSoaSystem, sampleRequest(AndResponseAsserts)); // samples from mined REST
        
        // publishes (TODO major version of) model to SOA collaboration space (or / and gives read rights to DPS) : v4
        // ((* merge : for each thing not ours ((changed)), suggest getting it ; show table of services with what's changed in it : reqs, model ex. deliv / impl, state ex. endpoint, doc...))
        // * show validation dashboard (table with services per app (or biz ?), state (endpoints), conflicts and missing, doc & samples...)
        // * for each ((changed)) thing of ours (endpoint, impl, ((systems, service))), suggest putting it in downstream envs down to target env (staging)
        // (OR only in the next downstream one, where publishing has also to be done) and remember when not (ex. dpsDev-only impl : tag it with "test" System and don't give read rights)
        // TODO LATER say to those referencing the previous one that they have to update / auto update
        // i.e. DCV updates its dev from staging
        
        // TODO reproviding a service (or other things) : that's actually creating another (ex. NuxeoService => SalesNuxeoService) that relies on the same things (itf/d/si/e, docs...)
        // how to allow painless update and reprovide ?? => TODO service "proxy" / live proxy everything
        // TODO share & reuse : either 1. create it (service) in the shared area (collab) 2. else use (live) proxy to publish / share it 3. do similar proxying of subelements for finer control
        
        // TODO the most flexible way to share & update is to allow configured copy (or overrides) of everything !!!!!!!
        
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
        // the idea is : when disco'd (his) in staging then "apply to others ((my) dev)" TODO BUT WHAT ???

        // DPS : (ESB) APV developer does a code discovery of Client Service consumption & exposition
        
        // (putting things back in order for case 1)
        stagingEnvironment.getRelations().put(clientServiceEndpoint.getId(), clientServiceEndpoint.getSoaNodeType());
        stagingEnvironment.getRelations().remove(clientServiceEndpoint2.getId());
        // (NB. in case 2 (or other further divergence), TODO Q either dev endpoint should have been ajusted auto (or on staging disco save / publish / approve ??) as soon as staging's did,
        // or inconsistency appears BETWEEN environments : "one endpoint is has gone missing (compared to previous state) in dev compared to staging")
        
        // web disco of dev env (continued from before) of mock old endpoint :
        clientServiceMockDevDpsEndpoint = new Endpoint(dpsDevTest1Environment, "http://localhost/WS/ClientService.asmx", clientServiceWsdl, "0.1");
        dpsDevTest1Environment.addRelation(clientServiceMockDevDpsEndpoint);
        dpsTest1SoapuiConfDeliverable = new Deliverable("dpsTest1SoapuiConfDeliverable", "dpsTest1SoapuiConfDeliverable", "0.1", null);
        dpsApvSoftware.addRelation(dpsTest1SoapuiConfDeliverable); // modeling implementation
        dpsTest1SoapuiConfDeliverable.addRelation(dpsDevTest1TdrServiceEndpoint); // modeling architectural consumption OR BY IMPL
        dpsTest1SoapuiClientServiceImpl = new ServiceImpl(dpsTest1SoapuiConfDeliverable, "SOAPUI", "soapui:ClientServiceMock1", "ClientServiceMock1");
        clientService.addRelation(dpsTest1SoapuiClientServiceImpl);
        dpsTest1SoapuiClientServiceImpl.addRelation(clientServiceMockDevDpsEndpoint);
        dpsTest1SoapuiConfDeliverable.addRelation(dpsTest1SoapuiClientServiceImpl);
        dpsTest1SoapuiConfDeliverable.addRequirement("Consumes TdrService"); // SOAPUI conf consumes
        dpsTest1SoapuiConfDeliverable.addRequirement("Consumes ClientService"); // SOAPUI conf consumes
        dpsTestSoapuiSystem = new SoaSystem("dpsTestSoapui", "0.1"); // models test app ; TODO or deliverable (soapui conf) or both ??? YES
        dpsTestSoapuiSystem.addRelation(clientServiceMockDevDpsEndpoint); // set in web discovery ui ; TODO rather service ?! NO

        // web disco of SOAPUI-exposed service tagged "test-only" :
        // TODO create endpoint only in this test env
        
        // on revalidation, alerts DPS about inconsistency :
        // web discovery (in dev) : redisco'd dev env wsdl differs from reference TODO code NO or design (is old one, found by hash) 
        if (!clientServiceMockDevDpsEndpoint.getWsdl().equals(clientServiceEndpoint.getWsdl())) { System.out.println("Error : web disco'd test wsdl differs from the TODO design one");}
        
        request.addDiscoveryNotification(stagingEnvironment, dpsDevTest1Environment, clientServiceMockDevDpsEndpoint, dpsTest1SoapuiConfDeliverable, dpsTest1SoapuiClientServiceImpl, dpsTestSoapuiSystem);
        System.out.println("\ndpsRediscoversOldClientServiceInDev:");
    }
    
    @After
    public void postTrace() throws IOException  {
        request.send();
        repository.traceRepository();
    }
    
}
