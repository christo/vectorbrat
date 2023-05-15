package com.chromosundrift.vectorbrat.audio.audioservers;

import org.jaudiolibs.audioservers.AudioServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServerRunner {

    private static final Logger logger = LoggerFactory.getLogger(ServerRunner.class);

    private final Thread thread;
    private final AudioServer server;

    public ServerRunner(AudioServer server) {
        thread = new Thread(() -> {
            try {
                logger.info("server thread running");
                server.run();
            } catch (Exception ex) {
                logger.error("server spewed", ex);
            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        this.server = server;
    }

    public void start() {
        logger.info("starting server thread");
        thread.start();
    }

    public void stop() {
        if (server.isActive()) {
            logger.info("requesting server shutdown");
            server.shutdown();
        } else {
            logger.warn("server not active, not requesting shutdown");
        }
    }
}
