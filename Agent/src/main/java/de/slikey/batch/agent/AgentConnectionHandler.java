package de.slikey.batch.agent;

import de.slikey.batch.network.client.NIOClient;
import de.slikey.batch.network.protocol.ConnectionHandler;
import de.slikey.batch.network.protocol.PacketHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Kevin Carstens
 * @since 30.04.2015
 */
public class AgentConnectionHandler extends ConnectionHandler {

    private static final Logger logger = LogManager.getLogger(AgentConnectionHandler.class.getSimpleName());

    private final AgentPacketChannelInitializer initializer;

    public AgentConnectionHandler(AgentPacketChannelInitializer initializer) {
        this.initializer = initializer;
    }

    public AgentPacketChannelInitializer getInitializer() {
        return initializer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Connected to Controller! (" + ctx.channel().remoteAddress() + ")");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Disconnected from Controller! (" + ctx.channel().remoteAddress() + ")");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        final NIOClient nioClient = initializer.getBatchAgent();
        tryReconnect(nioClient, ctx);
    }

    @Override
    public PacketHandler newPacketHandler() {
        return new AgentPacketHandler(this);
    }
    
}
