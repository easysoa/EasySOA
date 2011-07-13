require 'shellwords'

repositories.remote << 'http://www.ibiblio.org/maven2'
#repositories.remote << 'http://maven.nuxeo.org/nexus/content/groups/public'

# ----------------------
# EasySOA Demo Buildfile
# ----------------------

############### PREREQUISITES & COMMANDS

# See README.md
# (Command list: buildall, packageall, tgz, nx_mvn, nx_dist, nx_clean, paf_mvn, trip, esper)

############### CONFIG LOADING

NUXEO_PATH = Buildr.settings.build['nuxeo']['path'] || ENV['HOME']+'/nuxeo-dm-5.4.1-tomcat' #|| Buildr.settings.user['nuxeo']['path']
FRASCATI_PATH = Buildr.settings.build['frascati']['path'] || './frascati'

THIS_VERSION = Buildr.settings.build['release']['version']
MODEL_VERSION = Buildr.settings.build['model']['version']
PAF_VERSION = Buildr.settings.build['paf']['version']

PACKAGING_OUTPUT_PATH = 'easysoa'
PACKAGING_OUTPUT_ARCHIVE = 'easysoa-demo-'+THIS_VERSION+'.tar.gz';

# Generated
NUXEO_PLUGINS_PATH = NUXEO_PATH+'/nxserver/plugins/'
MANIFEST_RELATIVE_PATH = 'src/main/resources/META-INF/MANIFEST.MF'

############### CUSTOM FUNCTIONS

def maven(goals, project)
  command = 'mvn '
  if !goals.is_a?(Array)
    goals = [goals]
  end
  for goal in goals
    command += goal+' '
  end
  command += '-f ' + project.base_dir + '/pom.xml'
  puts command
  system command
  puts "", ""
end
 # 
 
############### PROJECT DEFINITIONS

DBBROWSING = 'easysoa:easysoa-model-demo:discovery-by-browsing'
MODEL = 'easysoa:easysoa-model-demo:plugins'
PAF_CXF = 'easysoa:easysoa-demo-pureAirFlowers-proxy:pureAirFlowers-easysoa-demo-cxf-server'
PAF_PROXY = 'easysoa:easysoa-demo-pureAirFlowers-proxy:pureAirFlowers-ServiceUiScaffolderProxy'
PAF_RELEASE = 'easysoa:easysoa-demo-pureAirFlowers-proxy:pureAirFlowers-Release'
PAF_LOGINTENT = 'easysoa:easysoa-demo-pureAirFlowers-proxy:pureAirFlowers-logIntent'
PAF_FUSINTENT = 'easysoa:easysoa-demo-pureAirFlowers-proxy:pureAirFlowers-autoRearmFuseIntent'
TRIP = 'easysoa:EasySOADemoTravel'
#TRIP_TEST = 'easysoa:galaxyDemoTest'
ESPER = 'easysoa:esper-frascati-poc'

define 'easysoa', :base_dir => '../' do
  
  define 'easysoa-model-demo' do
    
    desc 'Nuxeo plugins'
    define 'plugins' do
    
      project.version = MODEL_VERSION
      task :mvn do
        maven(['clean', 'install'], project)
      end
      
      desc 'Send plugins to Nuxeo'
      task :dist do
        DIST_DELETE = FileList[_(NUXEO_PLUGINS_PATH+'*.jar')]
        puts "Nuxeo plugins - Deleting : ", DIST_DELETE
        rm DIST_DELETE
        DIST_COPY = FileList[_(project(MODEL).base_dir+'/target/*.jar')]
        puts "Nuxeo plugins - Deploying : ", DIST_COPY
        mkdir_p NUXEO_PLUGINS_PATH
        cp DIST_COPY, NUXEO_PLUGINS_PATH
      end
      
    end
    
    desc 'Discovery by browsing'
    define 'discovery-by-browsing' do
      # Nothing
    end
    
  end
  
  define 'easysoa-demo-pureAirFlowers-proxy' do
  
    define 'pureAirFlowers-Release' do
      task :mvn do
        maven(['clean', 'install'], project)
      end
    end
    
    define 'pureAirFlowers-easysoa-demo-cxf-server'
    define 'pureAirFlowers-ServiceUiScaffolderProxy'
    define 'pureAirFlowers-BinaryBuildComponents'
    define 'pureAirFlowers-logIntent'
    define 'pureAirFlowers-autoRearmFuseIntent'
    
  end
  
  define 'EasySOADemoTravel' do
    task :mvn do
      maven(['clean', 'install'], project)
    end
  end
  
  define 'esper-frascati-poc' do
    task :mvn do
      maven(['clean', 'install'], project)
    end
  end
  
  desc 'EasySOA packaging'
  define 'easysoa-demo-dist' do
    # Nothing
  end
  
end


############### TASKS DEFINITIONS
                 
desc "Builds Nuxeo plugins using Maven"
task :nx_mvn => [MODEL+':mvn']
                 
desc "Deploys Nuxeo plugins"
task :nx_dist => [MODEL+':dist']

desc "Cleans all Nuxeo plugins"
task :nx_clean => [MODEL+':clean']
             
desc "Builds PAF CXF server and service proxy"
task :paf_mvn => [PAF_RELEASE+':mvn']

task :esper => [ESPER+':mvn']

task :trip => [TRIP+':mvn', TRIP_TEST+':mvn']

desc "Builds all needed projects"
task :buildall => ['paf_mvn', 'nx_mvn', 'esper', 'trip']

