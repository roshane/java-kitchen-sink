package com.commons.togglz.feature;

import org.togglz.core.Feature;

import java.util.List;

public interface IFeatureProvider {

    List<Class<? extends Feature>> features();
}
