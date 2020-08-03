package ca.qc.johnabbott.cs406;

import ca.qc.johnabbott.cs406.collections.Either;
import ca.qc.johnabbott.cs406.collections.map.HashMap;
import ca.qc.johnabbott.cs406.serialization.Serializable;
import ca.qc.johnabbott.cs406.serialization.io.BufferedChannel;
import ca.qc.johnabbott.cs406.serialization.Serializer;
import ca.qc.johnabbott.cs406.serialization.util.*;
import ca.qc.johnabbott.cs406.collections.list.LinkedList;
import ca.qc.johnabbott.cs406.collections.set.TreeSet;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Serialization example.
 */
public class MainSerialize {

    public static void main(String arg[]) throws IOException {

        BufferedChannel channel = new BufferedChannel(new RandomAccessFile("foo.bin", "rw").getChannel(), BufferedChannel.Mode.WRITE);

        Serializer serializer = new Serializer(null, channel);

        SInteger i = new SInteger(123);
        SString s = new SString("hello,");
        Box<SString> bs = new Box<>(new SString("world."));
        SDate d = new SDate();
        Grade g = new Grade("Hunter Rogers", 100, new Date());
        Either.LeftEither<SInteger, SString> el = new Either.LeftEither<>(new SInteger(5));
        Either.RightEither<SInteger, SString> er = new Either.RightEither<>(new SString("Five"));
        LinkedList<SInteger> ll = new LinkedList<>();
        for (int x = 0; x < 10; x++){
            ll.add(new SInteger(x));
        }
        HashMap<SInteger, SString> hm = new HashMap<>();
        for(int x = 0; x < 10; x++){
            hm.put(new SInteger(x), new SString("number " + x));
        }
        TreeSet<SInteger> ts = new TreeSet<>();
        for(int x = 0; x < 5; x++){
            ts.add(new SInteger(x));
        }
        for(int x = 9; x > 5; x--){
            ts.add(new SInteger(x));
        }

        serializer.serialize(i);
        serializer.serializeNull();
        serializer.serialize(s);
        serializer.serializeNull();
        serializer.serialize(bs);
        serializer.serializeNull();
        serializer.serialize(d);
        serializer.serializeNull();
        serializer.serialize(g);
        serializer.serializeNull();
        serializer.serialize(el);
        serializer.serializeNull();
        serializer.serialize(er);
        serializer.serializeNull();
        serializer.serialize(ll);
        serializer.serializeNull();
        serializer.serialize(hm);
        serializer.serializeNull();
        serializer.serialize(ts);
        serializer.serializeNull();

        channel.close();

    }
}