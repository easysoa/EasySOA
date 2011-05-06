require 'shellwords'

repositories.remote << 'http://www.ibiblio.org/maven2'
repositories.remote << 'http://maven.nuxeo.org/nexus/content/groups/public'

# ----------------------
# EasySOA Demo Buildfile
# ----------------------

############### PREREQUISITES & COMMANDS

# See README.md

############### CONFIG

NUXEO_PATH = ENV['HOME']+'/nuxeo-dm-5.4.1-tomcat'

THIS_VERSION = '1.0-SNAPSHOT'
MODEL_VERSION = '0.1.2-SNAPSHOT'
PAF_VERSION = '1.0-SNAPSHOT'

# Generated
NUXEO_PLUGINS = NUXEO_PATH+'/nxserver/plugins/'
MANIFEST_RELATIVE_PATH = 'src/main/resources/META-INF/MANIFEST.MF'

############### CUSTOM FUNCTIONS

def maven(goals, project, version)
  command = 'mvn '
  if !goals.is_a?(Array)
    goals = [goals]
  end
  for goal in goals
    case goal
      when 'package'
        command += 'package '
      when 'install:install-file'
        command += 'install:install-file -Dfile=target/'+ project.name.sub(project.parent.name+':', '') + '-'+version+'.jar -DpomFile=pom.xml '
      else
        command += goal+' '
    end
  end
  command += '-f ' + project.base_dir + '/pom.xml'
  puts command
  system command
  puts "", ""
end
 # 
 
############### PROJECT DEFINITIONS

DBBROWSING = 'easysoa:easysoa-model-demo:discovery-by-browsing'
MODEL_CORE = 'easysoa:easysoa-model-demo:plugins:easysoa-model-demo-core'
MODEL_WEB = 'easysoa:easysoa-model-demo:plugins:easysoa-model-demo-web'
MODEL_REST = 'easysoa:easysoa-model-demo:plugins:easysoa-model-demo-rest'
PAF_CXF = 'easysoa:easysoa-demo-pureAirFlowers:pureAirFlowers-easysoa-demo-cxf-server'
PAF_PROXY = 'easysoa:easysoa-demo-pureAirFlowers:pureAirFlowers-ServiceUiScaffolderProxy'
PAF_RELEASE = 'easysoa:easysoa-demo-pureAirFlowers:pureAirFlowers-Release'
PAF_BUILD = 'easysoa:easysoa-demo-pureAirFlowers:pureAirFlowers-BinaryBuildComponents'
PAF_LOGINTENT = 'easysoa:easysoa-demo-pureAirFlowers:pureAirFlowers-logIntent'
PAF_FUSINTENT = 'easysoa:easysoa-demo-pureAirFlowers:pureAirFlowers-autoRearmFuseIntent'

define 'easysoa', :base_dir => '../' do
  
  define 'easysoa-model-demo' do
    
    # Nuxeo plugins
    define 'plugins' do
    
      desc 'Plugin Nuxeo - Core'
      define 'easysoa-model-demo-core' do
        project.version = MODEL_VERSION
        package(:jar).with :manifest=>_(MANIFEST_RELATIVE_PATH)
        
        task :mvn do
          maven(['clean', 'package', 'install:install-file'], project, MODEL_VERSION)
        end
      end
      
      desc 'Plugin Nuxeo - Web'
      define 'easysoa-model-demo-web' do
        project.version = MODEL_VERSION
        package(:jar).with :manifest=>_(MANIFEST_RELATIVE_PATH)
        
        task :mvn do
          maven(['clean', 'package', 'install:install-file'], project, MODEL_VERSION)
        end
      end
      
      desc 'Plugin Nuxeo - REST API'
      define 'easysoa-model-demo-rest' do
        project.version = MODEL_VERSION
        package(:jar).with :manifest=>_(MANIFEST_RELATIVE_PATH)
        
        task :mvn do
          maven(['clean', 'package', 'install:install-file'], project, MODEL_VERSION)
        end
      end
      
      desc 'Send plugins to Nuxeo'
      task :dist do
        DIST_DELETE = FileList[_(NUXEO_PLUGINS+'*.jar')]
        DIST_COPY = FileList[_(project(MODEL_CORE).base_dir+'/target/*'+MODEL_VERSION+'.jar'),
          _(project(MODEL_WEB).base_dir+'/target/*'+MODEL_VERSION+'.jar'),
          _(project(MODEL_REST).base_dir+'/target/*'+MODEL_VERSION+'.jar')]
        puts "Nuxeo plugins - Deleting : ", DIST_DELETE
        rm DIST_DELETE
        puts "Nuxeo plugins - Deploying : ", DIST_COPY
        mkdir_p NUXEO_PLUGINS
        cp DIST_COPY, NUXEO_PLUGINS
      end
      
      desc 'Put Nuxeo jars in git build folder'
      task :git do
        DIST_JARS = FileList[_(NUXEO_PLUGINS+'*'+MODEL_VERSION+'.jar')]
        puts "Nuxeo plugins - Deploying to Git folder : ", DIST_JARS
        cp DIST_JARS, "../easysoa-model-demo/build/"
      end
      
    end
    
    desc 'Discovery by browsing'
    define 'discovery-by-browsing' do
      # Nothing
    end
    
  end
  
  define 'easysoa-demo-pureAirFlowers' do
  
    define 'pureAirFlowers-Release' do
      task :mvn do
        maven(['clean', 'install'], project, PAF_VERSION)
      end
    end
    
    define 'pureAirFlowers-easysoa-demo-cxf-server'
    define 'pureAirFlowers-ServiceUiScaffolderProxy'
    define 'pureAirFlowers-BinaryBuildComponents'
    define 'pureAirFlowers-logIntent'
    define 'pureAirFlowers-autoRearmFuseIntent'
    
  end
  
  desc 'EasySOA packaging'
  define 'easysoa-demo-dist' do
    # Nothing
  end
  
