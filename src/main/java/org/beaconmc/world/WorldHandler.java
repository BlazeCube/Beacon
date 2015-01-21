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
 package org.beaconmc.world;

import java.util.LinkedList;

import org.beaconmc.logging.ErrorStream;

public class WorldHandler extends Thread{

	private static final int SKIPMS = 50; // 20 TPS
	private static final int VALUES = 10; // Values to keep for tps measurement
	
    private World world;

    private boolean shutdown = false;

	private LinkedList<Long>	times;

	public WorldHandler(World world) {
		this.world = world;
		times = new LinkedList<Long>();
    }

    public void shutdown(){
        this.shutdown = true;
    }

	@Override
	public void run() {
		while (!this.shutdown) {
			long startTime = System.nanoTime() / 1000;

			world.heartBeat();

			long elapsedTime = System.nanoTime()/1000 - startTime;

			times.add(elapsedTime);
			
			if (times.size() > VALUES)
				times.removeFirst();

			if (elapsedTime < SKIPMS) {
				try {
					Thread.sleep(SKIPMS - elapsedTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					ErrorStream.handle(e);
				}
			}
		}
	}

	// NOTE: can be way more than 20... still ticks at max of 20
	// NOTE: might get problems with multithreading ;)
	public double getTPS() {		
		double average = 0.0;

		for (long time : times)
			average += (time < 50000) ? 50000 : time;	
		
		average /= (double)times.size();

		return 1000000.0 / average;
	}
	
	//For eieste <3 my love....
	public double getAwesomeTPS(){
		double average = 0.0;

		for (long time : times)
			average += time;	
		
		average /= (double)times.size();

		return 1000000.0 / average;
	}
}
