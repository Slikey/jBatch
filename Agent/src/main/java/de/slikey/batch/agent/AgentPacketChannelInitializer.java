package de.slikey.batch.agent;

import de.slikey.batch.network.protocol.ConnectionHandler;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @author Kevin Carstens
 * @since 30.04.2015
 */
public class AgentPacketChannelInitializer extends PacketChannelInitializer {

    private final BatchAgent batchAgent;

    public AgentPacketChannelInitializer(BatchAgent batchAgent) {
        this.batchAgent = batchAgent;
    }

    public BatchAgent getBatchAgent() {
        return batchAgent;
    }

    @Override
    protected ConnectionHandler newConnectionHandler(final SocketChannel socketChannel) {
        return new AgentConnectionHandler(this);
    }

    @Override
    protected ReadTimeoutHandler newReadTimeoutHandler(SocketChannel socketChannel) {
        return null;
    }

}
