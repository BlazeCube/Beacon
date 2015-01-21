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

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.beaconmc.logging.DebugStream;
import org.beaconmc.logging.ErrorStream;
import org.beaconmc.logging.InfoStream;
import org.beaconmc.logging.abstractlogger.DebugStreamConsole;
import org.beaconmc.logging.abstractlogger.ErrorStreamConsole;
import org.beaconmc.logging.abstractlogger.InfoStreamConsole;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    private static final Logger logger = LogManager.getLogger("beaconmc");

    public static void main(final String[] args){

        ErrorStream.register(new ErrorStreamConsole(logger));
        InfoStream.register(new InfoStreamConsole(logger));
        DebugStream.register(new DebugStreamConsole(logger));

        OptionParser optionParser = new OptionParser(){
            {
                acceptsAll(Arrays.asList("help", "?"), "Show the help");
                acceptsAll(Arrays.asList("version", "v"), "Show the version");
                acceptsAll(Arrays.asList("host", "server-ip", "ip"), "Host to listen on").withRequiredArg().ofType(String.class).describedAs("Server host");
                acceptsAll(Arrays.asList("port", "server-port"), "Port to listen on").withRequiredArg().ofType(Integer.class).describedAs("Server port");
                acceptsAll(Arrays.asList("online-mode"), "Check players session on join").withRequiredArg().ofType(Boolean.class).describedAs("Authentication");
                acceptsAll(Arrays.asList("w", "worlds", "worlds-dir"), "Worlds directory to use").withRequiredArg().ofType(File.class).defaultsTo(new File("worlds")).describedAs("Worlds directory");
                acceptsAll(Arrays.asList("players", "players-dir"), "Players directory to use").withRequiredArg().ofType(File.class).defaultsTo(new File("players")).describedAs("Players directory");
                acceptsAll(Arrays.asList("plugins", "plugins-dir"), "Plugin directory to use").withRequiredArg().ofType(File.class).defaultsTo(new File("plugins")).describedAs("Plugin directory");
                acceptsAll(Arrays.asList("size", "max-players", "slots"), "Maximum amount of players").withRequiredArg().ofType(Integer.class).describedAs("Server size");
                acceptsAll(Arrays.asList("config", "server-config"), "Server properties file to use").withRequiredArg().ofType(File.class).defaultsTo(new File("server.properties")).describedAs("Server properties file");
                acceptsAll(Arrays.asList("bconfig", "beacon-config"), "Beacon config file to use").withRequiredArg().ofType(File.class).defaultsTo(new File("beacon.json")).describedAs("Beacon config file");
            }
        };

        OptionSet options = null;
        try{
            options = optionParser.parse(args);
        }catch(OptionException ex){
            ErrorStream.handle("Error whilst parsing arguments", ex.getLocalizedMessage());
        }

        if(options == null || options.has("help")){
            try{
                optionParser.printHelpOn(System.out);
            }catch(IOException ex){
                ErrorStream.handle(ex);
            }
        }else if(options.has("version")){
            InfoStream.handle(Main.class.getPackage().getImplementationVersion());
        }else{
            InfoStream.handle("Starting BeaconMC now..");
            new BeaconServer(options);
        }

}

}
