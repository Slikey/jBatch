package de.slikey.batch.protocol;

import de.slikey.batch.network.protocol.Packet;
import io.netty.buffer.ByteBuf;

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
    public void write(ByteBuf buf) throws IOException {
        buf.writeLong(sentTime);
        writeByteArray(buf, bytes);
    }

    @Override
    public void read(ByteBuf buf) throws IOException {
        sentTime = buf.readLong();
        bytes = readByteArray(buf);
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
