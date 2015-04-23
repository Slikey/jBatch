package de.slikey.batch.network.protocol.packet;

import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.PacketHandler;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 14.04.2015
 */
public class PongPacket extends Packet {

    public static PongPacket create(PingPacket pingPacket) {
        return new PongPacket(pingPacket.getSentTime(), pingPacket.getReceivedTime(), pingPacket.getBytes());
    }

    private long sentTime;
    private long receivedTime;
    private byte[] bytes;

    public PongPacket() {
    }

    public PongPacket(long sentTime, long receivedTime, byte[] bytes) {
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

    public void setRecievedTime(long recievedTime) {
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
    public void write(ByteBuf buf) throws IOException {
        buf.writeLong(sentTime);
        buf.writeLong(receivedTime);
        writeByteArray(buf, bytes);
    }

    @Override
    public Packet read(ByteBuf buf) throws IOException {
        sentTime = buf.readLong();
        receivedTime = buf.readLong();
        bytes = readByteArray(buf);
        return this;
    }

    @Override
    public void handle(PacketHandler packetListener) {
        packetListener.handle(this);
    }

    @Override
    public String toString() {
        return "PongPacket{" +
                "sentTime=" + sentTime +
                ", receiveTime=" + receivedTime +
                ", bytes=" + bytes.length +
                ", ping=" + getPing() +
                '}';
    }
}
