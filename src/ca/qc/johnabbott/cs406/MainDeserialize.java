package ca.qc.johnabbott.cs406;

import ca.qc.johnabbott.cs406.collections.Either;
import ca.qc.johnabbott.cs406.collections.list.LinkedList;
import ca.qc.johnabbott.cs406.collections.map.HashMap;
import ca.qc.johnabbott.cs406.serialization.io.BufferedChannel;
import ca.qc.johnabbott.cs406.serialization.SerializationException;
import ca.qc.johnabbott.cs406.serialization.Serializer;
import ca.qc.johnabbott.cs406.serialization.util.*;
import ca.qc.johnabbott.cs406.collections.set.TreeSet;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Deserialize example.
 */
public class MainDeserialize {
    public static void main(String arg[]) throws IOException, SerializationException {

        Serializer serializer = new Serializer(
                new BufferedChannel(
                        new RandomAccessFile("foo.bin", "rw").getChannel()
                        , BufferedChannel.Mode.READ
                ), null);

        SInteger i = (SInteger) serializer.deserialize();
        System.out.println(serializer.deserialize());
        SString s = (SString) serializer.deserialize();
        System.out.println(serializer.deserialize());
        Box<SString> bs = (Box<SString>) serializer.deserialize();
        System.out.println(serializer.deserialize());
        SDate d = (SDate) serializer.deserialize();
        System.out.println(serializer.deserialize());
        Grade g = (Grade) serializer.deserialize();
        System.out.println(serializer.deserialize());
        Either.LeftEither<SInteger, SString> el = (Either.LeftEither<SInteger, SString>) serializer.deserialize();
        System.out.println(serializer.deserialize());
        Either.RightEither<SInteger, SString> er = (Either.RightEither<SInteger, SString>) serializer.deserialize();
        System.out.println(serializer.deserialize());
        LinkedList<SInteger> ll = (LinkedList<SInteger>) serializer.deserialize();
        System.out.println(serializer.deserialize());
        HashMap<SInteger, SString> hm = (HashMap<SInteger, SString>) serializer.deserialize();
        System.out.println(serializer.deserialize());
        TreeSet<SInteger> ts = (TreeSet<SInteger>) serializer.deserialize();
        System.out.println(serializer.deserialize());

        System.out.println(i);
        System.out.println(s);
        System.out.println(bs);
        System.out.println(d);
        System.out.println(g);
        System.out.println(el);
        System.out.println(er);
        System.out.println(ll);
        System.out.println(hm);
        System.out.println(ts);

        serializer.close();

    }
}
