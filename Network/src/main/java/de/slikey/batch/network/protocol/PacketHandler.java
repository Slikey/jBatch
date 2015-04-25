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

    public void handle(HandshakePacket packet) {
        error(packet);
    }

    public void handle(HealthStatusPacket packet) {
        error(packet);
    }

    public void handle(PingPacket packet) {
        error(packet);
    }

    public void handle(PongPacket packet) {
        error(packet);
    }

    public void handle(KeepAlivePacket packet) {
        error(packet);
    }

    public void handle(AuthResponsePacket packet) {
        error(packet);
    }

    public void handle(AgentInformationPacket packet) {
        error(packet);
    }

    public void handle(JobExecutePacket packet) { error(packet);}

    public void handle(JobResponsePacket packet) {
        error(packet);
    }

    public void handle(ExceptionPacket packet) { error(packet); }

}
