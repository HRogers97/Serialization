package ca.qc.johnabbott.cs406.serialization.util;

import ca.qc.johnabbott.cs406.serialization.Serializable;
import ca.qc.johnabbott.cs406.serialization.SerializationException;
import ca.qc.johnabbott.cs406.serialization.Serializer;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

/**
 * Represents a grade.
 *
 * @author Ian Clement (ian.clement@johnabbott.qc.ca)
 * @since 2018-04-29
 */
public class Grade implements Serializable {

    private String name;
    private int result;
    private Date date;

    public static final byte SERIAL_ID = 0x05;

    public Grade(String name, int result, Date date) {
        this.name = name;
        this.result = result;
        this.date = date;
    }

    public Grade() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "name='" + name + '\'' +
                ", result=" + result +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade = (Grade) o;
        return result == grade.result &&
                Objects.equals(name, grade.name) &&
                Objects.equals(date, grade.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, result, date);
    }

    @Override
    public byte getSerialId() {
        return SERIAL_ID;
    }

    @Override
    public void writeTo(Serializer s) throws IOException {
        // Make serializable versions of all the elements
        SString sName = new SString(name);
        SInteger sResult = new SInteger(result);
        SDate sDate = new SDate(date);

        // Serialize the new serializable variables
        s.serialize(sName);
        s.serialize(sResult);
        s.serialize(sDate);
    }

    @Override
    public void readFrom(Serializer s) throws IOException, SerializationException {
        // Deserialize into serializable variables
        SString sName = (SString) s.deserialize();
        SInteger sResult = (SInteger) s.deserialize();
        SDate sDate = (SDate) s.deserialize();

        // Get values from serializable variables
        this.name = sName.get();
        this.result = sResult.get();
        this.date = sDate.get();
    }

    @Override
    public boolean immutable() {
        return true;
    }
}
