#require "buildr/bnd"

repositories.remote << 'http://www.ibiblio.org/maven2'
repositories.remote << 'http://maven.nuxeo.org/nexus/content/groups/public'

############### CONFIG

THIS_VERSION = '0.1.1'
NUXEO_VERSION = '5.4.1'
#NUXEO_PATH = '/opt/nuxeo-dm'
# MDU
NUXEO_PATH = '/home/mdutoo/dev/easysoa/nuxeo-dm-$NUXEO_VERSION-tomcat'

# Generated
NUXEO_PLUGINS = NUXEO_PATH+'/nxserver/plugins/'

############### TASKS

#desc "Builds and deploy plugins with buildr"
#task :nuxeo => ['easysoa:plugins:easysoa-demo-model-core:package',
#                 'easysoa:plugins:easysoa-demo-model-web:package',
#                 'easysoa:plugins:easysoa-demo-rest:package',
#                 'nuxeo_dist']
                 
desc "Builds plugins using Maven, then deploy"
task :nuxeo_mvn => ['easysoa:plugins:easysoa-demo-model-core:maven',
                 'easysoa:plugins:easysoa-demo-model-web:maven',
                 'easysoa:plugins:easysoa-demo-rest:maven',
                 'nuxeo_dist']
                 
desc "Deploy plugins only"
task :nuxeo_dist => ['easysoa:plugins:dist']

desc "Cleans all plugins"
task :clean => ['easysoa:plugins:easysoa-demo-model-core:clean',
                 'easysoa:plugins:easysoa-demo-model-web:clean',
                 'easysoa:plugins:easysoa-demo-rest:clean']
                 
############### DEPENDENCIES

# NUXEO : Nuxeo & dependancies
COMMONS_LOGGING = 'commons-logging:commons-logging:jar:1.0'
NUXEO_RUNTIME = group(
    'nuxeo-runtime',
    'nuxeo-runtime-test',
  :under=>'org.nuxeo.runtime', :type=>'jar', :version=>NUXEO_VERSION)
NUXEO_COMMON = 'org.nuxeo.common:nuxeo-common:jar:'+NUXEO_VERSION
NUXEO_CORE = group(
    'nuxeo-core',
    'nuxeo-core-api',
    'nuxeo-core-event',
  :under=>'org.nuxeo.ecm.core', :type=>'jar', :version=>NUXEO_VERSION)
NUXEO_PLATFORM = group(
    'nuxeo-platform-directory-core',
    'nuxeo-platform-directory-api',
    'nuxeo-platform-directory-sql',
    'nuxeo-platform-relations-api',
    'nuxeo-platform-webapp-base',
    'nuxeo-platform-ui-web',
  :under=>'org.nuxeo.ecm.platform', :type=>'jar', :version=>NUXEO_VERSION)
NUXEO_AUTOMATION = 'org.nuxeo.ecm.automation:nuxeo-automation-core:jar:'+NUXEO_VERSION
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

############### CUSTOM FUNCTIONS

def maven(goal, project_path)
  system 'mvn '+goal+' -f '+project_path+'/pom.xml'
  puts "", ""
end

############### PROJECT DEFINITIONS
  
define 'easysoa' do
  
  # Nuxeo plugins
  define 'plugins' do
  
    desc 'Plugin Nuxeo - Core'
    define 'easysoa-demo-model-core' do
      project.version = THIS_VERSION
      package(:jar).with :manifest=>_('src/main/resources/META-INF/MANIFEST.MF')
      compile.with NUXEO, EASYWSDL
      
      task :maven do
        maven 'package', 'plugins/easysoa-demo-model-core'
      end
    end
    
    desc 'Plugin Nuxeo - Web'
    define 'easysoa-demo-model-web' do
      project.version = THIS_VERSION
      package(:jar).with :manifest=>_('src/main/resources/META-INF/MANIFEST.MF')
      
      task :maven do
        maven 'package', 'plugins/easysoa-demo-model-web'
      end
    end
    
    desc 'Plugin Nuxeo - REST API'
    define 'easysoa-demo-rest' do
      project.version = THIS_VERSION
      package(:jar).with :manifest=>_('src/main/resources/META-INF/MANIFEST.MF')
      compile.with project('easysoa-demo-model-core'), NUXEO, HTMLCLEANER, RESTLET, JSON
      
      task :maven do
        maven 'package', 'plugins/easysoa-demo-rest'
      end
    end
    
    desc 'Send plugins to Nuxeo'
    task :dist do
      DIST_DELETE = FileList[_(NUXEO_PLUGINS+'*.jar')]
      DIST_COPY = FileList[_('easysoa-demo-model-core/target/*.jar'),
        _('easysoa-demo-model-web/target/*.jar'),
        _('easysoa-demo-rest/target/*.jar')]
      puts "Deleting : ", "", DIST_DELETE, "", ""
      rm DIST_DELETE
      puts "Deploying : ", "", DIST_COPY
      cp DIST_COPY, NUXEO_PLUGINS
    end
    
    desc ''
    task :dist do
      DIST_DELETE = FileList[_(NUXEO_PLUGINS+'*.jar')]
      DIST_COPY = FileList[_('easysoa-demo-model-core/target/*.jar'),
        _('easysoa-demo-model-web/target/*.jar'),
        _('easysoa-demo-rest/target/*.jar')]
      puts "Deleting : ", "", DIST_DELETE, "", ""
      rm DIST_DELETE
      puts "Deploying : ", "", DIST_COPY
      cp DIST_COPY, NUXEO_PLUGINS
    end
    
  end
  
end

############### GLOBAL TASKS
