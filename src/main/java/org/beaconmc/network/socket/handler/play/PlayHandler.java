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
 package org.beaconmc.network.socket.handler.play;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import org.beaconmc.BeaconServer;
import org.beaconmc.GameMode;
import org.beaconmc.chat.ChatElement;
import org.beaconmc.entity.living.Player;
import org.beaconmc.logging.DebugStream;
import org.beaconmc.logging.InfoStream;
import org.beaconmc.material.Material;
import org.beaconmc.network.socket.ClientConnection;
import org.beaconmc.network.socket.handler.PacketHandler;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInAnimation;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInChatMessage;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInClientSettings;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInKeepAlive;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInPlayer;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInPlayerDigging;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInPlayerDigging.Status;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInPlayerLook;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInPlayerPosition;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInPlayerPositionAndLook;
import org.beaconmc.network.socket.protocol.packets.play.in.PacketPlayInPluginMessage;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutChatMessage;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutDisconnect;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutKeepAlive;
import org.beaconmc.utils.MathUtils;
import org.beaconmc.world.Block;
import org.beaconmc.world.BlockLocation;
import org.beaconmc.world.Location;

public class PlayHandler extends PacketHandler {

    private long lastPingPacket = -1;
    private boolean invokedPing = false;

    public PlayHandler(ClientConnection clientConnection) {
        super(clientConnection);
    }

    public void invokePing(){
        if(lastPingPacket != -1){
            invokedPing = true;
            return;
        }
        lastPingPacket = 0;
        invokedPing = false;
        this.clientConnection.sendPacket(new PacketPlayOutKeepAlive(MathUtils.getRandom().nextInt())).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                PlayHandler.this.lastPingPacket = System.currentTimeMillis();
            }
        });
    }

    @Override
    public void disconnect(ChatElement reason){
        this.clientConnection.sendPacket(new PacketPlayOutDisconnect(reason));
    }

    public void handle(PacketPlayInKeepAlive packetPlayInKeepAlive){
        this.clientConnection.setPing((int)(lastPingPacket-System.currentTimeMillis()));
        this.lastPingPacket = -1;
        if(this.invokedPing)
            this.invokePing();
    }

    public void handle(PacketPlayInPluginMessage packetPlayInPluginMessage){
        InfoStream.handle("New PluginMessage");//TODO -> handle
        InfoStream.handle("Channel: " + packetPlayInPluginMessage.getChannel());
        InfoStream.handle("Data: " + new String(packetPlayInPluginMessage.getData()));
    }

    public void handle(PacketPlayInPlayer packetPlayInPlayer){
        this.clientConnection.getPlayer().setOnGround(packetPlayInPlayer.isOnGround());
    }

    public void handle(PacketPlayInPlayerPosition packetPlayInPlayerPosition){
        this.clientConnection.getPlayer().setLocation(packetPlayInPlayerPosition.getLocation(), false);
        this.clientConnection.getPlayer().setOnGround(packetPlayInPlayerPosition.isOnGround());
    }

    public void handle(PacketPlayInPlayerPositionAndLook packetPlayInPlayerPositionAndLook){
        this.clientConnection.getPlayer().setLocation(packetPlayInPlayerPositionAndLook.getLocation(), false);
        this.clientConnection.getPlayer().setOnGround(packetPlayInPlayerPositionAndLook.isOnGround());
    }

    public void handle(PacketPlayInPlayerLook packetPlayInPlayerLook){
        Location location = this.clientConnection.getPlayer().getLocation();
        location.setYaw(packetPlayInPlayerLook.getYaw());
        location.setPitch(packetPlayInPlayerLook.getPitch());
        this.clientConnection.getPlayer().setLocation(location, false);
    }

    public void handle(PacketPlayInClientSettings packetPlayInClientSettings){
        this.clientConnection.setClientSettings(packetPlayInClientSettings);
    }
    
    public void handle(PacketPlayInAnimation packetPlayInAnimation){
    	//TODO: handling
    }
    
    public void handle(PacketPlayInPlayerDigging packetPlayInPlayerDigging){
    	//TODO: Correct handling
    	DebugStream.handle("Player digging Packet:");
    	DebugStream.handle("     " + packetPlayInPlayerDigging.getPosition() + 
    			" Status: " + packetPlayInPlayerDigging.getStatus() + 
    			" Face:" + packetPlayInPlayerDigging.getFace());
    	DebugStream.handle("     " + "Gamemode: " + this.clientConnection.getPlayer().getGameMode());
    	
    	//If Creative, break the block instantly
    	if(packetPlayInPlayerDigging.getStatus() == Status.STARTED_DIGGING && this.clientConnection.getPlayer().getGameMode() == GameMode.CREATIVE){
    		BlockLocation bl = new BlockLocation(this.clientConnection.getPlayer().getWorld(), packetPlayInPlayerDigging.getPosition());
    		bl.getBlock().setType(Material.AIR);
    	}
    }
    
    public void handle(PacketPlayInChatMessage packetPlayInChatMessage){
    	//TODO: Chat system
    	String playerMessage = this.clientConnection.getPlayer().getName() 
    			+ ": " + packetPlayInChatMessage.getMessage();
    	
    	this.clientConnection.getPlayer().getWorld().getEntityManager().sendPacket(
    		new PacketPlayOutChatMessage(new ChatElement(playerMessage),PacketPlayOutChatMessage.ChatMessagePosition.CHATBOX));
    	
    	InfoStream.handle("[Chat] " + playerMessage);
    	
    	//Test commands
    	if(!packetPlayInChatMessage.getMessage().startsWith("/"))
    		return;
    	
    	String cmd = packetPlayInChatMessage.getMessage().substring(1);
    	Player p = this.clientConnection.getPlayer();
    	
    	switch(cmd){
    		case "tps":
    			p.sendChatMessage("TPS of '" + p.getWorld().getName() + "' : "  
    					+ BeaconServer.getServer().getWorldsManager().getWorldHandler(p.getWorld().getName()).getTPS() +
    					" ( " +BeaconServer.getServer().getWorldsManager().getWorldHandler(p.getWorld().getName()).getAwesomeTPS() + " )");
    			break;
    		case "chunk":
    			this.clientConnection.sendPacket(p.getLocation().toChunkLocation().getChunk().getDataPacket(true, true));
    			break;
    		case "lightinfo":
    			Block b = this.clientConnection.getPlayer().getLocation().toBlockLocation().getBlock();
    			p.sendChatMessage("Skylight: " + b.getSkyLight());
    			p.sendChatMessage("BlockLight: " + b.getBlockLight());
    			

    			break;
    	}
    	
    }
}
