package io.quarkus.deployment;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * This is used currently only to suppress warnings about unknown properties
 * when the user supplies something like: -Dquarkus.test.native-image-profile=someProfile
 *
 * TODO refactor code to actually use these values
 */
@ConfigRoot
public class TestConfig {

    /**
     * Number of seconds to wait for the native image to built during testing
     */
    @ConfigItem(defaultValue = "300")
    Integer nativeImageWaitTime;

    /**
     * The profile to use when testing the native image
     */
    @ConfigItem(defaultValue = "prod")
    String nativeImageProfile;
}
