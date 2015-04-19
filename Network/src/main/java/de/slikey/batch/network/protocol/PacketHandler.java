package de.slikey.batch.network.protocol;


import de.slikey.batch.network.protocol.packet.*;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kevin
 * @since 23.03.2015
 */
public class PacketHandler extends ChannelHandlerAdapter {

    @Override
    public final void channelRead(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        ((Packet) object).handle(this);
    }

    public void handle(Packet1Handshake packet) {

    }

    public void handle(Packet2HealthStatus packet) {

    }

    public void handle(Packet4Ping packet) {

    }

    public void handle(Packet5Pong packet) {

    }

    public void handle(Packet6KeepAlive packet) {

    }

    public void handle(Packet7RequestDisconnect packet) {

    }

}
