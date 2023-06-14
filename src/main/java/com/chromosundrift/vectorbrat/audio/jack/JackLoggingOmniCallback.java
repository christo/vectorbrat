package com.chromosundrift.vectorbrat.audio.jack;

import org.jaudiolibs.jnajack.JackBufferSizeCallback;
import org.jaudiolibs.jnajack.JackClient;
import org.jaudiolibs.jnajack.JackClientRegistrationCallback;
import org.jaudiolibs.jnajack.JackGraphOrderCallback;
import org.jaudiolibs.jnajack.JackPortConnectCallback;
import org.jaudiolibs.jnajack.JackPortRegistrationCallback;
import org.jaudiolibs.jnajack.JackPosition;
import org.jaudiolibs.jnajack.JackSampleRateCallback;
import org.jaudiolibs.jnajack.JackShutdownCallback;
import org.jaudiolibs.jnajack.JackSyncCallback;
import org.jaudiolibs.jnajack.JackTimebaseCallback;
import org.jaudiolibs.jnajack.JackTransportState;
import org.jaudiolibs.jnajack.JackXrunCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience implementation of all Jack's callbacks that merely logs the invocations.
 */
public class JackLoggingOmniCallback implements
        JackBufferSizeCallback,
        JackClientRegistrationCallback,
        JackGraphOrderCallback,
        JackPortConnectCallback,
        JackPortRegistrationCallback,
        JackSampleRateCallback,
        JackShutdownCallback,
        JackSyncCallback,
        JackTimebaseCallback,
        JackXrunCallback {
    private static final Logger logger = LoggerFactory.getLogger(JackLoggingOmniCallback.class);

    @Override
    public void portsConnected(JackClient jackClient, String s, String s1) {
        logger.info("ports connected callback [%s|%s]".formatted(s, s1));
    }

    @Override
    public void portsDisconnected(JackClient jackClient, String s, String s1) {
        logger.info("ports disconnected callback [%s|%s]".formatted(s, s1));
    }

    @Override
    public void clientRegistered(JackClient jackClient, String s) {
        logger.info("client registration callback %s".formatted(s));
    }

    @Override
    public void clientUnregistered(JackClient jackClient, String s) {
        logger.info("client unregistration callback %s".formatted(s));
    }

    @Override
    public void buffersizeChanged(JackClient jackClient, int i) {
        logger.info("buffer size callback %s".formatted(i));

    }

    @Override
    public void graphOrderChanged(JackClient jackClient) {
        logger.info("graph order callback");
    }

    @Override
    public void portRegistered(JackClient jackClient, String s) {
        logger.info("port registered callback %s".formatted(s));
    }

    @Override
    public void portUnregistered(JackClient jackClient, String s) {
        logger.info("port deregistered callback %s".formatted(s));
    }

    @Override
    public void sampleRateChanged(JackClient jackClient, int i) {
        logger.info("sample rate callback %s".formatted(i));
    }

    @Override
    public void clientShutdown(JackClient jackClient) {
        logger.info("client shutdown callback");
    }

    @Override
    public boolean syncPosition(JackClient jackClient,
                                JackPosition jackPosition,
                                JackTransportState jackTransportState) {
        logger.info("sync position callback");
        return false;
    }

    @Override
    public void updatePosition(JackClient jackClient,
                               JackTransportState jackTransportState,
                               int i,
                               JackPosition jackPosition,
                               boolean b) {
        logger.info("update position callback");
    }

    @Override
    public void xrunOccured(JackClient jackClient) {
        logger.warn("xrun occurred callback");
    }
}
