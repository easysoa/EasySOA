require 'shellwords'

repositories.remote << 'http://www.ibiblio.org/maven2'

# ----------------------
# EasySOA Demo Buildfile
# ----------------------

############### HOWTO

# Type "buildr help" for a list of available tasks

############### CONFIG LOADING

NUXEO_PATH = Buildr.settings.build['nuxeo']['path'] || ENV['HOME']+'/nuxeo-dm-5.4.1-tomcat' #|| Buildr.settings.user['nuxeo']['path']
NODE_PATH = Buildr.settings.build['node']['path'] || ENV['HOME']+'/node'
FRASCATI_PATH = Buildr.settings.build['frascati']['path'] || './deps/frascati'

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
PAF = 'easysoa:easysoa-demo-pureAirFlowers-proxy'
TRAVEL = 'easysoa:EasySOADemoTravel'
TRAVEL_BACKUP = 'easysoa:EasySOADemoTravel:travel-services-backup'
ESPER = 'easysoa:esper-frascati-poc'

define 'easysoa', :base_dir => '../' do
  
  desc 'Service registry and discovery by browsing project'
  define 'easysoa-model-demo' do
    
    desc 'Nuxeo plugins project'
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
    
    desc 'Discovery by browsing project'
    define 'discovery-by-browsing' do
      # Nothing
    end
    
  end
  
  desc 'EasySOA Light proxy project'
  define 'easysoa-demo-pureAirFlowers-proxy' do
    task :mvn do
      maven(['clean', 'install'], project)
    end
  end
  
  desc 'Smart travel demo project'
  define 'EasySOADemoTravel' do
    task :mvn do
      maven(['clean', 'install'], project)
    end
    desc 'Smart travel backup services project'
    define 'travel-services-backup' do
      task :mvn do
        maven(['clean', 'assembly:assembly'], project)
      end
    end
  end
  
  desc 'Esper monitoring proxy project'
  define 'esper-frascati-poc' do
    task :mvn do
      maven(['clean', 'install', '-DskipTests=true'], project) # TODO Make tests pass
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
task :paf => [PAF+':mvn']

desc "Builds the Esper proxy"
task :esper => [ESPER+':mvn']

desc "Builds the Smart Travel demo and its backup services"
task :travel => [TRAVEL+':mvn', TRAVEL_BACKUP+':mvn']

desc "Builds all needed projects"
task :buildall => ['paf', 'nx_mvn', 'esper', 'travel']

