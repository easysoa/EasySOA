package org.easysoa.registry.types.java;

import org.easysoa.registry.types.Deliverable;

public interface MavenDeliverable extends Deliverable {

    public static final String NATURE = "maven";

    String getGroupId();

    String getArtifactId();

}