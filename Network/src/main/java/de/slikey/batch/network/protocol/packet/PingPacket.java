package de.slikey.batch.network.protocol.packet;

import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.PacketHandler;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * @author Kevin Carstens
 * @since 14.04.2015
 */
public class PingPacket extends Packet {

    public static PingPacket create() {
        byte[] bytes = new byte[Short.MAX_VALUE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return new PingPacket(System.nanoTime(), bytes);
    }

    private long sentTime;
    private byte[] bytes;

    public PingPacket() {
    }

    public PingPacket(long sentTime, byte[] bytes) {
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

    @Override
    public void write(ByteBuf buf) throws IOException {
        buf.writeLong(sentTime);
        writeByteArray(buf, bytes);
    }

    @Override
    public Packet read(ByteBuf buf) throws IOException {
        sentTime = buf.readLong();
        bytes = readByteArray(buf);
        return this;
    }

    @Override
    public void handle(PacketHandler packetListener) {
        packetListener.handle(this);
    }

    @Override
    public String toString() {
        return "PingPacket{" +
                "sentTime=" + sentTime +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }

}
