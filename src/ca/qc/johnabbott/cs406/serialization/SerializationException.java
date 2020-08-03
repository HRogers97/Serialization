/*
 * Copyright (c) 2017 Ian Clement.  All rights reserved.
 */

package ca.qc.johnabbott.cs406.serialization;

/**
 * Exception thrown when a serialization error occurs.
 * @author Ian Clement
 */
public class SerializationException extends Exception {
    public SerializationException() {
        super();
    }

    public SerializationException(String message) {
        super(message);
    }
}
