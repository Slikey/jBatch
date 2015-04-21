package de.slikey.batch.network.protocol;


import de.slikey.batch.network.protocol.packet.*;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Kevin
 * @since 23.03.2015
 */
public class PacketHandler extends ChannelHandlerAdapter {

    @Override
    public final void channelRead(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        ((Packet) object).handle(this);
    }

    private void error(Packet packet) {
        throw new IllegalStateException("PacketHandler does not implement handler for " + packet.getClass().getSimpleName() + "!");
    }

    public void handle(Packet1Handshake packet) {
        error(packet);
    }

    public void handle(Packet2HealthStatus packet) {
        error(packet);
    }

    public void handle(Packet4Ping packet) {
        error(packet);
    }

    public void handle(Packet5Pong packet) {
        error(packet);
    }

    public void handle(Packet6KeepAlive packet) {
        error(packet);
    }

    public void handle(Packet8AuthResponse packet) {
        error(packet);
    }

    public void handle(Packet40AgentInformation packet) {
        error(packet);
    }

}
