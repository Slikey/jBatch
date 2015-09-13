package de.slikey.batch.agent;

import de.slikey.batch.network.client.ClientConnectionHandler;
import de.slikey.batch.network.client.ClientPacketChannelInitializer;
import de.slikey.batch.network.protocol.PacketHandler;
import de.slikey.batch.network.protocol.packet.PacketHandshake;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Kevin Carstens
 * @since 30.04.2015
 */
public class MainClientConnectionHandler extends ClientConnectionHandler<MainClient> {

    private static final Logger logger = LogManager.getLogger(MainClientConnectionHandler.class.getSimpleName());

    public MainClientConnectionHandler(ClientPacketChannelInitializer<MainClient> initializer) {
        super(initializer);
    }

    @Override
    public PacketHandler newPacketHandler(PacketHandshake.ServerType serverType) {
        return new MainClientPacketHandler(this);
    }
    
}
