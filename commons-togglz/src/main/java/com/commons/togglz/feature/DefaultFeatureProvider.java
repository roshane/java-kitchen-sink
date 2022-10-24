package com.commons.togglz.feature;

import org.togglz.core.Feature;

import java.util.Arrays;
import java.util.List;

public class DefaultFeatureProvider implements IFeatureProvider{
    @Override
    public List<Class<? extends Feature>> features() {
        return Arrays.asList(
                NissanFeatures.class,
                TestFeature.class,
                ToyotaFeatures.class
        );
    }
}
