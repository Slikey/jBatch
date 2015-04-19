package de.slikey.batch.network.protocol.packet;

import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.PacketHandler;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin
 * @since 17.04.2015
 */
public class Packet7RequestDisconnect extends Packet {

    private int delay;

    public Packet7RequestDisconnect() {
    }

    public Packet7RequestDisconnect(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        buf.writeInt(delay);
    }

    @Override
    public Packet read(ByteBuf buf) throws IOException {
        delay = buf.readInt();
        return this;
    }

    @Override
    public void handle(PacketHandler packetListener) {
        packetListener.handle(this);
    }

    @Override
    public String toString() {
        return "Packet7RequestDisconnect{" +
                "delay=" + delay +
                '}';
    }

}
