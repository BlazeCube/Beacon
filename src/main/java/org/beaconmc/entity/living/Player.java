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
 package org.beaconmc.entity.living;

import lombok.Getter;

import org.beaconmc.BeaconServer;
import org.beaconmc.chat.ChatElement;
import org.beaconmc.logging.DebugStream;
import org.beaconmc.logging.InfoStream;
import org.beaconmc.network.socket.ClientConnection;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutChatMessage;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutJoinGame;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutPlayerPositionAndLook;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutPluginMessage;
import org.beaconmc.network.socket.protocol.packets.play.out.PacketPlayOutSpawnPosition;
import org.beaconmc.utils.ByteArrayUtils;
import org.beaconmc.world.ChunkLocation;
import org.beaconmc.world.Vector3d;

public class Player extends HumanEntity{

    @Getter
    protected ClientConnection clientConnection;

    public Player(BeaconServer server, ClientConnection clientConnection) {
        super(server, server.getWorldsManager().getWorlds().get(0).getSpawnLocation().add(new Vector3d(0, 20, 0)), clientConnection.getPlayerProfile());
        this.clientConnection = clientConnection;
    }

    @Override
    public void spawn() {
    	InfoStream.handle("New Player joined the server: " + this.clientConnection.getPlayer().getName());
    	
        this.clientConnection.sendPacket(new PacketPlayOutJoinGame(this));
        this.clientConnection.sendPacket(new PacketPlayOutPluginMessage("MC|Brand", ByteArrayUtils.toByteArray(this.server.getName())));

        sendSpawnChunks();
        
        this.clientConnection.sendPacket(new PacketPlayOutSpawnPosition(this.getLocation()));

        this.clientConnection.sendPacket(new PacketPlayOutPlayerPositionAndLook(this.getLocation(), (byte)0x00));
    }
    
    @Override
    public void heartBeat(){
        this.clientConnection.heartBeat();
        super.heartBeat();
    }
    
    //Convinient Methods
    public void sendChatMessage(String message){
    	this.clientConnection.getPlayer().getWorld().getEntityManager().sendPacket(
        		new PacketPlayOutChatMessage(new ChatElement(message),PacketPlayOutChatMessage.ChatMessagePosition.CHATBOX));
    }
    
    private void sendSpawnChunks(){
    	//TODO: Server option
    	//Viewdistance in Chunks
    	//TODO: Chunk Bulk
    //	int viewDistance = Math.min(16, Math.max(0,this.clientConnection.getClientSettings().getViewDistance()));
    	int viewDistance = 2;
    	
    	DebugStream.handle("Sending spawn chunks to " + this.clientConnection.getPlayer().getName());
    	
    	
    	ChunkLocation base = this.getLocation().toChunkLocation();
    	
//    	List<PacketPlayOutChunkData> chunks = new LinkedList<>();
    	
    	
    	for(int x = -viewDistance;x <= viewDistance;x++){
    		for(int z = -viewDistance;z <= viewDistance;z++){
        		ChunkLocation cl = new ChunkLocation(base.getWorld(),base.getX() + x,base.getZ() + z);
        		this.clientConnection.sendPacket(cl.getChunk().getDataPacket(true, true));
        		//chunks.add(cl.getChunk().getDataPacket(true, true));
        	}
    	}
    
//    	
//    	List<PacketPlayOutChunkBulk> bulks = PacketPlayOutChunkBulk.bulkifyChunks(chunks, true);
//    	
//    	for(PacketPlayOutChunkBulk bulk : bulks)
//    		this.clientConnection.sendPacket(bulk);
    		
    	
    	
    }
}
