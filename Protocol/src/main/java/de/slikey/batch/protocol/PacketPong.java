package de.slikey.batch.protocol;

import de.slikey.batch.network.protocol.BufferWrapper;
import de.slikey.batch.network.protocol.Packet;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 14.04.2015
 */
public class PacketPong extends Packet {

    public static PacketPong create(PacketPing packetPing) {
        return new PacketPong(packetPing.getSentTime(), packetPing.getReceivedTime(), packetPing.getBytes());
    }

    private long sentTime;
    private long receivedTime;
    private byte[] bytes;

    public PacketPong() {
        super();
    }

    public PacketPong(long sentTime, long receivedTime, byte[] bytes) {
        this.sentTime = sentTime;
        this.receivedTime = receivedTime;
        this.bytes = bytes;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public long getRecievedTime() {
        return receivedTime;
    }

    public void setRecievedTime(long receivedTime) {
        this.receivedTime = receivedTime;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public double getPing() {
        return (receivedTime - sentTime) / 1E9;
    }

    @Override
    public void write(BufferWrapper buf) throws IOException {
        buf.writeLong(sentTime);
        buf.writeLong(receivedTime);
        buf.writeByteArray(bytes);
    }

    @Override
    public void read(BufferWrapper buf) throws IOException {
        sentTime = buf.readLong();
        receivedTime = buf.readLong();
        bytes = buf.readByteArray();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "sentTime=" + sentTime +
                ", receiveTime=" + receivedTime +
                ", bytes=" + bytes.length +
                ", ping=" + getPing() +
                '}';
    }
}
