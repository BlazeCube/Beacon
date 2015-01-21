/**	
 * Beacon - Open Source Minecraft Server
 * Copyright (C) 2014  Jan Delius
 * Copyright (C) 2014  Blazecube
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author Jan Delius
 */
 package org.beaconmc.network.socket.pipeline;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.beaconmc.BeaconServer;
import org.beaconmc.chat.ChatColor;
import org.beaconmc.events.EventFactory;
import org.beaconmc.events.server.AsyncServerPingEvent;

public class PacketLegacy extends SimpleChannelInboundHandler<ByteBuf> {

    private BeaconServer server;

    public PacketLegacy(BeaconServer server){
        this.server = server;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {

        byteBuf.markReaderIndex();
        boolean repliedPing = true;

        try{
            short packetId = byteBuf.readUnsignedByte();
            if(packetId == 254){
                int length = byteBuf.readableBytes();
                AsyncServerPingEvent asyncServerPingEvent = EventFactory.callAsyncServerPingEvent(this.server.createServerPing());
                if(asyncServerPingEvent.getPing() != null){
                    switch(length) {
                        case 0:
                            this.sendPingAndClose(channelHandlerContext, this.toArray(String.format("%s§%d§%d", asyncServerPingEvent.getPing().getDescription().getText(), asyncServerPingEvent.getPing().getPlayers().getOnline(), asyncServerPingEvent.getPing().getPlayers().getMax())));
                            break;
                        case 1:
                            if (byteBuf.readUnsignedByte() != 1) {
                                return;
                            }
                            this.sendPingAndClose(channelHandlerContext, this.toArray(String.format("§1\0%d\0%s\0%s\0%d\0%d", 127, asyncServerPingEvent.getPing().getVersion().getName(), asyncServerPingEvent.getPing().getDescription().getText(), asyncServerPingEvent.getPing().getPlayers().getOnline(), asyncServerPingEvent.getPing().getPlayers().getMax())));
                            break;
                        default:
                            boolean checkFlag = byteBuf.readUnsignedByte() == 1;
                            checkFlag &= byteBuf.readUnsignedByte() == 250;
                            checkFlag &= "MC|PingHost".equals(new String(byteBuf.readBytes(byteBuf.readShort()*2).array(), Charsets.UTF_16));

                            int checkShort = byteBuf.readShort();

                            checkFlag &= byteBuf.readUnsignedByte() >= 73;
                            checkFlag &= 3 + byteBuf.readBytes(byteBuf.readShort()*2).array().length + 4 == checkShort;
                            checkFlag &= byteBuf.readInt() <= '\uffff';
                            checkFlag &= byteBuf.readableBytes() == 0;

                            if(!checkFlag){
                                return;
                            }

                            this.sendPingAndClose(channelHandlerContext, this.toArray(String.format("§1\0%d\0%s\0%s\0%d\0%d", 127, asyncServerPingEvent.getPing().getVersion().getName(), asyncServerPingEvent.getPing().getDescription().getText(), asyncServerPingEvent.getPing().getPlayers().getOnline(), asyncServerPingEvent.getPing().getPlayers().getMax())));
                            break;
                    }
                }else {
                    this.close(channelHandlerContext);
                }
                repliedPing = false;
            }else if(packetId == 0x02 && byteBuf.isReadable()){
                this.sendPingAndClose(channelHandlerContext, this.toArray(ChatColor.RED + "Outdated Client"));
                repliedPing = false;
            }
        }catch(Exception e){
            return;
        }finally {
            if(repliedPing){
                byteBuf.resetReaderIndex();
                channelHandlerContext.channel().pipeline().remove("legacy_ping");
                channelHandlerContext.fireChannelRead(byteBuf.retain());
            }
        }
    }

    private void close(ChannelHandlerContext channelHandlerContext){
        channelHandlerContext.pipeline().close();
    }

    private void sendPingAndClose(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf){
        channelHandlerContext.pipeline().firstContext().writeAndFlush(byteBuf).addListeners(ChannelFutureListener.CLOSE);
    }

    private ByteBuf toArray(String pingstring){
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeByte(255);
        char[] chars = pingstring.toCharArray();
        byteBuf.writeShort(chars.length);
        for(char charpart: chars){
            byteBuf.writeChar(charpart);
        }
        return byteBuf;
    }
}