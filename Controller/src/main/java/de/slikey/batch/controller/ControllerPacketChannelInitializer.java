package de.slikey.batch.controller;

import de.slikey.batch.network.protocol.ConnectionHandler;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class ControllerPacketChannelInitializer extends PacketChannelInitializer {

    private final BatchController batchController;

    public ControllerPacketChannelInitializer(BatchController batchController) {
        this.batchController = batchController;
    }

    public BatchController getBatchController() {
        return batchController;
    }

    @Override
    protected ConnectionHandler newConnectionHandler(SocketChannel socketChannel) {
        return new ControllerConnectionHandler(this);
    }

}
