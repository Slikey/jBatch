package de.slikey.batch.network.protocol;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.timeout.ReadTimeoutException;

import java.io.IOException;

/**
 * @author Kevin
 * @since 17.04.2015
 */
public class ConnectionHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(">> " + ctx.channel().remoteAddress().toString() + " connected.");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("<< " + ctx.channel().remoteAddress().toString() + " disconnected.");

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
            String address = ctx.channel().remoteAddress().toString();
            if (cause instanceof ReadTimeoutException) {
                System.out.println(";; " + address + " timed out.");
            } else if (cause instanceof IOException) {
                System.out.println(";; " + address + " IOException: " + cause.getMessage());
            } else if (cause instanceof DecoderException) {
                System.out.println(";; " + address + " sent a bad packet: " + cause.getMessage());
                if (cause.getCause() instanceof IndexOutOfBoundsException)
                    cause.printStackTrace();
            } else {
                System.out.println(";; " + address + " encountered exception: ");
                cause.printStackTrace();
            }

            ctx.close();
        }
    }

}
