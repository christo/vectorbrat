package com.chromosundrift.vectorbrat.audio;

import org.jaudiolibs.audioservers.AudioServerProvider;
import org.jaudiolibs.audioservers.ext.Device;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import com.chromosundrift.vectorbrat.Util;

public class ServiceBridge {

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

    public AudioServerProvider getProvider() throws MissingAudioDevice {
        for (AudioServerProvider p : ServiceLoader.load(AudioServerProvider.class)) {
            if (backend.equals(p.getLibraryName())) {
                return p;
            }
        }
        throw new MissingAudioDevice(JACK);
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
