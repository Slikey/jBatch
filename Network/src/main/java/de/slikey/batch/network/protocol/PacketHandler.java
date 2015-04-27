package de.slikey.batch.network.protocol;


import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Kevin
 * @since 23.03.2015
 */
public class PacketHandler extends ChannelHandlerAdapter {

    private final MethodCache methodCache;

    public PacketHandler() {
        this.methodCache = new MethodCache(this);
    }

    @Override
    public final void channelRead(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        Packet packet = (Packet) object;
        methodCache.invoke(packet);
    }

    private class MethodCache {

        private final Method[] methods;

        private MethodCache(PacketHandler packetHandler) {
            List<Class<? extends Packet>> packets = Protocol.getPackets();
            this.methods = new Method[packets.size()];
            Class<? extends PacketHandler> packetHandlerClass = packetHandler.getClass();
            int index = 0;
            for (Class<? extends Packet> packet : packets) {
                try {
                    Method method = packetHandlerClass.getDeclaredMethod("handle", packet);
                    method.setAccessible(true);
                    this.methods[index] = method;
                } catch (NoSuchMethodException e) {
                    // No Handler for Packet is defined.
                    // Ignoring Exception because this is a common way to have specific handlers
                }

                index++;
            }
        }

        public void invoke(Packet packet) throws InvocationTargetException, IllegalAccessException {
            Method method = methods[packet.getId()];
            if (method == null)
                return;

            method.invoke(PacketHandler.this, packet);
        }

    }

}
