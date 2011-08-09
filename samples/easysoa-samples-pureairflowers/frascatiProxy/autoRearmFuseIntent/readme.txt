A fuse intent demo for Frascati.

The fuse intent can be configured to accept a defined number of request in a given period of time. If the received number of requests exceed the max request number, the fuse block and do not accept more requests.
The fuse auto-rearm while no requests are received in the defined time period.

The intent parameters can be modified in the autorearmFuse.composite file.

  * <sca:property name="maxRequestsNumber">10</sca:property> : Max number of requests 
  * <sca:property name="maxTimePeriod">120000</sca:property> : Period of time (in milliseconds)

When this parameters are modified, you need to build the project.

Use the "mvn clean install" command to build the project.

This intent can only work with a Frascati executable project.
Frascati 1.4 is required to run this software.

