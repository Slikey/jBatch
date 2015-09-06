package de.slikey.batch.network.protocol;

import de.slikey.batch.network.client.NIOClient;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.timeout.ReadTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Kevin
 * @since 17.04.2015
 */
public abstract class ConnectionHandler extends ChannelHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(ConnectionHandler.class.getSimpleName());

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
            } else {
                logger.info("xx " + address + " encountered exception: ");
                cause.printStackTrace();
            }

            ctx.close();
        }
    }

    protected void tryReconnect(final NIOClient nioClient, final ChannelHandlerContext ctx) {
        if (nioClient.isReconnect()) {
            final EventLoop loop = ctx.channel().eventLoop();
            loop.schedule(() -> {
                logger.info("Reconnecting to: " + nioClient.getHost() + ':' + nioClient.getPort());
                try {
                    nioClient.connect();
                } catch (InterruptedException e) {
                    logger.error(e);
                }
            }, 10, TimeUnit.SECONDS);
        } else {
            synchronized (nioClient) {
                nioClient.notify();
            }
        }
    }

    public abstract PacketHandler newPacketHandler();
}
