package de.slikey.batch.network.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * @author Kevin
 * @since 25.03.2015
 */
public class PacketCodec extends ByteToMessageCodec<Packet> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf buf) throws Exception {
        ByteBuf alloc = buf.alloc().buffer();

        Packet.writeVarInt(alloc, Protocol.getId(packet.getClass()));
        packet.write(alloc);

        int length = alloc.readableBytes();
        Packet.writeVarInt(buf, length);
        buf.writeBytes(alloc);

        alloc.release();
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> list) throws Exception {
        if (!buf.isReadable()) {
            return;
        }

        if (buf.readableBytes() >= 4) {
            // Length is received
            buf.markReaderIndex();

            int length = Packet.readVarInt(buf);
            if (buf.readableBytes() < length) {
                // Not all data is received yet.
                buf.resetReaderIndex();
            } else {
                // All data is received
                int packetId = Packet.readVarInt(buf);
                Packet packet = Protocol.getPacket(packetId);
                packet.read(buf);
                list.add(packet);
            }
        }
    }

}
