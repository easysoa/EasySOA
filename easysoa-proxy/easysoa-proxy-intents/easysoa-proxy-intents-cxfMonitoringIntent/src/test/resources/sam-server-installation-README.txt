The fastest way to install the SAM server in EasySOA is to copy the files and folfers included in the sam-server-installation folder in the easysoa-distribution/easysoa folder.

A configured MySQL database with the following parameters is required with this method. 

Address and port : "localhost:3306"
Database name : "samDB"
Login : "root"
Password : "" 

Warning : the storage engine for the database tables must be 'MyIsam'. If it is not the case, the SAM server will send back a success response but the events will not be persisted in the database.

-----------------------------------------

The manually installation way is described here :


Installation Talend Service Activity Monitoring server (working with mysql) in Easysoa :

* Copy mysql-connector-java-5.1.22-bin.jar in serviceRegistry/lib
* Copy sam-server-war.war in serviceRegistry/webapps
* Copy create_mysql.sql file in serviceRegistry/webapps/sam-server-war/WEB-INF or create manually the base (base name => samDB, prefered solution) in mysql with this script. 
  Warning : the storage engine for the database tables must be 'MyIsam'. If it is not the case, the SAM server will send back a success response but the events will not be persisted in the database.
  The MySQL server is not included in EasySOA.

* Open the tomcat context.xml file in serviceRegistry/conf/ and add this resource tag :

  <Resource name="jdbc/datasource" auth="Container"
    type="javax.sql.DataSource" username="root" password=""
    driverClassName="com.mysql.jdbc.Driver"
    url="jdbc:mysql://localhost:3306/samDB"
    maxActive="8" maxIdle="30" maxWait="10000"/>

* Modify the file serviceRegistry/webapps/sam-server-war/WEB-INF/logserver.properties : comment the derby block and uncomment the mysql block

  ...
  #for Derby (by default)
  #db.datasource=java:comp/env/jdbc/datasource
  #db.dialect=derbyDialect
  #db.recreate=true
  #db.createsql=create.sql

  #for Mysql
  db.datasource=java:comp/env/jdbc/datasource
  db.dialect=mysqlDialect
  db.recreate=false
  db.createsql=create_mysql.sql
  ...

* To check if the SAM server is started, go to : http://localhost:8080/sam-server-war/
