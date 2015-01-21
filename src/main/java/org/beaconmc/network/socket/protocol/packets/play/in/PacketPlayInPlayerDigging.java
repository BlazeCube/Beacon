/**	
 * Beacon - Open Source Minecraft Server
 * Copyright (C) 2014  Marvin Seidl
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
 * @author Marvin Seidl
 */
 package org.beaconmc.network.socket.protocol.packets.play.in;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.beaconmc.network.socket.protocol.packets.PacketIn;
import org.beaconmc.network.socket.protocol.packets.PacketSerializer;
import org.beaconmc.network.socket.protocol.packets.play.PacketPlay;
import org.beaconmc.utils.Vector3i;

@NoArgsConstructor
@AllArgsConstructor
public class PacketPlayInPlayerDigging extends PacketPlay implements PacketIn{
	
	@Getter
	private Status status;
	@Getter
	private Vector3i position;
	@Getter
	private byte face;
	
	@Override
	public void read(PacketSerializer packetSerializer, int protocolVersion) {
		this.status = Status.getById(packetSerializer.readByte());
		this.position = packetSerializer.readPosition();
		this.face = packetSerializer.readByte();
	}
	
    public enum Status{
    	STARTED_DIGGING(0),
    	CANCELLED_DIGGING(1),
    	FINISHED_DIGGING(2),
    	DROP_ITEMSTACK(3),
    	DROP_ITEM(4),
    	SHOOT_ARROW(5);//+ Finish eating
    	
    	private byte status;
    	
    	private Status(int status){
    		this.status = (byte) status;
    	}
    	
    	public byte getStatus(){
    		return status;
    	}
    	
    	public static Status getById(byte b){
    		return byId.get(b);
    	}
    	
    	private static final Map<Byte,Status> byId = new HashMap<>();
    	
    	static{
    		for(Status s : Status.values()){
    			byId.put(s.getStatus(), s);
    		}
    	}
    }
    
    //TODO: Face
}
