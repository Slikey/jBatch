package de.slikey.batch.network.protocol;

import de.slikey.batch.network.protocol.packet.PacketKeepAlive;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author Kevin
 * @since 23.03.2015
 */
public abstract class PacketChannelInitializer extends ChannelInitializer<SocketChannel> {

    public static final String TIMEOUT_HANDLER = "TimeoutHandler";
    public static final String CONNECTION_HANDLER = "ConnectionHandler";
    public static final String PACKET_CODEC = "PacketCodec";
    public static final String PACKET_HANDLER = "PacketHandler";

    @Override
    public void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        ReadTimeoutHandler readTimeoutHandler = newReadTimeoutHandler();
        if (readTimeoutHandler != null) {
            pipeline.addLast(TIMEOUT_HANDLER, readTimeoutHandler);
        }

        pipeline.addLast(PACKET_CODEC, newPacketCodec());

        ConnectionHandler connectionHandler = newConnectionHandler(socketChannel);
        pipeline.addLast(CONNECTION_HANDLER, connectionHandler);
    }

    protected PacketCodec newPacketCodec() {
        return new PacketCodec();
    }

    protected abstract ConnectionHandler newConnectionHandler(SocketChannel socketChannel);

    protected ReadTimeoutHandler newReadTimeoutHandler() {
        return new ReadTimeoutHandler(PacketKeepAlive.TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

}
