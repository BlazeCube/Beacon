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
 package org.beaconmc.utils;

import java.util.Random;

public class MathUtils {

	private static final Random random = new Random();

	public static double square(double value) {
		return value * value;
	}

	public static Random getRandom() {
		return random;
	}

	public static double round(double number, int position) {
		double pow = Math.pow(10, position);
		return Math.round(number * pow) / pow;
	}

	public static double floor(double value){
		return Math.floor(value);
	}

	public static int toPacketInt(double value){
		return (int)floor(value*32D);
	}

	public static double fromPacketInt(int value){
		return (double)value/32D;
	}

	public static byte toPacket360Fraction(float value){
		return (byte)(value*256F/360F);
	}

}
