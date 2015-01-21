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
 * @author Marvin seidl
 */
 package org.beaconmc.utils;


public class NibbleArray {

	private final byte[]	data;

	/**
	 * Creates a NibbleArray with the specified size
	 * 
	 * @param size
	 *            size of the nibble array
	 */

	public NibbleArray(int size) {
		data = new byte[size / 2];
	}

	/**
	 * Creates a NibbleArray out of an existing byte array
	 * 
	 * @param data
	 *            byte array containing nibbles
	 */
	public NibbleArray(byte[] data) {
		this.data = data;
	}

	/**
	 * Returns the nibble at the specified position
	 * 
	 * @param index
	 *            index of nibble
	 * @return value of nibble
	 */
	public byte getNibble(int index) {
		byte value = data[index / 2];

		if (index % 2 == 0) {
			return (byte) (value & 0x0f);
		} else {
			return (byte) ((value & 0xf0) >> 4);
		}
	}

	/**
	 * Sets the nibble at the specified position with a specified value
	 * 
	 * @param index
	 *            index of nibble
	 * @param value
	 *            value to set
	 */

	public void setNibble(int index, int value) {
		int arrIndex = index / 2;
		
		value &= 0x0f;

		if(index % 2 == 0){
			data[arrIndex] = (byte) ((data[arrIndex] & 0xf0) | value);
		}else{
			data[arrIndex] = (byte) ((data[arrIndex] & 0x0f) | (value << 4));
		}
	}

	/**
	 * Fills the whole NibbleArray with a specified value
	 * 
	 * @param value
	 *            the value to fill
	 */
	public void fill(int value) {
		for (int i = 0; i < size(); i++) {
			setNibble(i, value);
		}
	}
	
	/**
	 * Checks if the NibbleArray is filled with a specified value
	 * 
	 * @param value
	 *            the value to check
	 * @return whether or not the array is filled with the specified value
	 */
	public boolean isFilledWith(int value){
		for (int i = 0; i < size(); i++)
			if(getNibble(i) != value)
				return false;
		return true;
	}

	/**
	 * Returns the number of nibbles this NibbleArray stores
	 * 
	 * @return number of nibbles
	 */
	public int size() {
		return 2 * data.length;
	}

	/**
	 * Returns the length of the underlying byte array
	 * 
	 * @return length of array
	 */
	public int byteSize() {
		return data.length;
	}

	/**
	 * Returns the underlying byte array
	 * 
	 * @return The underlying byte array
	 */
	public byte[] getByteArray() {
		return data;
	}
}
