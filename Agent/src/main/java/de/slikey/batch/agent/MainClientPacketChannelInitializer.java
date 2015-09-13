package de.slikey.batch.agent;

import de.slikey.batch.network.protocol.ConnectionHandler;
import io.netty.channel.socket.SocketChannel;

/**
 * @author Kevin Carstens
 * @since 30.04.2015
 */
public class MainClientPacketChannelInitializer extends de.slikey.batch.network.client.ClientPacketChannelInitializer<MainClient> {

    public MainClientPacketChannelInitializer(MainClient client) {
        super(client);
    }

    @Override
    protected ConnectionHandler newConnectionHandler(final SocketChannel socketChannel) {
        return new MainClientConnectionHandler(this);
    }

}
