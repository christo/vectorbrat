package com.chromosundrift.vectorbrat.audio.javasound;

import junit.framework.AssertionFailedError;
import org.junit.Ignore;
import org.junit.Test;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.join;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.audio.MissingAudioDevice;

/**
 * Requires audio devices connected and jackd running.
 */
public class JavaSoundBridgeTest {

    @Ignore
    @Test
    public void allDevicesPresent() {
        assertDevicesPresent(Config.knownDevices());
    }

    void assertDevicesPresent(List<String> devices) {
        JavaSoundBridge jsb = new JavaSoundBridge();
        List<String> missing = devices.stream()
                .filter(deviceName -> jsb.getMixer(deviceName).isEmpty())
                .collect(Collectors.toList());
        if (!missing.isEmpty()) {
            throw new AssertionFailedError("missing devices: %s".formatted(join(", ", missing)));
        }
    }


    @Test
    public void tryDataLineInfos() throws Exception {

        AudioFormat f = new AudioFormat(96000f, 24, 2, true, true);

        TargetDataLine line;


        DataLine.Info info = new DataLine.Info(TargetDataLine.class, f); // format is an AudioFormat object
        if (!AudioSystem.isLineSupported(info)) {
            throw new VectorBratException("line unsupported: " + info);
        }
        // Obtain and open the line.
        line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(f);
    }

    @Test
    public void audioFormatCombinations() {
        List<Integer> bits = Arrays.asList(8, 16, 24, 32);
        List<Boolean> bigEndian = Arrays.asList(true, false);
        List<Boolean> signed = Arrays.asList(true, false);
        List<Integer> bitRates = Arrays.asList(44100, 48000, 96000);
        List<String> devices = Config.knownDevices();
        // run all combinations to find supported formats
    }

    @Test
    public void testListFormats() throws MissingAudioDevice {
        Mixer m = new JavaSoundBridge().getMixerOrDie(Config.ES9);
        Line.Info[] sourceLines = m.getSourceLineInfo();
        Line.Info[] targetLines = m.getTargetLineInfo();

        System.out.println("sourceLines");
        Function<Line.Info, String> dump = info -> JavaSoundBridge.dumpFormats("\n\tformat: ", (DataLine.Info) info);
        Arrays.stream(sourceLines).map(dump).forEach(System.out::println);
        System.out.println("targetLines");
        Arrays.stream(targetLines).map(dump).forEach(System.out::println);

    }

    @Test
    public void dumpAudioSystem() {
        JavaSoundBridge.dump();
    }

}