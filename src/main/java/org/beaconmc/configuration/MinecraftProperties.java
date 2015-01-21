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
 package org.beaconmc.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class MinecraftProperties{

    private final File propertiesFile;
    private final Properties properties;

    public MinecraftProperties(File propertiesFile){

        this.propertiesFile = propertiesFile;
        this.properties = new Properties();

        FileInputStream input = null;
        try{
            input = new FileInputStream(this.propertiesFile);
            this.properties.load(input);
        }catch(IOException ex){
            this.saveFile();
        }finally {
            if(input != null)
                try{
                    input.close();
                }catch(IOException e){
                    //Nothing
                }
        }
    }

    private void saveFile(){
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(this.propertiesFile);
            this.properties.store(output, "BeaconServer properties file");
        } catch (Exception exception) {
            this.saveFile();
        } finally {
            if(output != null)
                try{
                    output.close();
                }catch(IOException e){
                    //Nothing
                }
        }
    }

    private String getString(String key, String defaultvalue){
        if(!this.properties.containsKey(key)){
            this.properties.setProperty(key, defaultvalue);
            this.saveFile();
        }
        return this.properties.getProperty(key);
    }

    private int getInt(String key, int defaultvalue){
        try{
            return Integer.parseInt(this.getString(key, defaultvalue+""));
        }catch(NumberFormatException ex){
            this.properties.setProperty(key, defaultvalue+"");
            this.saveFile();
            return defaultvalue;
        }
    }

    private boolean getBoolean(String key, boolean defaultvalue){
        try{
            return Boolean.parseBoolean(this.getString(key, defaultvalue+""));
        }catch(Exception ex){
            this.properties.setProperty(key, defaultvalue+"");
            this.saveFile();
            return defaultvalue;
        }
    }

    public boolean getAllowFlight(){
        return this.getBoolean("allow-flight", false);
    }

    public boolean getAllowNether(){
        return this.getBoolean("allow-nether", false);
    }

    public boolean getAnnouncePlayerAchievements(){
        return this.getBoolean("announce-player-achievements", true);
    }

    public int getDifficulty(){
        return this.getInt("difficulty", 1);
    }

    public boolean getEnableQuery(){
        return this.getBoolean("enable-query", false);
    }

    public boolean getEnableRcon(){
        return this.getBoolean("enable-rcon", false);
    }

    public boolean getEnableCommandBlock(){
        return this.getBoolean("enable-command-block", false);
    }

    public boolean getForceGamemode(){
        return this.getBoolean("force-gamemode", false);
    }

    public int getGamemode(){
        return this.getInt("gamemode", 0);
    }

    public boolean getGenerateStructures(){
        return this.getBoolean("generate-structures", true);
    }

    public String getGeneratorSettings(){
        return this.getString("generator-settings", "");
    }

    public boolean getHardcore(){
        return this.getBoolean("hardcore", false);
    }

    public String getLevelName(){
        return this.getString("level-name", "world");
    }

    public String getLevelSeed(){
        return this.getString("level-seed", "");
    }

    public String getLevelType(){
        return this.getString("level-type", "DEFAULT");
    }

    public int getMaxBuildHeight(){
        return this.getInt("max-build-height", 256);
    }

    public int getMaxPlayers(){
        return this.getInt("max-players", 20);
    }

    public String getMotd(){
        return this.getString("motd", "A Minecraft Server running with Beacon");
    }

    public int getNetworkCompressionThreshold(){
        return this.getInt("network-compression-threshold", 256);
    }

    public boolean getOnlineMode(){
        return this.getBoolean("online-mode", true);
    }

    public int getOpPermissionLevel(){
        return this.getInt("op-permission-level", 4);
    }

    public int getPlayerIdleTimeout(){
        return this.getInt("player-idle-timeout", 0);
    }

    public boolean getPvP(){
        return this.getBoolean("pvp", true);
    }

    public int getQueryPort(){
        return this.getInt("query.port", 25565);
    }

    public String getResourcePack(){
        return this.getString("resource-pack", "");
    }

    public String getRconPassword(){
        return this.getString("rcon.password", "");
    }

    public int getRconPort(){
        return this.getInt("rcon.port", 25575);
    }

    public String getServerIp(){
        return this.getString("server-ip", "");
    }

    public int getServerPort(){
        return this.getInt("server-port", 25565);
    }

    public boolean getSnooperEnabled(){
        return this.getBoolean("snooper-enabled", true);
    }

    public boolean getSpawnAnimals(){
        return this.getBoolean("spawn-animals", true);
    }

    public boolean getSpawnMonsters(){
        return this.getBoolean("spawn-monsters", true);
    }

    public boolean getSpawnNpcs(){
        return this.getBoolean("spawn-npcs", true);
    }

    public int getSpawnProtection(){
        return this.getInt("spawn-protection", 10);
    }

    public int getViewDistance(){
        return this.getInt("view-distance", 10);
    }

    public boolean getWhitelist(){
        return this.getBoolean("white-list", false);
    }

}
