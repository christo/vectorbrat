package com.chromosundrift.vectorbrat.audio;

import junit.framework.AssertionFailedError;
import org.jaudiolibs.audioservers.ext.Device;
import org.jaudiolibs.jnajack.examples.SineAudioSource;
import org.jaudiolibs.jnajack.util.SimpleAudioClient;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.join;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.Util;

/**
 * Sanity test of the jack stack.
 */
public class JackTest {

    @BeforeClass
    public static void setup() {
        Util.setSystemLibraryPath();
    }


    @Test
    public void baep() throws Exception {
        SimpleAudioClient client = SimpleAudioClient.create("sine", new String[0],
                new String[]{"output-L", "output-R"}, true, true, new SineAudioSource());
        client.activate();
        long stopTime = System.currentTimeMillis() + 1000;
        while (System.currentTimeMillis() < stopTime) {
            Thread.sleep(10);
        }
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
                .peek(d -> System.out.println(dump(d)))
                .map(Device::getName)
                .toList());
        if (!missing.isEmpty()) {
            throw new AssertionFailedError("missing devices: %s".formatted(join(", ", missing)));
        }
    }

    @Test
    public void testClient() {

    }

    static String dump(Device d) {
        return "device: %s in(%s) out(%s)".formatted(d.getName(), d.getMaxInputChannels(),d.getMaxOutputChannels());
    }

}
