package com.chromosundrift.vectorbrat;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class AudioLink {

    private static int SAMPLE_RATE = 96000;
    private static int CHANNEL_X = 9;
    private static int CHANNEL_Y = 10;
    private static int CHANNEL_R = 11;
    private static int CHANNEL_G = 12;
    private static int CHANNEL_B = 13;
    private static String audioDevice = "ES-9";
    private final Mixer mixer;

    public static void main(String[] args) throws RuntimeException {



        System.out.println("----");
    }

    static String dump(Mixer.Info i) {
        String n = i.getName();
        String v = i.getVendor();
        String d = i.getDescription();
        Mixer mixer = AudioSystem.getMixer(i);
        String targetlines = dump("\n  target:", mixer.getTargetLines());
        String sourceLines = dump("\n  source:", mixer.getSourceLines());
        return "mixer: '%s' (%s) %s%s%s".formatted(n, v, d, targetlines, sourceLines);
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

    public AudioLink(boolean dump) {

        Consumer<Mixer.Info> mixerDump = dump ? mi -> System.out.println(dump(mi)) : x -> {};

        // get mixer
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        Mixer mixer = AudioSystem.getMixer(Arrays.stream(mixerInfo)
                .filter(mi -> !mi.getName().contains("MacBook"))
                .peek(mixerDump)
                .filter(info -> audioDevice.equals(info.getName()))
                .findFirst().orElseThrow());
        this.mixer = mixer;
    }
}