desc "Creates the EasySOA package"
task :packageall do

  puts "", "Starting to build EasySOA package"
  
  # Preparie EasySOA environment
  puts "Preparing EasySOA environment..."
  rm_rf PACKAGING_OUTPUT_PATH
  mkdir_p PACKAGING_OUTPUT_PATH
  cp_r FileList['packaging-files/*'], PACKAGING_OUTPUT_PATH
  
  # Download/extract FraSCAti if necessary
  puts 'FraSCAti path not defined in `build.yaml`, using default one.'
  if !File.exist?(FRASCATI_PATH) 
    if !File.exist?('frascati-1.4-bin.zip')
      puts 'FraSCAti not found, downloading it...'
      system 'wget', 'http://download.forge.objectweb.org/frascati/frascati-1.4-bin.zip' # XXX Linux-dependent
    end
      puts 'Extracting FraSCAti...'
    system 'unzip', '-q', '-o', 'frascati-1.4-bin.zip' # XXX Linux-dependent
    FileUtils.mv 'frascati-runtime-1.4', 'frascati'
  end

  # Deploy FraSCAti
  puts 'Copying FraSCAti...'
  FileUtils.cp_r FRASCATI_PATH, PACKAGING_OUTPUT_PATH
  
  # Copy web services and proxies
  puts "Copying web services and proxies..."
  mkdir_p PACKAGING_OUTPUT_PATH+'/pafServices'
  begin
    cp FileList[project(PAF_CXF).base_dir+'/target/*with-dependencies.jar'].to_s, PACKAGING_OUTPUT_PATH+'/pafServices/'
  rescue Exception
    raise "PureAirFlowers CXF server JAR seems missing"
  end
  cp FileList[project(PAF_PROXY).base_dir+'/target/*.jar'].to_s, PACKAGING_OUTPUT_PATH+'/frascati/sca-apps'
  cp FileList[project(ESPER).base_dir+"/target/*.jar"], PACKAGING_OUTPUT_PATH+'/frascati/sca-apps'
  cp FileList[project(TRIP).base_dir+"/trip/target/*.jar"], PACKAGING_OUTPUT_PATH+'/frascati/sca-apps'
  cp FileList[project(TRIP).base_dir+"/easysoa-meteo-sca-backup/target/*.jar"], PACKAGING_OUTPUT_PATH+'/frascati/sca-apps'
  cp FileList[project(TRIP).base_dir+"/currency-model/target/*.jar"], PACKAGING_OUTPUT_PATH+'/frascati/lib'
  cp FileList[project(TRIP).base_dir+"/meteo-model/target/*.jar"], PACKAGING_OUTPUT_PATH+'/frascati/lib'
  cp FileList[project(TRIP).base_dir+"/summary-model/target/*.jar"], PACKAGING_OUTPUT_PATH+'/frascati/lib'
  cp FileList[project(TRIP).base_dir+"/translate-model/target/*.jar"], PACKAGING_OUTPUT_PATH+'/frascati/lib'
  cp FileList[project(PAF_LOGINTENT).base_dir+'/target/*.jar'].to_s, PACKAGING_OUTPUT_PATH+'/frascati/lib'
  cp FileList[project(PAF_FUSINTENT).base_dir+'/target/*.jar'].to_s, PACKAGING_OUTPUT_PATH+'/frascati/lib'
  #cp project(PAF_CXF).base_dir+'/readme.txt', PACKAGING_OUTPUT_PATH+'/distrib/cxf-server/'
  
  # Copying Nuxeo
  puts "Copying service registry (Nuxeo)..."
  begin
    cp_r NUXEO_PATH, PACKAGING_OUTPUT_PATH
  rescue Exception
    raise "Files copy failed: Nuxeo is probably running. Could you please stop it?"
  end
  system 'mv', '-T', FileList[PACKAGING_OUTPUT_PATH+'/nuxeo-dm*'].to_s, PACKAGING_OUTPUT_PATH+'/serviceRegistry' # XXX Linux-dependent
  cp FileList[project('easysoa:easysoa-model-demo').base_dir+'/lib/*.jar'], PACKAGING_OUTPUT_PATH+'/serviceRegistry/lib'
  cp FileList[project('easysoa:easysoa-model-demo').base_dir+'/plugins/target/*.jar'], PACKAGING_OUTPUT_PATH+'/serviceRegistry/nxserver/plugins' # TODO Check that bundles exist
  rm_rf PACKAGING_OUTPUT_PATH+'/serviceRegistry/tmp/'
  rm_rf PACKAGING_OUTPUT_PATH+'/serviceRegistry/nxserver/data/'
  
  puts "Copying web server (node.js + antinode)..."
  mkdir PACKAGING_OUTPUT_PATH+'/web'
  cp_r FileList[project(DBBROWSING).base_dir+"/webserver"], PACKAGING_OUTPUT_PATH+'/web'
  cp_r FileList[project(DBBROWSING).base_dir+"/start-web.*"], PACKAGING_OUTPUT_PATH+'/web'
  
  puts "Copying web proxy (node.js)..."
  mkdir PACKAGING_OUTPUT_PATH+'/webProxy'
  cp_r FileList[project(DBBROWSING).base_dir+"/proxyserver"], PACKAGING_OUTPUT_PATH+'/webProxy'
  cp_r FileList[project(DBBROWSING).base_dir+"/start-proxy.*"], PACKAGING_OUTPUT_PATH+'/webProxy'
  
  puts "Packaging done."
  
end

desc "Creates the EasySOA package"
task :tgz do
  
  rm_f FileList['easysoa-demo-*']
  
  # Tar
  puts "Compressing..."
  system 'tar -zcf ' + PACKAGING_OUTPUT_ARCHIVE + ' -C ' + PACKAGING_OUTPUT_PATH + \
    ' serviceregistry web webproxy webservices ' + \
    FileList["packaging-files/*"].sub('packaging-files/', '').to_s  # XXX Linux-dependent
  
  puts "EasySOA successfully compressed in "+PACKAGING_OUTPUT_ARCHIVE
  
end
