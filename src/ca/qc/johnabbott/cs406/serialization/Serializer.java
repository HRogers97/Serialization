/*
 * Copyright (c) 2017 Ian Clement.  All rights reserved.
 */

package ca.qc.johnabbott.cs406.serialization;


import ca.qc.johnabbott.cs406.serialization.io.Destination;
import ca.qc.johnabbott.cs406.serialization.io.Source;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Serializer class.
 *
 * - Sent objects to be serialized.
 * - Retrieves deserialized objects.
 * - Manage the interaction with the input source and output destination.
 *
 * @author Ian Clement (ian.clement@johnabbott.qc.ca)
 * @author TODO
 */
public class Serializer {

    private static final byte ALIAS_MARKER = (byte) 0xff;
    private static final byte ORIGINAL_MARKER = 0x00;

    private Source source;
    private Destination destination;

    private boolean optimizeReferences;
    private Map<Object, Integer> refs;
    private Map<Integer, Object> refsInv;

    /**
     * Construct a serializer.
     * @param source the input source.
     * @param destination the output destination.
     */
    public Serializer(Source source, Destination destination) {
        this(source, destination, false);
    }

    /**
     * Construct a serializer.
     * @param source the input source.
     * @param destination the output destination.
     * @param optimizeReferences reference optimization.
     */
    public Serializer(Source source, Destination destination, boolean optimizeReferences) {
        this.source = source;
        this.destination = destination;
        this.optimizeReferences = optimizeReferences;
        if(optimizeReferences) {
            refs = new IdentityHashMap<>();
            refsInv = new HashMap<>();
        }
    }

    /**
     * Write a NULL record to the output destination.
     * @throws IOException
     */
    public void serializeNull() throws IOException {
        destination.write(SerializableInstance.SERIAL_NULL_ID);
    }

    /**
     * Write a record to the output destination for the serializable.
     * @param s the object to serialize
     * @throws IOException
     */
    public void serialize(Serializable s) throws IOException {
        destination.write(s.getSerialId());
        s.writeTo(this);

    }

    /**
     * Read a record from the input source and return the object it represents.
     * @return the deserialized object.
     * @throws IOException
     * @throws SerializationException
     */
    public Serializable deserialize() throws IOException, SerializationException {
        byte serialId = source.read();
        Serializable s = SerializableInstance.byId(serialId);
        if(s != null)
            s.readFrom(this);
        return s;
    }

    /**
     * Write bytes to the output destination.
     * @param bytes the bytes to write.
     * @throws IOException
     */
    public void write(byte[] bytes) throws IOException {
        destination.write(bytes);
    }

    /**
     * Write byte to the output destination.
     * @param b the byte to write.
     * @throws IOException
     */
    public void write(byte b) throws IOException {
        destination.write(b);
    }


    /**
     * Read a length `n` from the source (as an int),
     * and use this length as the number of bytes to read and return
     * @return the bytes read.
     * @throws IOException
     */
    public byte[] readNext() throws IOException {
        byte[] bytes = new byte[Integer.BYTES];
        source.read(bytes, Integer.BYTES);
        int n = ByteBuffer.wrap(bytes).getInt();
        bytes = new byte[n];
        source.read(bytes, n);
        return bytes;
    }

    /**
     * Read the next byte from the input source.
     * @return the bytes read.
     * @throws IOException
     */
    public byte read() throws IOException {
        return source.read();
    }

    /**
     * Read the next bytes from the source into the input array
     * @param bytes
     * @return
     * @throws IOException
     */
    public int read(byte[] bytes) throws IOException {
        return read(bytes, bytes.length);
    }

    /**
     * Read the next bytes from the input source into the input array up to specified length.
     * @param bytes
     * @param length
     * @return
     * @throws IOException
     */
    public int read(byte[] bytes, int length) throws IOException {
        return source.read(bytes, length);
    }

    /**
     * Close the serializer. Closes the source and/or destination.
     * @throws IOException
     */
    public void close() throws IOException {
        if(source != null)
            source.close();
        if(destination != null)
            destination.close();
    }

}
