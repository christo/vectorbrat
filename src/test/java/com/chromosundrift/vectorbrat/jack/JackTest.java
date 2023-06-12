package com.chromosundrift.vectorbrat.jack;

import org.jaudiolibs.jnajack.examples.SineAudioSource;
import org.jaudiolibs.jnajack.util.SimpleAudioClient;
import org.junit.BeforeClass;
import org.junit.Test;

import com.chromosundrift.vectorbrat.Util;

/**
 * Test using jack directly.
 */
public class JackTest {

    @BeforeClass
    public static void setup() {
        Util.setSystemLibraryPath();
    }

    /**
     * Makes a one second beep through jack using its sound device. Requires jackd to be running.
     *
     * @throws Exception
     */
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
}
