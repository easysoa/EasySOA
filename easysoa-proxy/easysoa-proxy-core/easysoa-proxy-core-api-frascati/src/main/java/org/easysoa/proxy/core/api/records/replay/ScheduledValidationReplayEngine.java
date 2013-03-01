/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */
package org.easysoa.proxy.core.api.records.replay;

import java.io.IOException;
import java.security.InvalidParameterException;
import org.easysoa.validation.ExchangeReplayController;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * Scheduled validation replay engine implementation
 * @author jguillemotte
 */
@Scope("composite")
public class ScheduledValidationReplayEngine implements ExchangeReplayController {

    // The replay engine
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