desc "Creates the EasySOA package"
task :packageall do # TODO Less messy code

  puts "", "Starting to build EasySOA package"
  
  # Check dependencies
  if !File.exist?(NUXEO_PATH)
    raise "Files copy failed: Nuxeo not found. You need to provide a distribution of Nuxeo DM 5.4.1 in `build.yaml`."
  end
  if !File.exist?(NODE_PATH+'/node')
    raise "Files copy failed: Node not found. You need to provide a built Node path in `build.yaml` (download sources from: http://nodejs.org/#download)."
  end
  
  # Prepare EasySOA environment
  puts "Preparing EasySOA environment..."
  rm_rf PACKAGING_OUTPUT_PATH
  mkdir_p PACKAGING_OUTPUT_PATH
  mkdir_p './deps'
  cp_r FileList['packaging-files/*'], PACKAGING_OUTPUT_PATH
  
  # Download/extract node.js if necessary
  if !File.exist?('./deps/node.exe')
    puts 'Downloading node.js for Windows...'
    system 'wget', 'http://nodejs.org/dist/v0.5.1/node.exe' # XXX Linux-dependent
    mv 'node.exe', './deps'
  end
  puts 'Copying node.js...'
  mkdir_p PACKAGING_OUTPUT_PATH+'/node'
  cp './deps/node.exe', PACKAGING_OUTPUT_PATH+'/node'
  begin
    cp NODE_PATH+'/node', PACKAGING_OUTPUT_PATH+'/node'
  rescue Exception
    raise "Files copy failed: Node location seems invalid (binary not found)"
  end
  
  # Download/extract FraSCAti if necessary
  if !File.exist?(FRASCATI_PATH) 
    if !File.exist?('deps/frascati-1.4-bin.zip')
      puts 'FraSCAti not found, downloading it...'
      system 'wget', 'http://download.forge.objectweb.org/frascati/frascati-1.4-bin.zip' # XXX Linux-dependent
      mv 'frascati-1.4-bin.zip','./deps'
    end
    puts 'Extracting FraSCAti...'
    system 'unzip', '-q', '-o', './deps/frascati-1.4-bin.zip' # XXX Linux-dependent
    mv 'frascati-runtime-1.4', './deps/frascati'
    rm_rf FileList['./deps/frascati/doc', './deps/frascati/examples', './deps/frascati/README', './deps/frascati/RELEASE_NOTES']
  end

  # Deploy FraSCAti
  puts 'Copying FraSCAti...'
  FileUtils.cp_r FRASCATI_PATH, PACKAGING_OUTPUT_PATH
  
  # Copy web services and proxies
  puts "Copying web services and proxies..."
  puts "  * Needed libraries..."
  cp FileList[project(TRAVEL).base_dir+"/currency-model/target/*.jar"], PACKAGING_OUTPUT_PATH+'/frascati/lib'
  cp FileList[project(TRAVEL).base_dir+"/meteo-model/target/*.jar"], PACKAGING_OUTPUT_PATH+'/frascati/lib'
  cp FileList[project(TRAVEL).base_dir+"/summary-model/target/*.jar"], PACKAGING_OUTPUT_PATH+'/frascati/sca-apps' # Conflicting with the Light trip proxy if put in lib/
  cp FileList[project(TRAVEL).base_dir+"/translate-model/target/*.jar"], PACKAGING_OUTPUT_PATH+'/frascati/lib'
  puts "  * FraSCAti applications & PAF services..."
  cp_r FileList[project(PAF).base_dir+'/distrib/*'], PACKAGING_OUTPUT_PATH
  cp FileList[project(ESPER).base_dir+"/target/*.jar"], PACKAGING_OUTPUT_PATH+'/frascati/sca-apps'
  cp FileList[project(TRAVEL).base_dir+"/trip*/target/*.jar"], PACKAGING_OUTPUT_PATH+'/frascati/sca-apps'
  puts "  * Travel backup services..."
  mkdir_p PACKAGING_OUTPUT_PATH+'/travelBackup'
  system 'unzip', '-q', '-o', FileList[project(TRAVEL_BACKUP).base_dir+'/target/*-bin.zip'].to_s # XXX Linux-dependent
  cp_r FileList['travel-services-backup*/*'], PACKAGING_OUTPUT_PATH+'/travelBackup/'
  rm_rf FileList['travel-services-backup*']
  
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
  cp_r FileList[project('easysoa:easysoa-model-demo').base_dir+'/exemples/*'], PACKAGING_OUTPUT_PATH+'/exemples'
  rm_rf PACKAGING_OUTPUT_PATH+'/serviceRegistry/log/'
  rm_rf PACKAGING_OUTPUT_PATH+'/serviceRegistry/tmp/'
  rm_rf PACKAGING_OUTPUT_PATH+'/serviceRegistry/nxserver/data/'
  
  puts "Copying web server (node.js + antinode)..."
  mkdir PACKAGING_OUTPUT_PATH+'/web'
  cp_r FileList[project(DBBROWSING).base_dir+"/webserver"], PACKAGING_OUTPUT_PATH+'/web'
  cp_r FileList[project(DBBROWSING).base_dir+"/start-web.*"], PACKAGING_OUTPUT_PATH+'/web'
  
  puts "Copying discovery by browsing proxy (node.js)..."
  mkdir PACKAGING_OUTPUT_PATH+'/dbbProxy'
  cp_r FileList[project(DBBROWSING).base_dir+"/proxyserver"], PACKAGING_OUTPUT_PATH+'/dbbProxy'
  cp_r FileList[project(DBBROWSING).base_dir+"/start-dbbProxy.*"], PACKAGING_OUTPUT_PATH+'/dbbProxy'
  
  puts "Packaging done."
  puts "You can launch EasySOA by typing 'cd easysoa', then './run.sh'."
  
end

desc "Creates the EasySOA package"
task :tgz do
  
  rm_f FileList['easysoa-demo-*']
  
  # Tar
  puts "Compressing..."
  system 'tar -zcf ' + PACKAGING_OUTPUT_ARCHIVE + ' -C ' + PACKAGING_OUTPUT_PATH + \
    ' dbbProxy frascati travelBackup serviceRegistry web ' + \
    FileList["packaging-files/*"].sub('packaging-files/', '').to_s  # XXX Linux-dependent
  
  puts "EasySOA successfully compressed in "+PACKAGING_OUTPUT_ARCHIVE
  
end
