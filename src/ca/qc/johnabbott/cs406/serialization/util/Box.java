/*
 * Copyright (c) 2017 Ian Clement.  All rights reserved.
 */


package ca.qc.johnabbott.cs406.serialization.util;

import ca.qc.johnabbott.cs406.serialization.Serializable;
import ca.qc.johnabbott.cs406.serialization.SerializationException;
import ca.qc.johnabbott.cs406.serialization.Serializer;

import java.io.IOException;

/**
 * Box class
 *
 * - Stores a value in a box. Example of serialization for generic classes.
 *
 * Format:
 *
 * 1.  Serial representation of the value.
 *
 */
public class Box<T extends Serializable> implements Serializable {

    public static final byte SERIAL_ID = 0x03;

    private T value;

    public Box() {
    }

    public Box(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Box{" + value.toString() + "}";
    }

    @Override
    public byte getSerialId() {
        return SERIAL_ID;
    }

    @Override
    public void writeTo(Serializer s) throws IOException {
        s.serialize(value);
    }

    @Override
    public void readFrom(Serializer s) throws IOException, SerializationException {
        value = (T) s.deserialize();
    }

    @Override
    public boolean immutable() {
        return false; // due to setter above
    }

}
