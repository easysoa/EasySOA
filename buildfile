repositories.remote << 'http://www.ibiblio.org/maven2'
repositories.remote << 'http://maven.nuxeo.org/nexus/content/groups/public'

############### CONFIG

THIS_VERSION = '0.1.1'
#NUXEO_PATH = '/opt/nuxeo-dm'
# MDU
NUXEO_PATH = '/home/mdutoo/dev/easysoa/nuxeo-dm-5.4.1-tomcat'

# Generated
NUXEO_PLUGINS = NUXEO_PATH+'/nxserver/plugins/'

############### ALIASES

task :nuxeo_dist => ['easysoa:plugins:easysoa-demo-model-web:package',
                 'easysoa:plugins:easysoa-demo-model-web:package',
                 'easysoa:plugins:easysoa-demo-rest:package',
                 'easysoa:plugins:dist']

############### DEPENDENCIES

# NUXEO : Nuxeo & dependancies
COMMONS_LOGGING = 'commons-logging:commons-logging:jar:1.0'
NUXEO_RUNTIME = group(
    'nuxeo-runtime',
    'nuxeo-runtime-test',
  :under=>'org.nuxeo.runtime', :type=>'jar', :version=>'5.4.1')
NUXEO_COMMON = 'org.nuxeo.common:nuxeo-common:jar:5.4.1'
NUXEO_CORE = group(
    'nuxeo-core',
    'nuxeo-core-api',
    'nuxeo-core-event',
  :under=>'org.nuxeo.ecm.core', :type=>'jar', :version=>'5.4.1')
NUXEO_PLATFORM = group(
    'nuxeo-platform-directory-core',
    'nuxeo-platform-directory-api',
    'nuxeo-platform-directory-sql',
    'nuxeo-platform-relations-api',
    'nuxeo-platform-webapp-base',
    'nuxeo-platform-ui-web',
  :under=>'org.nuxeo.ecm.platform', :type=>'jar', :version=>'5.4.1')
NUXEO_AUTOMATION = 'org.nuxeo.ecm.automation:nuxeo-automation-core:jar:5.4.1'
NUXEO = COMMONS_LOGGING, NUXEO_RUNTIME, NUXEO_COMMON, NUXEO_CORE, NUXEO_PLATFORM, NUXEO_AUTOMATION

# EASYWSDL
EASYWSDL = group(
    'easywsdl-schema',
    'easywsdl-wsdl',
  :under=>'org.ow2.easywsdl', :type=>'jar', :version=>'2.1')

# HTMLCLEANER
HTMLCLEANER = 'net.sourceforge.htmlcleaner:htmlcleaner:jar:2.2'

# RESTLET
RESTLET = 'org.restlet:org.restlet:jar:1.0.7'

# JSON
JSON = 'org.json:json:jar:20070829'

############### PROJECT DEFINITIONS

define 'easysoa' do
  
  # Nuxeo plugins
  define 'plugins' do
  
    task :dist do
      rm FileList[_(NUXEO_PLUGINS+'*.jar')]
      cp FileList[_('easysoa-demo-model-core/target/*.jar'),
        _('easysoa-demo-model-web/target/*.jar'),
        _('easysoa-demo-rest/target/*.jar')], NUXEO_PLUGINS
    end
  
    desc 'Plugin Nuxeo - Core'
    define 'easysoa-demo-model-core' do
      project.version = THIS_VERSION
      package :jar
      compile.with NUXEO, EASYWSDL
    end
    
    desc 'Plugin Nuxeo - Web'
    define 'easysoa-demo-model-web' do
      project.version = THIS_VERSION
      package :jar
    end
    
    desc 'Plugin Nuxeo - REST API'
    define 'easysoa-demo-rest' do
      project.version = THIS_VERSION
      package :jar
      compile.with project('easysoa-demo-model-core'), NUXEO, HTMLCLEANER, RESTLET, JSON
    end
    
  end
  
  # ZIP JS
 #define 'easysoa-servicefinder' do
  #  project.version = '0.1.1'
  #  package(:zip).include _('proxyserver')
  #end
  
end

