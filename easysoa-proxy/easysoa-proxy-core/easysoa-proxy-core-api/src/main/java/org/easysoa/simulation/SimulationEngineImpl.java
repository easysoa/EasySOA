/**
 * 
 */
package org.easysoa.simulation;

import org.osoa.sca.annotations.Scope;

/**
 * Simulation engine
 * 
 * @author jguillemotte
 *
 */
@Scope("composite")
public class SimulationEngineImpl implements SimulationEngine {

    @Override
    public SimulationStore getSimulationStoreFromSuggestion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SimulationStore getExistingSimulationStore() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     *
     */
    public void match(){
        
    }

}
