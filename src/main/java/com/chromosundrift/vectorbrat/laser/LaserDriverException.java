package com.chromosundrift.vectorbrat.laser;

import com.google.common.base.Joiner;
import org.jaudiolibs.jnajack.JackStatus;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.chromosundrift.vectorbrat.VectorBratException;

public class LaserDriverException extends VectorBratException {

    private static final Map<JackStatus, String> STATUS_MEANINGS = new HashMap<>();

    // future: patch jnajack JackStatus and submit pull request, also add failure field
    static {
        STATUS_MEANINGS.put(JackStatus.JackFailure, "Overall operation failed.");
        STATUS_MEANINGS.put(JackStatus.JackInitFailure, "Unable to initialize client.");
        STATUS_MEANINGS.put(JackStatus.JackInvalidOption, "The operation contained an invalid or unsupported option.");
        STATUS_MEANINGS.put(JackStatus.JackLoadFailure, "Unable to load internal client.");
        STATUS_MEANINGS.put(JackStatus.JackNoSuchClient, "Requested client does not exist.");
        STATUS_MEANINGS.put(JackStatus.JackServerError, "Communication error with the JACK server.");

        STATUS_MEANINGS.put(JackStatus.JackServerFailed, "Unable to connect to the JACK server.");
        STATUS_MEANINGS.put(JackStatus.JackShmFailure, "Unable to access shared memory");
        STATUS_MEANINGS.put(JackStatus.JackVersionError, "Client's protocol version does not match");
        STATUS_MEANINGS.put(JackStatus.JackNameNotUnique, "The desired client name was not unique.");
        // not an error
        STATUS_MEANINGS.put(JackStatus.JackServerStarted,
                "The JACK server was started as a result of this operation. Otherwise, it was running already");
    }

    public LaserDriverException(Exception e) {
        super(e);
    }

    public LaserDriverException(String message) {
        super(message);
    }

    public LaserDriverException(String message, Throwable cause) {
        super(message, cause);
    }

    public LaserDriverException(String message, EnumSet<JackStatus> statuses) {
        super(message + getMessage(statuses));
    }

    public LaserDriverException(String message, EnumSet<JackStatus> statuses, Throwable cause) {
        super(message + getMessage(statuses), cause);
    }

    private static String getMessage(EnumSet<JackStatus> statuses) {
        return "JackStatus: %s".formatted(Joiner.on(", ")
                .join(statuses.stream().map(STATUS_MEANINGS::get).iterator()));
    }
}
