package org.easysoa.registry.rest.client.types.java;

import org.easysoa.registry.rest.client.types.DeliverableInformation;
import org.easysoa.registry.types.java.MavenDeliverable;

public class MavenDeliverableInformation extends DeliverableInformation {

    public MavenDeliverableInformation(String name) throws Exception {
        super(name);
        this.setNature(MavenDeliverable.NATURE);
    }

 }
