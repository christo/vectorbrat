package com.chromosundrift.vectorbrat.audio;

import com.chromosundrift.vectorbrat.Util;
import org.jaudiolibs.jnajack.JackException;
import org.jaudiolibs.jnajack.examples.SineAudioSource;
import org.jaudiolibs.jnajack.util.SimpleAudioClient;
import org.junit.Test;

/**
 * Sanity test of the jack stack.
 */
public class JackTest {

    @Test
    public void baep() throws Exception {
        Util.setSystemLibraryPath();
        SimpleAudioClient client = SimpleAudioClient.create("sine", new String[0],
                new String[]{"output-L", "output-R"}, true, true, new SineAudioSource());
        client.activate();
        while (true) {
            Thread.sleep(1000);
        }
    }
}
