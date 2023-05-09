package com.chromosundrift.vectorbrat.audio;

import com.google.common.collect.ImmutableList;
import org.jaudiolibs.audioservers.AudioConfiguration;
import org.jaudiolibs.audioservers.AudioServerProvider;
import org.jaudiolibs.audioservers.ext.ClientID;
import org.jaudiolibs.audioservers.ext.Connections;
import org.jaudiolibs.audioservers.ext.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.Util;

public class ServiceBridge {

    private static final Logger logger = LoggerFactory.getLogger(ServiceBridge.class);

    static {
        Util.setSystemLibraryPath();
    }

    public static final String JACK = "JACK";
    public static final String JAVASOUND = "JavaSound";
    private final String backend;

    /**
     * The root accessor of sound servers.
     *
     * @param backend ry {@link ServiceBridge#JACK} or {@link ServiceBridge#JAVASOUND}.
     */
    public ServiceBridge(String backend) {
        this.backend = backend;
    }

    public static AudioConfiguration getStereoAudioConfiguration() {
        AudioConfiguration audioConfig = new AudioConfiguration(
                Config.DEFAULT_SAMPLE_RATE,
                0,
                2,
                256,
                new ClientID("Lissajou"),
                Connections.OUTPUT);
        logger.info("created audio config: " + audioConfig);
        return audioConfig;
    }

    public AudioServerProvider getProvider() throws MissingAudioDevice {
        for (AudioServerProvider p : ServiceLoader.load(AudioServerProvider.class)) {
            if (backend.equals(p.getLibraryName())) {
                logger.info("found provider " + p);
                return p;
            }
        }
        throw new MissingAudioDevice(JACK);
    }

    public ImmutableList<AudioConfiguration> getConfigurations() throws MissingAudioDevice {
        Iterator<AudioConfiguration> iterator = ServiceLoader.load(AudioConfiguration.class).iterator();
        ImmutableList<AudioConfiguration> configurations = ImmutableList.copyOf(iterator);
        System.out.println("configurations:");
        for (AudioConfiguration c : configurations) {
            // dump. TODO fix this
            System.out.println("c = " + c);
        }
        return configurations;
    }

    public List<Device> getDevices() throws MissingAudioDevice {
        List<Device> all = new ArrayList<>();
        for (Device dev : getProvider().findAll(Device.class)) {
            System.out.println(dev.getName() + " (inputs: " + dev.getMaxInputChannels() + ", outputs: " + dev.getMaxOutputChannels() + ")");
            all.add(dev);
        }
        return all;
    }
}
