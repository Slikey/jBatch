package de.slikey.batch.controller;

import de.slikey.batch.network.protocol.PacketHandler;
import de.slikey.batch.network.protocol.packet.Packet2HealthStatus;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class ControllerPacketHandler extends PacketHandler {

    private final ControllerPacketChannelInitializer initializer;

    public ControllerPacketHandler(ControllerPacketChannelInitializer initializer) {
        this.initializer = initializer;
    }

    public ControllerPacketChannelInitializer getInitializer() {
        return initializer;
    }

    @Override
    public void handle(Packet2HealthStatus packet) {
        System.out.println(packet);
    }

}
