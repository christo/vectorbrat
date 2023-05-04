package com.chromosundrift.vectorbrat.javasound;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.MissingAudioDevice;
import org.junit.Test;

import javax.sound.sampled.Mixer;

import static org.junit.Assert.*;

public class JavaSoundDemoTest {

    @Test
    public void es9Present() throws MissingAudioDevice {
        Mixer mixer = JavaSoundDemo.getMixer(Config.ES9);
        assertEquals(mixer.getMixerInfo().getName(), Config.ES9);
    }
}