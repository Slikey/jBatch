package de.slikey.batch.controller;

import de.slikey.batch.network.protocol.HandlePacket;
import de.slikey.batch.network.protocol.PacketHandler;
import de.slikey.batch.protocol.PacketException;
import de.slikey.batch.protocol.PacketHealthStatus;
import de.slikey.batch.protocol.PacketPing;
import de.slikey.batch.protocol.PacketPong;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class ControllerPacketHandler extends PacketHandler {

    private static final Logger logger = LogManager.getLogger(ControllerPacketHandler.class.getSimpleName());
    private final ControllerConnectionHandler connectionHandler;

    public ControllerPacketHandler(ControllerConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public ControllerConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    @HandlePacket
    public void handle(PacketHealthStatus packet) {
        connectionHandler.getAgent().handleHealthStatus(packet);
    }

    @HandlePacket
    public void handle(PacketPing packet) {
        logger.debug(packet);
        connectionHandler.getAgent().sendPacket(PacketPong.create(packet));
    }

    @HandlePacket
    public void handle(PacketPong packet) {
        logger.debug(packet);
    }

    @HandlePacket
    public void handle(PacketException packet) {
        logger.error("Exception thrown in connected Agent. (" + connectionHandler.getAgent().getChannel().remoteAddress().toString() + ")", packet.getException());
    }

}
