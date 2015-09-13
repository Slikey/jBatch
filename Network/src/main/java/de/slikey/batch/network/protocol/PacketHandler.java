package de.slikey.batch.network.protocol;


import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
    public final void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
        Packet packet = (Packet) object;
        methodCache.invoke(ctx, packet);
    }

    private class MethodCache {

        private final Method[][] methods;

        private MethodCache(PacketHandler packetHandler) {
            List<Class<? extends Packet>> packets = Protocol.getPackets();
            this.methods = new Method[packets.size()][];
            Class<? extends PacketHandler> packetHandlerClass = packetHandler.getClass();
            for (int i = 0; i < packets.size(); i++) {
                Class<? extends Packet> packet = packets.get(i);
                List<Method> methods = new ArrayList<>();
                for (Method method : packetHandlerClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(HandlePacket.class)
                            && method.getParameterCount() == 1
                            && ArrayUtils.contains(method.getParameterTypes(), packet)) {
                        methods.add(method);
                    }
                }
                this.methods[i] = methods.toArray(new Method[methods.size()]);
            }
        }

        public void invoke(ChannelHandlerContext ctx, Packet packet) throws InvocationTargetException, IllegalAccessException {
            for (Method method : methods[packet.getId()]) {
                try {
                    method.invoke(PacketHandler.this, packet);
                } catch (Throwable cause) {
                    ctx.fireExceptionCaught(cause);
                }
            }
        }

    }

}
