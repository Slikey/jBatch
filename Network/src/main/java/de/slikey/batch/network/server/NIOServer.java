package de.slikey.batch.network.server;

import de.slikey.batch.network.NIOComponent;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import de.slikey.batch.network.protocol.Protocol;
import de.slikey.batch.network.server.listener.StartServerListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * @author Kevin
 * @since 23.03.2015
 */
public abstract class NIOServer extends NIOComponent {

    private static final Logger logger = LogManager.getLogger(NIOServer.class.getSimpleName());

    private final int port;
    private EventLoopGroup bossLoop, workerLoop;

    public NIOServer(int port, int threadCount) {
        super(threadCount);
        this.port = port;
    }

    public void run() {
        bossLoop = new NioEventLoopGroup();
        workerLoop = new NioEventLoopGroup();

        try {
            Protocol.initialize();
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossLoop, workerLoop)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(buildPacketChannelInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
                    .childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            logger.info("Attempting to bind on port " + port + "...");
            bootstrap.bind(port)
                    .sync()
                    .addListener(new StartServerListener(this))
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            bossLoop.shutdownGracefully();
            workerLoop.shutdownGracefully();
        }
    }

    public int getPort() {
        return port;
    }

    protected void close() throws InterruptedException {
        System.out.println("Shutdown requested...");
        if (bossLoop != null) {
            bossLoop.shutdownGracefully().awaitUninterruptibly();
            System.out.println("Successfully shut bossLoop down!");
        } else {
            System.out.println("There is no bossLoop!");
        }

        if (workerLoop != null) {
            workerLoop.shutdownGracefully().awaitUninterruptibly();
            System.out.println("Successfully shut workerLoop down!");
        } else {
            System.out.println("There is no workerLoop!");
        }
    }

    public abstract void startApplication() throws InterruptedException;

    protected abstract PacketChannelInitializer buildPacketChannelInitializer();

}
