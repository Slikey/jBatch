package de.slikey.batch.controller;

import de.slikey.batch.controller.agent.Agent;
import de.slikey.batch.network.protocol.ConnectionHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class ControllerConnectionHandler extends ConnectionHandler {

    private final ControllerPacketChannelInitializer initializer;

    public ControllerConnectionHandler(ControllerPacketChannelInitializer initializer) {
        this.initializer = initializer;
    }

    public ControllerPacketChannelInitializer getInitializer() {
        return initializer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Agent agent = new Agent(ctx.channel(), ctx.channel().remoteAddress().toString());
        initializer.getBatchController().getAgentManager().addAgent(agent);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        initializer.getBatchController().getAgentManager().removeAgent(ctx.channel());
    }

}
