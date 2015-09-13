package de.slikey.batch.network.protocol;

import de.slikey.batch.network.protocol.packet.PacketHandshake;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.timeout.ReadTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * @author Kevin
 * @since 17.04.2015
 */
public abstract class ConnectionHandler extends ChannelHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(ConnectionHandler.class.getSimpleName());

    private boolean initialized;

    public ConnectionHandler() {
        initialized = false;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
            String address = ctx.channel().remoteAddress().toString();
            if (cause instanceof ReadTimeoutException) {
                logger.info("xx " + address + " timed out.");
            } else if (cause instanceof IOException) {
                logger.info("xx " + address + " IOException: " + cause.getMessage());
            } else if (cause instanceof DecoderException) {
                logger.info("xx " + address + " sent a bad packet: " + cause.getMessage());
                if (cause.getCause() instanceof IndexOutOfBoundsException)
                    cause.printStackTrace();
            } else if (cause instanceof Error) {
                logger.info("xx " + address + " Error: " + cause);
            } else {
                logger.info("xx " + address + " encountered exception: ");
                cause.printStackTrace();
            }

            ctx.close();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().write(new PacketHandshake(Protocol.getProtocolHash(), PacketHandshake.ServerType.DEFAULT));
        logger.info("Sent handshake to " + ctx.channel().remoteAddress() + "!");
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!initialized) {
            if (msg instanceof PacketHandshake) {
                final PacketHandshake packetHandshake = (PacketHandshake) msg;
                if (packetHandshake.getVersion() == Protocol.getProtocolHash()) {
                    final ChannelPipeline pipeline = ctx.channel().pipeline();
                    pipeline.addLast(PacketChannelInitializer.PACKET_HANDLER, newPacketHandler(packetHandshake.getServerType()));
                    initialized = true;
                } else {
                    throw new Error("Protocol-Version mismatch! " + Protocol.getProtocolHash() + " (We) != " + packetHandshake.getVersion() + " (Other)");
                }
            } else {
                throw new Error("Server doesn't accept Packets yet. Received " + msg.getClass().getName() + "!");
            }
        }
        super.channelRead(ctx, msg);
    }

    public abstract PacketHandler newPacketHandler(PacketHandshake.ServerType serverType);
}
