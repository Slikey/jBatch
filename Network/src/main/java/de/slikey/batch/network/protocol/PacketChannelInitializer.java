package de.slikey.batch.network.protocol;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

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

        ReadTimeoutHandler readTimeoutHandler = newReadTimeoutHandler(socketChannel);
        if (readTimeoutHandler != null)
            pipeline.addLast(TIMEOUT_HANDLER, readTimeoutHandler);

        pipeline.addLast(PACKET_CODEC, newPacketCodec(socketChannel));

        ConnectionHandler connectionHandler =  newConnectionHandler(socketChannel);
        pipeline.addLast(CONNECTION_HANDLER,connectionHandler);

        pipeline.addLast(PACKET_HANDLER, connectionHandler.newPacketHandler());
    }

    protected PacketCodec newPacketCodec(SocketChannel socketChannel) {
        return new PacketCodec();
    }

    protected abstract ConnectionHandler newConnectionHandler(SocketChannel socketChannel);

    protected ReadTimeoutHandler newReadTimeoutHandler(SocketChannel socketChannel) {
        return new ReadTimeoutHandler(5);
    }

}
