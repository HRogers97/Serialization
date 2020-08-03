package ca.qc.johnabbott.cs406.serialization.util;

import ca.qc.johnabbott.cs406.serialization.Serializable;
import ca.qc.johnabbott.cs406.serialization.SerializationException;
import ca.qc.johnabbott.cs406.serialization.Serializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

public class SDate implements Serializable, Comparable<SDate> {
    public static final byte SERIAL_ID = 0x04;
    private Date value;

    public SDate (){
        this.value = new Date();
    }

    public SDate (Date value){
        this.value = value;
    }

    public Date get(){
        return this.value;
    }

    @Override
    public byte getSerialId() {
        return SERIAL_ID;
    }

    @Override
    public void writeTo(Serializer s) throws IOException {
        // Convert date to string for serialization
        s.serialize(new SString(value.toString()));
    }

    @Override
    public void readFrom(Serializer s) throws IOException, SerializationException {
        // Get date from deserialized string
        String dateString = ((SString) s.deserialize()).get();

        value = new Date(dateString);
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean immutable() {
        return false;
    }

    @Override
    public int compareTo(SDate rhs) {
        return this.value.compareTo(rhs.value);
    }
}
