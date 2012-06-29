package org.easysoa.validation;

import java.io.IOException;
import java.security.InvalidParameterException;

public interface ExchangeReplayController {

    /**
     * Replays a run synchronously.
     * 
     * @param runName The run name
     * @param environmentName The environment to which notifications have to be sent 
     * @throws InvalidParameterException If the run doesn't exist
     * @throws IOException If something went wrong during the replay
     */
    void replayRecord(String runName, String environmentName)
            throws InvalidParameterException, IOException;

    /**
     * @return The name of all availables runs.
     */
    String[] getAllRunNames();

}
