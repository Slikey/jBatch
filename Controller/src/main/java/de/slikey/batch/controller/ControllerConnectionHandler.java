package de.slikey.batch.controller;

import de.slikey.batch.controller.agent.Agent;
import de.slikey.batch.network.protocol.ConnectionHandler;
import de.slikey.batch.network.protocol.PacketHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class ControllerConnectionHandler extends ConnectionHandler {

    private static final Logger logger = LogManager.getLogger(ControllerConnectionHandler.class);

    private final ControllerPacketChannelInitializer initializer;
    private Agent agent;

    public ControllerConnectionHandler(ControllerPacketChannelInitializer initializer) {
        this.initializer = initializer;
        this.agent = null;
    }

    public ControllerPacketChannelInitializer getInitializer() {
        return initializer;
    }

    public Agent getAgent() {
        return agent;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info(">> Connected new Agent! (" + ctx.channel().remoteAddress() + ")");
        agent = new Agent(ctx.channel());
        agent.connected();
        initializer.getBatchController().getAgentManager().addAgent(agent);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        initializer.getBatchController().getAgentManager().removeAgent(ctx.channel());
        agent.disconnected();
        logger.info("<< Disconnected Agent! (" + ctx.channel().remoteAddress() + ")");
    }

    @Override
    public PacketHandler newPacketHandler() {
        return new ControllerPacketHandler(this);
    }

}
