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
 package org.beaconmc.network.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayDeque;
import java.util.Queue;

import javax.crypto.SecretKey;

import lombok.Getter;
import lombok.Setter;

import org.beaconmc.BeaconServer;
import org.beaconmc.chat.ChatElement;
import org.beaconmc.entity.living.Player;
import org.beaconmc.events.EventFactory;
import org.beaconmc.events.protocol.AsyncPacketInEvent;
import org.beaconmc.events.protocol.AsyncPacketOutEvent;
import org.beaconmc.logging.ErrorStream;
import org.beaconmc.logging.InfoStream;
import org.beaconmc.network.socket.handler.PacketHandler;
import org.beaconmc.network.socket.handler.play.PlayHandler;
import org.beaconmc.network.socket.pipeline.PacketCompression;
import org.beaconmc.network.socket.pipeline.PacketDecoder;
import org.beaconmc.network.socket.pipeline.PacketEncoder;
import org.beaconmc.network.socket.pipeline.PacketEncryption;
import org.beaconmc.network.socket.protocol.PlayerProfile;
import org.beaconmc.network.socket.protocol.ProtocolState;
import org.beaconmc.network.socket.protocol.packets.PacketOut;
import org.beaconmc.network.socket.protocol.packets.handshake.in.PacketHandshakeInHandshake;
import org.beaconmc.network.socket.protocol.packets.login.out.PacketLoginOutSetCompression;
import org.beaconmc.network.socket.protocol.packets.login.out.PacketLoginOutSuccess;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInClientSettings;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutEntity;

public class ClientConnection {

    private Channel channel;
    @Getter
    private PacketHandler packetHandler;
    @Getter
    private BeaconServer server;
    private ProtocolState protocolState;
    @Getter
    private NetworkHandler networkHandler;
    private Queue<AsyncPacketInEvent> packetInQueue = new ArrayDeque<>();

    @Getter@Setter
    private String authenticationName;
    @Getter
    private PlayerProfile playerProfile;
    @Getter
    private Player player;

    @Getter
    private PacketHandshakeInHandshake handshake;
    @Getter@Setter
    private PacketPlayInClientSettings clientSettings;
    @Getter@Setter
    private int ping;

    private int tickCount;

    public ClientConnection(ChannelHandlerContext channelHandlerContext, NetworkHandler networkHandler){
        this.channel = channelHandlerContext.channel();
        this.networkHandler = networkHandler;
        this.server = this.networkHandler.getServer();
        
    }

    public int getProtocolVersion(){
        return this.handshake.getProtocolVersion();
    }

    public void setup(){
        this.setProtocolState(ProtocolState.HANDSHAKE);
    }

    public void setHandshake(PacketHandshakeInHandshake handshake){
        this.handshake = handshake;
    }

    public void setProtocolState(ProtocolState protocolState){
        AsyncPacketInEvent asyncPacketInEvent;
        while ((asyncPacketInEvent = this.packetInQueue.poll()) != null) {
            try {
                this.handle(asyncPacketInEvent);
            } catch (Exception e) {
                ErrorStream.handle(e);
            }
        }

        this.channel.flush();
        this.channel.pipeline().replace("encoder", "encoder", new PacketEncoder(protocolState.getProtocol(), protocolState == ProtocolState.HANDSHAKE ? 0 :  this.getProtocolVersion()));
        this.channel.pipeline().replace("decoder", "decoder", new PacketDecoder(protocolState.getProtocol(), protocolState == ProtocolState.HANDSHAKE ? 0 :  this.getProtocolVersion()));
        try {
            this.packetHandler = protocolState.getPacketHandlerClass().getConstructor(ClientConnection.class).newInstance(this);
        } catch (Exception e) {
            ErrorStream.handle(e);
        }
        this.protocolState = protocolState;
    }

    public void setEncryption(SecretKey secretKey){
        this.channel.flush();
        this.channel.pipeline().replace("encryption", "encryption", new PacketEncryption(secretKey));
    }

    public void setCompression(int threshold){
        this.channel.flush();
        if(this.protocolState == ProtocolState.LOGIN){
            this.sendPacket(new PacketLoginOutSetCompression(threshold));
        }
        this.channel.flush();
        this.channel.pipeline().replace("compression", "compression", new PacketCompression(threshold));
    }

    public void handle(AsyncPacketInEvent asyncPacketInEvent){
        asyncPacketInEvent.getPacket().handle(asyncPacketInEvent.getPacketHandler());
    }

    public void handleOnQueue(AsyncPacketInEvent asyncPacketInEvent){
        this.packetInQueue.add(asyncPacketInEvent);
    }

    public void heartBeat() {
        if (++this.tickCount == Integer.MAX_VALUE)
            this.tickCount = 0;
        AsyncPacketInEvent asyncPacketInEvent;
        while ((asyncPacketInEvent = this.packetInQueue.poll()) != null) {
            try {
                this.handle(asyncPacketInEvent);
            } catch (Exception e) {
                ErrorStream.handle(e);
            }
        }
        if (this.tickCount % 200 == 0 && this.packetHandler instanceof PlayHandler)
            ((PlayHandler) this.packetHandler).invokePing();
    }

    public ChannelFuture sendPacket(PacketOut packetOut){
        AsyncPacketOutEvent asyncPacketOutEvent = EventFactory.callAsyncPacketOutEvent(packetOut);
        if(asyncPacketOutEvent.isCancelled()) return null;
        if(this.player != null && asyncPacketOutEvent.getPacket() instanceof PacketPlayOutEntity && ((PacketPlayOutEntity) asyncPacketOutEvent.getPacket()).getEntityId() == this.player.getEntityId()){
            if(!((PacketPlayOutEntity) asyncPacketOutEvent.getPacket()).isSendToOwnClient()) return null;
            int id = ((PacketPlayOutEntity) asyncPacketOutEvent.getPacket()).getEntityId();
            ((PacketPlayOutEntity) asyncPacketOutEvent.getPacket()).setEntityId(0);
            ChannelFuture channelFuture = this.channel.writeAndFlush(asyncPacketOutEvent.getPacket());
            ((PacketPlayOutEntity) asyncPacketOutEvent.getPacket()).setEntityId(id);
            return channelFuture;
        }
        return this.channel.writeAndFlush(asyncPacketOutEvent.getPacket());
    }

    public void disconnect(String reason){
        this.disconnect(new ChatElement(reason));
    }

    public void disconnect(ChatElement reason){
        this.packetHandler.disconnect(reason);
    }

    public void createPlayer(PlayerProfile playerProfile){
        this.playerProfile = playerProfile;
        if(this.getServer().getNetworkCompressionThreshold() != -1){
            InfoStream.handle("new compression");
            this.setCompression(this.getServer().getNetworkCompressionThreshold());
        }

        this.sendPacket(new PacketLoginOutSuccess(this.playerProfile));

        this.setProtocolState(ProtocolState.PLAY);

        this.player = new Player(this.getServer(), this);
        this.player.spawn();
    }

}
