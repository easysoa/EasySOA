%~d0
 cd %~dp0
java -Xms256M -Xmx1024M -cp classpath.jar; talend_esb_tutorial.simpleprovider_0_1.SimpleProvider --context=Default %* 