cd `dirname $0`
 ROOT_PATH=`pwd`
java -Xms128M -Xmx256M -cp classpath.jar: talend_esb_tutorial.simpleprovider_0_1.SimpleProvider --context=Default $* 
