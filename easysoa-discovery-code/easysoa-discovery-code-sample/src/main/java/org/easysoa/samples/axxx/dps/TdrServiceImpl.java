package org.easysoa.samples.axxx.dps;

import javax.inject.Inject;

import org.easysoa.samples.axxx.dcv.ClientService;

public class TdrServiceImpl implements TdrService {
    
    @Inject ClientService clientService;

    @Override
    public void updateTdr(Tdr tdr) {

    }

}
