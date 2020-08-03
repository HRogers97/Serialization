package ca.qc.johnabbott.cs406;

import ca.qc.johnabbott.cs406.collections.Either;
import ca.qc.johnabbott.cs406.collections.list.LinkedList;
import ca.qc.johnabbott.cs406.collections.map.HashMap;
import ca.qc.johnabbott.cs406.collections.set.TreeSet;
import ca.qc.johnabbott.cs406.serialization.util.SInteger;
import ca.qc.johnabbott.cs406.serialization.util.SString;

public class TestingStuff {

    public static void main(String arg[]) {
        Either.LeftEither<SInteger, SString> left = new Either.LeftEither<>(new SInteger(5));
        Either.RightEither<SInteger, SString> right = new Either.RightEither<>(new SString("Five"));
    }
}