end


############### TASKS DEFINITIONS
                 
desc "Builds Nuxeo plugins using Maven"
task :nx_mvn => [MODEL_CORE+':mvn', MODEL_WEB+':mvn', MODEL_REST+':mvn']
                 
desc "Deploys Nuxeo plugins"
task :nx_dist => ['easysoa:easysoa-model-demo:plugins:dist']

desc "Copise plugins to git build folder"
task :nx_git => ['easysoa:easysoa-model-demo:plugins:git']

desc "Cleans all Nuxeo plugins"
task :nx_clean => [MODEL_CORE+':clean', MODEL_WEB+':clean', MODEL_REST+':clean']
             
desc "Builds PAF CXF server and service proxy"
task :paf_mvn => [PAF_RELEASE+':mvn']

desc "Builds all needed projects"
task :buildall => ['paf_mvn', 'nx_mvn']

desc "Creates the EasySOA package"
task :tgz => ['nx_dist'] do

  puts "", "Starting to build EasySOA package"

  TMP = '.tmp'
  OUT = 'easysoa-demo-'+THIS_VERSION+'.tar.gz';
  
  # Prepare environment
  rm_rf TMP
  rm_f FileList['easysoa-demo-*']
  
  # Copy all needed files
  PATH_NUXEO = 'nuxeo-dm'
  mkdir TMP
  
  puts "Copying web services (Apache CXF + FraSCAti)..."
  cp_r project(PAF_BUILD).base_dir+'/distrib/', TMP
  rm TMP+'/distrib/cxf-server/readme'
  rm TMP+'/distrib/frascati-proxy/readme'
  
  cp FileList[project(PAF_CXF).base_dir+'/target/*dep.jar'].to_s, TMP+'/distrib/cxf-server/'
  cp project(PAF_CXF).base_dir+'/readme.txt', TMP+'/distrib/cxf-server/'
  
  system 'unzip -q ' + project(PAF_BUILD).base_dir+'/Frascati_binary_runtime/*.zip -d ' + TMP + '/distrib/frascati-proxy'
  cp FileList[project(PAF_PROXY).base_dir+'/target/*.jar'].to_s, TMP+'/distrib/frascati-proxy/sca-apps'
  cp FileList[project(PAF_LOGINTENT).base_dir+'/target/*.jar'].to_s, TMP+'/distrib/frascati-proxy/sca-apps'
  cp FileList[project(PAF_FUSINTENT).base_dir+'/target/*.jar'].to_s, TMP+'/distrib/frascati-proxy/sca-apps'
  cp FileList[project(PAF_LOGINTENT).base_dir+'/target/*.jar'].to_s, TMP+'/distrib/frascati-proxy/lib'
  cp FileList[project(PAF_FUSINTENT).base_dir+'/target/*.jar'].to_s, TMP+'/distrib/frascati-proxy/lib'
  mv TMP+'/distrib', TMP+'/webservices'
  
  puts "Copying web server (node.js + antinode)..."
  mkdir TMP+'/web'
  cp_r FileList[project(DBBROWSING).base_dir+"/webserver"], TMP+'/web'
  cp_r project(DBBROWSING).base_dir+"/start-web.sh", TMP+'/web'
  
  puts "Copying web proxy (node.js)..."
  mkdir TMP+'/webproxy'
  cp_r FileList[project(DBBROWSING).base_dir+"/proxyserver"], TMP+'/webproxy'
  cp_r project(DBBROWSING).base_dir+"/start-proxy.sh", TMP+'/webproxy'
  
  puts "Copying service registry (Nuxeo)..."
  begin
    cp_r NUXEO_PATH, TMP
  rescue Exception
    raise "Files copy failed: Nuxeo is probably running. Could you please stop it?"
  end
  system 'mv', '-T', TMP+'/nuxeo-dm', TMP+'/serviceregistry'
  rm_rf TMP+'/serviceregistry/tmp/'
  rm_rf TMP+'/serviceregistry/nxserver/data/'
  
  # Tar
  puts "Compressing..."
  system 'tar -zcf ' + OUT + ' -C ' + TMP + \
    ' serviceregistry web webproxy webservices webservicesproxy ' + \
    ' -C ../files/ ' + FileList["files/*"].sub('files/', '').to_s
  
  # Clean
  puts "Cleaning temporary files..."
  system 'rm', '-r', TMP
  
  puts "EasySOA successfully packaged in "+OUT
  
end
