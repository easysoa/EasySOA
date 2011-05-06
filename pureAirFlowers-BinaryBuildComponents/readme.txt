This project contains some additional components to build a binary distribution of the Pure Air Flowers demo.

Instructions :

- Copy the files and folder structure contained in the folder distrib in your final distrib structure.
- Unzip the archive "frascati-runtime-1.4-SNAPSHOT-bin.zip" in the "frascati-proxy" folder
- Copy the three jar files "autoRearmFuseIntent-1.0-SNAPSHOT.jar", "logIntent-1.0-SNAPSHOT.jar" and "proxy-1.0-SNAPSHOT.jar" in the "frascati-proxy/sca-apps" folder.
- Copy the two jar files "autoRearmFuseIntent-1.0-SNAPSHOT.jar" and "logIntent-1.0-SNAPSHOT.jar" in the "frascati-proxy/lib" folder.
- copy the "pureAirFlowerServer-1.0-SNAPSHOT-with-dep.jar" in the "cxf-server" folder

You can remove the 2 empty Readme files in "cxf-server" and "frascati-proxy" folders. 

To launch the servers :

Execute in this order :

- "./start_cxf_server.sh"
- "./start_frascati_proxy.sh"


Note : if the cxf-server is not started first, the frascati-proxy will not start !


