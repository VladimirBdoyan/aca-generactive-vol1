package com.example.acageneractivweb.mock;



import com.example.acageneractivweb.model.Configuration;
import com.example.acageneractivweb.model.Resolution;

import java.util.Random;

public final class ConfigurationMock {

    public static Configuration getConfiguration() {
        return new Configuration(Resolution.values()[new Random().nextInt(Resolution.values().length - 1)]);
    }

    private ConfigurationMock() {

    }
}
