package de.slikey.batch.network.client;

import de.slikey.batch.network.protocol.PacketChannelInitializer;

/**
 * @author Kevin
 * @since 08.09.2015
 */
public abstract class ClientPacketChannelInitializer<Client extends NIOClient> extends PacketChannelInitializer {

    private final Client client;

    public ClientPacketChannelInitializer(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

}
