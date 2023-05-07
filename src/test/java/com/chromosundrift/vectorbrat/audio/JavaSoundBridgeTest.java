package com.chromosundrift.vectorbrat.audio;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.MissingAudioDevice;
import com.chromosundrift.vectorbrat.VectorBratException;
import junit.framework.AssertionFailedError;
import org.junit.Ignore;
import org.junit.Test;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.join;

public class JavaSoundBridgeTest {

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

    void assertDevicesPresent(List<String> devices) {
        JavaSoundBridge jsb = new JavaSoundBridge();
        List<String> missing = devices.stream()
                .filter(deviceName -> jsb.getMixer(deviceName).isEmpty())
                .collect(Collectors.toList());
        if (!missing.isEmpty()) {
            throw new AssertionFailedError("missing devices: %s".formatted(join(", ", missing)));
        }
    }

    @Ignore
    @Test
    public void tryDataLineInfos() throws Exception {

        AudioFormat f = new AudioFormat(96000f, 32, 2, false, true);

        TargetDataLine line;
        InputStream inputStream = SineWaveInputStream.create(440d, 96000);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputStream);

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
        Mixer m = new JavaSoundBridge().getMixerOrDie(Config.DEFAULT_XY);
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