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
 package org.beaconmc.network.socket.handler.login;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.beaconmc.BeaconServer;
import org.beaconmc.chat.ChatColor;
import org.beaconmc.chat.ChatElement;
import org.beaconmc.events.EventFactory;
import org.beaconmc.events.player.AsyncPlayerLoginStartEvent;
import org.beaconmc.events.player.PlayerLoginEvent;
import org.beaconmc.logging.ErrorStream;
import org.beaconmc.network.socket.ClientConnection;
import org.beaconmc.network.socket.handler.PacketHandler;
import org.beaconmc.network.socket.protocol.PlayerProfile;
import org.beaconmc.network.socket.protocol.packets.login.in.PacketLoginInEncryptionResponse;
import org.beaconmc.network.socket.protocol.packets.login.in.PacketLoginInStart;
import org.beaconmc.network.socket.protocol.packets.login.out.PacketLoginOutDisconnect;
import org.beaconmc.network.socket.protocol.packets.login.out.PacketLoginOutEncryptionRequest;
import org.beaconmc.utils.EncryptionUtils;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.UUID;

public class LoginHandler extends PacketHandler {

    public LoginHandler(ClientConnection clientConnection) {
        super(clientConnection);
    }

    private int state = START;

    private boolean onlineMode = BeaconServer.getServer().getOnlineMode();
    private byte[] token = EncryptionUtils.generateToken();

    public void handle(PacketLoginInStart packetLoginInStart){
        if(state != START){
            this.clientConnection.disconnect(ChatColor.RED + "Unexpected Packet");
            return;
        }

        if(this.clientConnection.getHandshake().getProtocolVersion() < this.clientConnection.getNetworkHandler().getProtocolVersion().get(0)){
            this.clientConnection.disconnect(ChatColor.RED + "Outdated Client");
        }else if(this.clientConnection.getHandshake().getProtocolVersion() > this.clientConnection.getNetworkHandler().getProtocolVersion().get(this.clientConnection.getNetworkHandler().getProtocolVersion().size()-1)){
            this.clientConnection.disconnect(ChatColor.RED + "Outdated Server");
        }else{
            this.clientConnection.setAuthenticationName(packetLoginInStart.getName());

            AsyncPlayerLoginStartEvent asyncPlayerLoginStartEvent = EventFactory.callAsyncPlayerLoginStartEvent(this.clientConnection, this);

            if(asyncPlayerLoginStartEvent.isCancelled()){
                this.clientConnection.disconnect(asyncPlayerLoginStartEvent.getCancelReason() == null ? "" : asyncPlayerLoginStartEvent.getCancelReason());
                return;
            }

            if(this.onlineMode){
                state = RESPONSE;
                this.clientConnection.sendPacket(new PacketLoginOutEncryptionRequest("", this.clientConnection.getServer().getRsaKeyPair().getPublic().getEncoded(), this.token));
            }else{
                state = DONE;
                this.clientConnection.createPlayer(new PlayerProfile(UUID.nameUUIDFromBytes(("OfflinePlayer:"+this.clientConnection.getAuthenticationName()).getBytes()), this.clientConnection.getAuthenticationName()));
            }
        }
    }

    public void handle(PacketLoginInEncryptionResponse packetLoginInEncryptionResponse){
        if(state != RESPONSE){
            this.clientConnection.disconnect(ChatColor.RED + "Unexpected Packet");
            return;
        }
        PrivateKey privateKey = BeaconServer.getServer().getRsaKeyPair().getPrivate();
        if(!Arrays.equals(this.token, packetLoginInEncryptionResponse.getVerifyToken(privateKey))){
            this.clientConnection.disconnect(ChatColor.RED + "Invalid response!");
        }else{

            PlayerLoginEvent playerLoginEvent = EventFactory.callPlayerLoginEvent(this.clientConnection);
            if(playerLoginEvent.isCancelled()){
                this.clientConnection.disconnect(playerLoginEvent.getCancelReason() == null ? "" : playerLoginEvent.getCancelReason());
                return;
            }

            SecretKey secretKey = packetLoginInEncryptionResponse.getSecretKey(privateKey);
            state = DONE;
            this.clientConnection.setEncryption(secretKey);
            String hash;
            try{
                final MessageDigest digest = MessageDigest.getInstance("SHA-1");
                digest.update("".getBytes());
                digest.update(secretKey.getEncoded());
                digest.update(BeaconServer.getServer().getRsaKeyPair().getPublic().getEncoded());
                hash = new BigInteger(digest.digest()).toString(16);
            }catch(NoSuchAlgorithmException e){
                ErrorStream.handle(e);
                this.clientConnection.disconnect(ChatColor.RED + "Failed to hash login");
                return;
            }
            new ClientLookupThread(this.clientConnection, hash).start();
        }
    }

    @Override
    public void disconnect(ChatElement reason){
        this.clientConnection.sendPacket(new PacketLoginOutDisconnect(reason)).addListeners(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                channelFuture.channel().close();
            }
        });
    }

    public void setOnlineMode(boolean onlineMode){
        if(this.state != START) throw new IllegalStateException("Cannot modify online mode without state start");
        this.onlineMode = onlineMode;
    }

    public boolean isOnlineMode(){
        return this.onlineMode;
    }

    private static final int START = 0;
    private static final int RESPONSE = 1;
    private static final int DONE = 2;
}
