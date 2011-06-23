package fr.inria.galaxy.j1.scenario1;

/**
 * Interface of the orchestration component. 
 */
public interface Trip {
  public String process( String activity,
                                   String userSentence,
                                   double rateTreshold);
}
