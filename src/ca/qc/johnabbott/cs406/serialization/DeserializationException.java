/*
 * Copyright (c) 2017 Ian Clement.  All rights reserved.
 */

package ca.qc.johnabbott.cs406.serialization;

/**
 * Exception thrown a deserialization error occurs.
 *
 * @author Ian Clement
 */
public class DeserializationException extends Exception {
    public DeserializationException() {
        super();
    }
    public DeserializationException(String message) {
        super(message);
    }
}
