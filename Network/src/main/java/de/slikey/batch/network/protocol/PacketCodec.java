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
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
        ByteBuf alloc = channelHandlerContext.alloc().buffer();
        Packet.writeVarInt(alloc, Protocol.getId(packet.getClass()));
        packet.write(alloc);

        int length = alloc.readableBytes();
        Packet.writeVarInt(byteBuf, length);
        byteBuf.writeBytes(alloc);
        alloc.release();
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (!byteBuf.isReadable())
            return;

        if (byteBuf.readableBytes() >= 4) {
            // Length is received
            byteBuf.markReaderIndex();

            int length = Packet.readVarInt(byteBuf);
            if (byteBuf.readableBytes() < length) {
                // Not all data is received yet.
                byteBuf.resetReaderIndex();
            } else {
                // All data is received
                int packetId = Packet.readVarInt(byteBuf);
                Packet packet = Protocol.getPacket(packetId);
                packet.read(byteBuf);
                list.add(packet);
            }
        }
    }

}
