A fuse intent demo for Frascati.

The fuse intent can be configured to accept a defined number of request in a given period of time. If the received number of requests exceed the max request number, the fuse block and do not accept more requests.
The fuse auto-rearm while no requests are received in the defined time period.

This intent can only work with a Frascati executable project.

Frascati 1.2 and above is required to run this software.

---

Installation instructions :

- Create an 'intent' folder in the "sca-apps" folder.
- Copy in this folder the fuse intent (only the class and the resources files must be copied, not the jar file) :
	*	Fuse intent
- Copy the intents jar's in the Frascati lib folder.

---
