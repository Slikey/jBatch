package de.slikey.batch.controller;

import de.slikey.batch.network.protocol.HandlePacket;
import de.slikey.batch.network.protocol.PacketHandler;
import de.slikey.batch.protocol.*;
import de.slikey.batch.protocol.job.PacketJobConsoleOutput;
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
        logger.error("Exception thrown in connected Agent. (" + connectionHandler.getAgent().getInformation().getName() + ")", packet.getException());
    }

    @HandlePacket
    public void handle(PacketAgentInformation packet) {
        connectionHandler.getAgent().handleAgentInformation(packet);
    }

    @HandlePacket
    public void handle(PacketJobResponse packet) {
        connectionHandler.getInitializer().getBatchController().getJobManager().handleJobResponse(packet);
    }

    @HandlePacket
    public void handle(PacketJobConsoleOutput packet) {
        System.out.println("[" + packet.getLevel() + "]: " + packet.getLine());
    }

}
