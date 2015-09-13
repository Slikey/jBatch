package de.slikey.batch.controller;

import de.slikey.batch.network.protocol.ConnectionHandler;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class ControllerPacketChannelInitializer extends PacketChannelInitializer {

    private final MainController mainController;

    public ControllerPacketChannelInitializer(MainController mainController) {
        this.mainController = mainController;
    }

    public MainController getMainController() {
        return mainController;
    }

    @Override
    protected ConnectionHandler newConnectionHandler(SocketChannel socketChannel) {
        return new ControllerConnectionHandler(this);
    }

}
