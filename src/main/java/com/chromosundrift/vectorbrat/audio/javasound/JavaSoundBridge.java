package com.chromosundrift.vectorbrat.audio.javasound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.audio.MissingAudioDevice;
import com.chromosundrift.vectorbrat.audio.SoundBridge;

/**
 * JavaSound implemention for getting required system sound resources.
 */
public class JavaSoundBridge {

    private static final Logger logger = LoggerFactory.getLogger(JavaSoundBridge.class);

    private static final Function<Optional<Mixer.Info>, Optional<Mixer>> systemMixerGetter = info -> info.map(AudioSystem::getMixer);
    private final Optional<Mixer> mixerXY;
    private final Optional<Mixer> mixerRZ;
    private final Optional<Mixer> mixerGB;
    private final Function<Optional<Mixer.Info>, Optional<Mixer>> mixerGetter;
    private Function<String, Mixer> mixerByName;

    public JavaSoundBridge(Function<Optional<Mixer.Info>, Optional<Mixer>> mixerGetter) {

        this.mixerGetter = mixerGetter;
        mixerXY = getMixer(Config.DEFAULT_XY);
        mixerRZ = getMixer(Config.DEFAULT_RZ);
        mixerGB = getMixer(Config.DEFAULT_GB);
    }

    public JavaSoundBridge() {
        this(systemMixerGetter);
    }

    public static void main(String[] args) {
        JavaSoundBridge demo = new JavaSoundBridge();
        logger.info("finished");
    }

    public static void dump() {
        Arrays.stream(AudioSystem.getMixerInfo()).map(JavaSoundBridge::dump).forEach(System.out::println);
    }

    private static String dump(Line.Info[] lineInfos) {
        StringJoiner sj = new StringJoiner(", ", "(", ")");
        Arrays.stream(lineInfos).forEachOrdered(li -> sj.add(li.toString()));
        return sj.toString();
    }

    private static String dump(Mixer.Info i) {
        String n = i.getName();
        String v = i.getVendor();
        String d = i.getDescription();
        Mixer mixer = AudioSystem.getMixer(i);

        String sli = dump(mixer.getSourceLineInfo());
        String tli = dump(mixer.getTargetLineInfo());

        int maxOutLines = mixer.getMaxLines(Port.Info.SPEAKER);
        int maxInLines = mixer.getMaxLines(Port.Info.LINE_IN);
        String targetlines = dump("\n  target:", mixer.getTargetLines());
        String sourceLines = dump("\n  source:", mixer.getSourceLines());
        return "mixer: '%s' (%s) %s \n :: sli %s \n :: tli %s \n :: %s out (%s) \n :: %s in (%s)"
                .formatted(n, v, d, sli, tli, maxOutLines, targetlines, maxInLines, sourceLines);
    }

    private static String dump(String prefix, Line[] lines) {
        return Arrays.stream(lines).map(line -> dump(prefix, line)).collect(Collectors.joining());
    }

    public static String dumpFormats(String prefix, DataLine.Info dli) {
        var formatJoin = Collectors.joining(prefix, prefix, "");
        return Arrays.stream(dli.getFormats()).map(JavaSoundBridge::toString).collect(formatJoin);
    }

    public static String toString(AudioFormat audioFormat) {
        return audioFormat.toString();
    }

    private static String dump(String prefix, Line i) {
        Control[] controls = i.getControls();
        StringBuilder sb = new StringBuilder(prefix);
        sb.append("Line[").append(controls.length).append("]:(");

        for (int j = 0; j < controls.length; j++) {
            if (j > 0) {
                sb.append(", ");
            }
            sb.append(j).append(":").append(controls[j].getType());
        }
        return sb.append(")").toString();
    }

    public List<Optional<Mixer>> getDeviceStatus() {
        return Config.knownDevices().stream().map(this::getMixer).collect(Collectors.toList());
    }

    public Mixer getMixerOrDie(final String deviceName) throws MissingAudioDevice {
        return getMixer(deviceName).orElseThrow(() -> new MissingAudioDevice(deviceName));
    }

    /**
     * Will return one Mixer matching the given name precisely or empty. If more than one mixer match the name,
     * returns an unspecified single one.
     */
    public Optional<Mixer> getMixer(final String deviceName) {
        return mixerGetter.apply(Arrays.stream(AudioSystem.getMixerInfo())
                .filter(info -> deviceName.equals(info.getName()))
                .findAny());
    }
}
