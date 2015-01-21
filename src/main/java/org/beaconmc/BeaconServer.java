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
 package org.beaconmc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyPair;

import javax.imageio.ImageIO;

import joptsimple.OptionSet;
import lombok.Getter;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.beaconmc.configuration.MinecraftProperties;
import org.beaconmc.logging.ErrorStream;
import org.beaconmc.logging.InfoStream;
import org.beaconmc.network.socket.NetworkHandler;
import org.beaconmc.network.socket.protocol.ServerPing;
import org.beaconmc.utils.EncryptionUtils;
import org.beaconmc.world.WorldsManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BeaconServer {

    private static BeaconServer beaconServer;
    public static Gson gson = new GsonBuilder().create();

    public static BeaconServer getServer(){
        return beaconServer;
    }

    public String getName(){
        return "BeaconMC";
    }

	private final OptionSet	options;
	private final MinecraftProperties minecraftProperties;
	private final NetworkHandler networkHandler;
	private final WorldsManager worldsManager;

    @Getter
    private KeyPair rsaKeyPair = EncryptionUtils.generateRSAPair();

    private String favicon;

    public BeaconServer(OptionSet options){
        if(beaconServer != null)
            throw new IllegalStateException("There is already an instance of "+this.getClass().getName());

        beaconServer = this;
        this.options = options;

        this.minecraftProperties = new MinecraftProperties((File)this.options.valueOf("server-config"));
        this.networkHandler = new NetworkHandler(this);

        if(new File("server-icon.png").exists()){
            try{
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                OutputStream b64 = new Base64OutputStream(os);
                BufferedImage icon = ImageIO.read(new File("server-icon.png"));
                if(icon.getWidth() != 64 || icon.getHeight() != 64){
                    ErrorStream.handle("server-icon cannot be loaded", "server-icon.png must be 64x64");
                }else{
                    ImageIO.write(icon, "png", b64);
                    this.favicon ="data:image/png;base64, "+ os.toString("UTF-8");
                }
                os.close();
            }catch(IOException ex){
                ErrorStream.handle(ex);
            }
        }
        
        this.worldsManager = new WorldsManager(this);
        this.worldsManager.loadAllworlds();
        
        //Test Worlds
        System.out.println(this.worldsManager.getWorld("world").getSpawnLocation());
       

        InetAddress inetAddress = null;
        if(this.getIP().length() > 0)
            try{
                inetAddress = InetAddress.getByName(this.getIP());
            } catch (UnknownHostException e) {
                //None
            }
        try{
            this.networkHandler.bind(inetAddress, this.getPort());
        }catch(Exception ex){
            InfoStream.handle("***** Failed to bind to port ****");
            InfoStream.handle(ex.getMessage());
            InfoStream.handle("Perhaps a server is already running on that port?");
            ErrorStream.handle("Failed to bind to port", ex.getMessage());
            System.out.println();
            System.exit(1);
            return;
        }
        InfoStream.handle("Ready for connections. Listen to "+(this.getIP().length() > 0? this.getIP():"*")+":"+this.getPort());
    }

    public ServerPing createServerPing(){
        return this.createServerPing(-1);
    }

    public ServerPing createServerPing(int protocolVersion){

        ServerPing.Protocol protocol;
        if(protocolVersion != -1 && this.networkHandler.getProtocolVersion().contains(protocolVersion))
            protocol = new ServerPing.Protocol(this.networkHandler.getProtocolName(), protocolVersion);
        else
            protocol = new ServerPing.Protocol(this.networkHandler.getProtocolName(), this.networkHandler.getProtocolVersion().get(this.networkHandler.getProtocolVersion().size()-1));

        return new ServerPing(
            protocol,
            new ServerPing.Players(30, (int)(Math.random()*5000)),
            new ServerPing.Description(this.getMotd()),
            this.getFavicon()
        );
    }

    public int getPort(){
        return this.options.has("port") ? (Integer)this.options.valueOf("port") : this.minecraftProperties.getServerPort();
    }

    public String getIP(){
        return this.options.has("host") ? (String)this.options.valueOf("host") : this.minecraftProperties.getServerIp();
    }

    public boolean getOnlineMode(){
        return this.options.has("online-mode") ? (Boolean)this.options.valueOf("online-mode") : this.minecraftProperties.getOnlineMode();
    }

    public int getMaxPlayers(){
        return this.options.has("max-players") ? (Integer)this.options.valueOf("max-players") : this.minecraftProperties.getMaxPlayers();
    }

    public GameMode getDefaultGamemode(){
        return GameMode.getById(this.minecraftProperties.getGamemode());
    }

    public int getDifficulty(){
        return this.minecraftProperties.getDifficulty();
    }

    public int getNetworkCompressionThreshold(){
        return this.minecraftProperties.getNetworkCompressionThreshold();
    }

    public String getFavicon(){
        return this.favicon;
    }

    public String getMotd(){
        return this.minecraftProperties.getMotd();
    }

	public File getWorldsDir() {
		return (File) this.options.valueOf("worlds-dir");
	}

	public File getPluginsDir() {
		return (File) this.options.valueOf("plugins-dir");
	}
	
	public WorldsManager getWorldsManager(){
		return this.worldsManager;
	}
}
