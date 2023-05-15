package com.chromosundrift.vectorbrat.audioservers;

import com.google.common.collect.ImmutableList;
import junit.framework.AssertionFailedError;
import org.jaudiolibs.audioservers.AudioConfiguration;
import org.jaudiolibs.audioservers.ext.Device;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.join;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.Util;
import com.chromosundrift.vectorbrat.audio.MissingAudioDevice;
import com.chromosundrift.vectorbrat.audio.audioservers.ServiceBridge;

/**
 * Sanity test of the jack stack. Needs jackd running and various devices configured and connected.
 */
public class AudioServersTest {

    private static final Logger logger = LoggerFactory.getLogger(AudioServersTest.class);

    @BeforeClass
    public static void setup() {
        Util.setSystemLibraryPath();
    }

    @Test
    public void allDevicesPresent() throws MissingAudioDevice {
        assertDevicesPresent(Config.knownDevices());
    }

    @Test
    public void requiredDevicesPresent() throws MissingAudioDevice {
        assertDevicesPresent(Config.requiredDevices());
    }

    @Test
    public void expectedDevicesPresent() throws MissingAudioDevice {
        assertDevicesPresent(Config.expectedDevices());
    }

    void assertDevicesPresent(List<String> devices) throws MissingAudioDevice {
        ServiceBridge jb = new ServiceBridge(ServiceBridge.JACK);
        List<String> missing = new ArrayList<>(devices);
        missing.removeAll(jb.getDevices().stream()
                .peek(d -> logger.info(dump(d)))
                .map(Device::getName)
                .toList());
        if (!missing.isEmpty()) {
            throw new AssertionFailedError("missing devices: %s".formatted(join(", ", missing)));
        }
    }

    @Test
    public void testAudioConfigurations() throws MissingAudioDevice {
        ServiceBridge jb = new ServiceBridge(ServiceBridge.JACK);
        ImmutableList<AudioConfiguration> configurations = jb.getConfigurations();
        Assert.assertNotNull(configurations);
        Assert.assertFalse(configurations.isEmpty());
    }

    static String dump(Device d) {
        return "device: %s in(%s) out(%s)".formatted(d.getName(), d.getMaxInputChannels(), d.getMaxOutputChannels());
    }

}
