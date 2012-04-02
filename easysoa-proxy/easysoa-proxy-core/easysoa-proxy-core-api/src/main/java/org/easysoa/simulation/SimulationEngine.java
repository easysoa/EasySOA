/**
 * 
 */
package org.easysoa.simulation;

/**
 * 
 * @author jguillemotte
 *
 */
public interface SimulationEngine {

    /**
     * Get a simulation store from suggestions
     * @return A simulation store
     */
    public SimulationStore getSimulationStoreFromSuggestion();
    
    /**
     * Get an existing simulation store
     * @return A simulation store
     */
    public SimulationStore getExistingSimulationStore();

}
