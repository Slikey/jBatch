package de.slikey.batch.protocol;

import de.slikey.batch.network.protocol.BufferWrapper;
import de.slikey.batch.network.protocol.Packet;

import java.io.IOException;
import java.security.SecureRandom;

/**
 * @author Kevin Carstens
 * @since 14.04.2015
 */
public class PacketPing extends Packet {

    public static PacketPing create() {
        byte[] bytes = new byte[Short.MAX_VALUE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return new PacketPing(System.nanoTime(), bytes);
    }

    private long receivedTime;
    private long sentTime;
    private byte[] bytes;

    public PacketPing() {
        super();
    }

    public PacketPing(long sentTime, byte[] bytes) {
        this.sentTime = sentTime;
        this.bytes = bytes;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public long getReceivedTime() {
        return receivedTime;
    }

    public double getPing() {
        return (receivedTime - sentTime) / 1E9;
    }

    @Override
    public void write(BufferWrapper buf) throws IOException {
        buf.writeLong(sentTime);
        buf.writeByteArray(bytes);
    }

    @Override
    public void read(BufferWrapper buf) throws IOException {
        sentTime = buf.readLong();
        bytes = buf.readByteArray();
        receivedTime = System.nanoTime();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "sentTime=" + sentTime +
                ", receivedTime=" + receivedTime +
                ", bytes=" + bytes.length +
                ", ping=" + getPing() +
                '}';
    }

}
