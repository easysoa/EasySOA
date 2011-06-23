package fr.inria.galaxy.j1.scenario1;

/**
 * A simple client used to validate the architecture of this demo.
 * 
 * @author Christophe Demarey.
 */
public class SimpleClient implements Runnable {
  /** The reference to the trip service */
  private Trip tripService;

  public void run() {
    tripService.process("ART", "I would like a bier!", 0.5);
  }

  /** Set the reference of the service. */
  public void setTrip(Trip service) {
    this.tripService = service;
  }
}
