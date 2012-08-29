package org.easysoa.proxy.core.api.records.replay;

import java.io.IOException;
import java.security.InvalidParameterException;
import org.easysoa.validation.ExchangeReplayController;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

@Scope("composite")
public class ScheduledValidationReplayEngine implements ExchangeReplayController {

    @Reference
    ReplayEngine replayEngine;
    
    @Override
    public void replayRecord(String runName, String environmentName) throws InvalidParameterException, IOException {
        replayEngine.replayRunRecords(runName, environmentName);
    }

    @Override
    public String[] getAllRunNames() {
        return replayEngine.getAllRunNamesArray();
    }

}
