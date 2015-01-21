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

import com.google.common.collect.Lists;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.beaconmc.BeaconServer;
import org.beaconmc.entity.living.Player;
import org.beaconmc.network.socket.pipeline.ChannelInitalizer;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NetworkHandler extends Thread {

    private final static ServerBootstrap serverBootstrap = new ServerBootstrap();
    private final static EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final static EventLoopGroup workerGroup = new NioEventLoopGroup();

    private static ChannelFuture channelFuture;

    public List<Integer> getProtocolVersion(){
        return Arrays.asList(47);
    }

    public String getProtocolName(){
        return "1.8.1";
    }

    private final BeaconServer beaconServer;

    private final ArrayList<ClientConnection> connections = Lists.newArrayList();

    private final ArrayList<Player> players = Lists.newArrayList();

    private boolean shutdown = false;

    public NetworkHandler(BeaconServer beaconServer){
        this.beaconServer = beaconServer;
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitalizer(this))
                .childOption(ChannelOption.TCP_NODELAY, true);
    }

    public void hasPlayer(ClientConnection clientConnection){
        this.players.add(clientConnection.getPlayer());
    }

    public ClientConnection newConnection(ChannelHandlerContext channelHandlerContext){
        ClientConnection clientConnection = new ClientConnection(channelHandlerContext, this);
        connections.add(clientConnection);
        return clientConnection;
    }

    public void destroy(ClientConnection clientConnection){
        this.connections.remove(clientConnection);
        if(clientConnection.getPlayer() != null)
            this.players.remove(clientConnection.getPlayer());
    }

    public void bind(InetAddress host, int port){
        channelFuture = serverBootstrap.localAddress(host, port).bind().syncUninterruptibly();
        this.start();
    }

    public BeaconServer getServer(){
        return this.beaconServer;
    }

    public void shutdown(){
        this.shutdown = true;
        if(channelFuture != null)
            channelFuture.channel().close();
    }

}
