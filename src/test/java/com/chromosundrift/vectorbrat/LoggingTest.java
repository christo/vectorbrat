package com.chromosundrift.vectorbrat;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Test to check that java.util.logging is bridged to SLF4J.
 */
public class LoggingTest {

    private static final Logger logger = LoggerFactory.getLogger(LoggingTest.class);

    @Test
    public void testLoggingBridge() {

        Util.bridgeJulToSlf4j();
        java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(LoggingTest.class.getName());
        AtomicReference<String> mesg = new AtomicReference<>();
        LOGGER.addHandler(new java.util.logging.Handler() {
            @Override
            public void publish(java.util.logging.LogRecord record) {
                mesg.set(record.getMessage());
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        });
        String expectedMesg = "this is through JUL";
        LOGGER.info(expectedMesg);
        Assert.assertEquals(expectedMesg, mesg.get());
        logger.info("finished logging test");
    }
}
