package com.commons.togglz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.util.NamedFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MicsTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(MicsTest.class);

    @Test
    public void testFeatureStateSerializer() throws JsonProcessingException {
        FeatureState featureState = new FeatureState(new NamedFeature("TEST_FEATURE"), false);
        final String actual = mapper.writeValueAsString(featureState);
        String expected = "{\"feature\":{\"active\":true},\"enabled\":false,\"strategyId\":null,\"parameterNames\":[],\"parameterMap\":{},\"users\":[]}";
        assertEquals(expected, actual);
    }

    @Test
    public void testFeatureStateDeserlializer() throws JsonProcessingException {
        final String jsonFeatureState = "{\"feature\":{\"active\":true},\"enabled\":false,\"strategyId\":null,\"users\":[],\"parameterNames\":[],\"parameterMap\":{}}";
        FeatureState expected = new FeatureState(new NamedFeature("TEST_FEATURE"), false);
        final FeatureState actual = mapper.readValue(jsonFeatureState, FeatureState.class);
        assertEquals(expected, actual);
    }

}