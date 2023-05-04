package com.chromosundrift.vectorbrat.javasound;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.MissingAudioDevice;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class JavaSoundDemo {

    public static void main(String[] args) throws MissingAudioDevice {
        JavaSoundDemo demo = new JavaSoundDemo(true);
        System.out.println("----");
    }

    private Mixer mixerXY;
    private Mixer mixerRZ;
    private Mixer mixerGB;

    public JavaSoundDemo(boolean dump) throws MissingAudioDevice {

        if (dump) {
            Arrays.stream(AudioSystem.getMixerInfo()).map(JavaSoundDemo::dump).forEach(System.out::println);
        }

        mixerXY = getMixer(Config.deviceXY);
        mixerRZ = getMixer(Config.deviceRZ);
        mixerGB = getMixer(Config.deviceGB);

    }

    static Mixer getMixer(String deviceName) throws MissingAudioDevice {
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        return AudioSystem.getMixer(Arrays.stream(mixerInfo)
                .filter(info -> deviceName.equals(info.getName()))
                .findFirst()
                .orElseThrow(() -> new MissingAudioDevice(deviceName)));
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
}
