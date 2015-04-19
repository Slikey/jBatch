package de.slikey.batch.network.protocol.packet;

import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.PacketHandler;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 14.04.2015
 */
public class Packet5Pong extends Packet {

    private long sentTime;
    private long receiveTime;
    private byte[] bytes;

    public Packet5Pong() {
    }

    public Packet5Pong(Packet4Ping ping, long receiveTime) {
        this.sentTime = ping.getSentTime();
        this.receiveTime = receiveTime;
        this.bytes = ping.getBytes();
    }

    public Packet5Pong(long sentTime, long receiveTime, byte[] bytes) {
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
    public int getId() {
        return 5;
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
        return "Packet5Pong{" +
                "sentTime=" + sentTime +
                ", receiveTime=" + receiveTime +
                ", bytes=" + bytes.length +
                ", ping=" + getPing() +
                '}';
    }
}
