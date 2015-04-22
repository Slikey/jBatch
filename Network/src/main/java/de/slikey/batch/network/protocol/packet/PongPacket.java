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
        return new PongPacket(pingPacket.getSentTime(), System.nanoTime(), pingPacket.getBytes());
    }

    private long sentTime;
    private long receiveTime;
    private byte[] bytes;

    public PongPacket() {
    }

    public PongPacket(long sentTime, long receiveTime, byte[] bytes) {
        this.sentTime = sentTime;
        this.receiveTime = receiveTime;
        this.bytes = bytes;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public long getRecieveTime() {
        return receiveTime;
    }

    public void setRecieveTime(long recieveTime) {
        this.receiveTime = recieveTime;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public double getPing() {
        return (receiveTime - sentTime) / 1E9;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        buf.writeLong(sentTime);
        buf.writeLong(receiveTime);
        writeByteArray(buf, bytes);
    }

    @Override
    public Packet read(ByteBuf buf) throws IOException {
        sentTime = buf.readLong();
        receiveTime = buf.readLong();
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
                ", receiveTime=" + receiveTime +
                ", bytes=" + bytes.length +
                ", ping=" + getPing() +
                '}';
    }
}
