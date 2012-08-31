#!/usr/bin/env ruby

require 'fileutils'

NUXEO_PATH = '/data/home/mkalam-alami/bin/nuxeo-cap-5.5-tomcat';
NUXEO_PLUGINS_PATH = NUXEO_PATH + '/nxserver/plugins';

ARGV.each do |arg|

  case arg
    
    when 'test'
      exec('mvn test')
      
    when 'debug'
      exec('mvn -Dmaven.surefire.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8787 -Xnoagent -Djava.compiler=NONE" test')
      
    when 'build'
      exec('mvn clean install -DskipTests=true')
      
    when 'deploy'
      oldJars = Dir[NUXEO_PATH + '/nxserver/plugins/*.jar']
      puts "> Deleting", oldJars
      FileUtils.rm oldJars
      newJars = Dir['**/*.jar']
      puts "> Copying", newJars
      FileUtils.cp newJars, NUXEO_PLUGINS_PATH
      
    when 'run'
      exec(NUXEO_PATH + '/bin/nuxeoctl console')
    
    when 'reset'
      dataFolder = NUXEO_PATH + '/nxserver/data';
      puts "> Deleting", dataFolder
      FileUtils.rm_r dataFolder, :force => true
    
    else
      puts 'Available commands: test, debug, build, deploy, run, reset'

  end
  
end
