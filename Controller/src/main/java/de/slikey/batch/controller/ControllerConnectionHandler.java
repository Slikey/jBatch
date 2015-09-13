package de.slikey.batch.controller;

import de.slikey.batch.controller.agent.Client;
import de.slikey.batch.network.protocol.ConnectionHandler;
import de.slikey.batch.network.protocol.PacketHandler;
import de.slikey.batch.network.protocol.packet.PacketHandshake;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class ControllerConnectionHandler extends ConnectionHandler {

    private static final Logger logger = LogManager.getLogger(ControllerConnectionHandler.class.getSimpleName());

    private final ControllerPacketChannelInitializer initializer;
    private Client client;

    public ControllerConnectionHandler(ControllerPacketChannelInitializer initializer) {
        this.initializer = initializer;
        this.client = null;
    }

    public ControllerPacketChannelInitializer getInitializer() {
        return initializer;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Connected new Client! (" + ctx.channel().remoteAddress() + ")");

        super.channelActive(ctx);

        client = new Client(ctx.channel());
        client.connected();
        initializer.getMainController().getClientManager().addClient(client);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Disconnected Client! (" + ctx.channel().remoteAddress() + ")");

        super.channelInactive(ctx);

        initializer.getMainController().getClientManager().removeClient(ctx.channel());
        client.disconnected();
    }

    @Override
    public PacketHandler newPacketHandler(PacketHandshake.ServerType serverType) {
        return new ControllerPacketHandler(this);
    }

}
