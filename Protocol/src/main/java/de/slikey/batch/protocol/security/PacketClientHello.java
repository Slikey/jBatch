package de.slikey.batch.protocol.security;

import de.slikey.batch.network.protocol.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Kevin Carstens
 * @since 27.04.2015
 */
public class PacketClientHello extends Packet {

    private static final Random RANDOM = new SecureRandom();

    public static PacketClientHello create() {
        return new PacketClientHello(System.nanoTime(), RANDOM.nextLong());
    }

    private long timestamp;
    private long random;

    public PacketClientHello() {

    }

    public PacketClientHello(long timestamp, long random) {
        this.timestamp = timestamp;
        this.random = random;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getRandom() {
        return random;
    }

    public void setRandom(long random) {
        this.random = random;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        buf.writeLong(timestamp);
        buf.writeLong(random);
    }

    @Override
    public void read(ByteBuf buf) throws IOException {
    }

    @Override
    public String toString() {
        return null;
    }

}
