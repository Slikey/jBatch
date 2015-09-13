package de.slikey.batch.network.client;

import de.slikey.batch.network.NIOComponent;
import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import de.slikey.batch.network.protocol.Protocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ResourceLeakDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Kevin
 * @since 23.03.2015
 */
public abstract class NIOClient extends NIOComponent {

    private static final Logger logger = LogManager.getLogger(NIOClient.class.getSimpleName());

    private final String host;
    private final int port;
    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;
    private Channel channel;
    private boolean reconnect;

    public NIOClient(String host, int port, int threadCount) {
        super(threadCount);
        this.host = host;
        this.port = port;
        this.reconnect = false;
    }

    public void run() {
        eventLoopGroup = new NioEventLoopGroup();

        try {
            Protocol.initialize();
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

            logger.info("Attempting to connect to " + host + ":" + port + "...");

            connect();
            started();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void connect() {
        channel = null;
        if (bootstrap == null) {
            bootstrap = new Bootstrap()
                    .group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
                    .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(buildPacketChannelInitializer());
        }

        bootstrap.connect(host, port)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        try {
                            Throwable cause = future.cause();
                            if (cause != null) {
                                throw cause;
                            }
                            channel = future.channel();
                            logger.info("Connected to " + host + ":" + port);
                        } catch (Throwable throwable) {
                            logger.error("Failed to connect: " + throwable);
                            connect();
                        }
                    }
                });
    }

    protected void close() throws InterruptedException {
        logger.info("Shutdown requested...");
        if (channel != null) {
            channel.close().awaitUninterruptibly();
        }
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully().awaitUninterruptibly();
        }

        getExecutorService().shutdown();
        getExecutorService().awaitTermination(5, TimeUnit.SECONDS);

        logger.info("Successfully shutdown.");
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isReconnect() {
        return reconnect;
    }

    public void setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
    }

    public void sendPacket(Packet packet) {
        channel.writeAndFlush(packet);
    }

    public boolean isRunning() {
        return channel != null && channel.isActive();
    }

    protected Channel getChannel() {
        return channel;
    }

    protected abstract PacketChannelInitializer buildPacketChannelInitializer();

    protected abstract void started();

    protected abstract void connected();

    protected abstract void disconnected();

}
