package com.chromosundrift.vectorbrat.audio.jack;

import org.jaudiolibs.jnajack.JackClient;
import org.jaudiolibs.jnajack.JackShutdownCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JackShutdown implements JackShutdownCallback {
    private static final Logger logger = LoggerFactory.getLogger(JackShutdown.class);

    @Override
    public void clientShutdown(JackClient client) {
        logger.info("Jack shutdown initiated for client " + client.getName());
    }
}
