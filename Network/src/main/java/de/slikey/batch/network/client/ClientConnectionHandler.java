package de.slikey.batch.network.client;

import de.slikey.batch.network.protocol.ConnectionHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Kevin
 * @since 08.09.2015
 */
public abstract class ClientConnectionHandler<Client extends NIOClient> extends ConnectionHandler {

    private static final Logger logger = LogManager.getLogger(ClientConnectionHandler.class.getSimpleName());

    private final ClientPacketChannelInitializer<Client> initializer;

    public ClientConnectionHandler(ClientPacketChannelInitializer<Client> initializer) {
        this.initializer = initializer;
    }

    public ClientPacketChannelInitializer<Client> getInitializer() {
        return initializer;
    }

    public Client getClient() {
        return initializer.getClient();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Connected to Server! (" + ctx.channel().remoteAddress() + ")");
        initializer.getClient().connected();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Disconnected from Server! (" + ctx.channel().remoteAddress() + ")");
        initializer.getClient().disconnected();
        super.channelInactive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        final NIOClient client = initializer.getClient();
        final EventLoop loop = ctx.channel().eventLoop();
        if (client.isReconnect()) {
            loop.submit(() -> {
                logger.info("Reconnecting to Server! (" + client.getHost() + ':' + client.getPort() + ")");
                client.connect();
            });
        }
    }

}
