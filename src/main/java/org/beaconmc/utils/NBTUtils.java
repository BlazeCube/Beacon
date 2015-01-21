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
 package org.beaconmc.utils;

import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.DoubleTag;
import org.spout.nbt.FloatTag;
import org.spout.nbt.IntArrayTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.LongTag;
import org.spout.nbt.ShortArrayTag;
import org.spout.nbt.ShortTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.TagType;

public class NBTUtils {
	public static boolean getBoolean(CompoundMap parent, String field, boolean defaultValue) {
		if (parent == null)
			return defaultValue;

		if (!parent.containsKey(field))
			return defaultValue;

		if (parent.get(field).getType() != TagType.TAG_BYTE)
			return defaultValue;

		return ((ByteTag) parent.get(field)).getBooleanValue();
	}

	public static byte getByte(CompoundMap parent, String field, byte defaultValue) {
		if (parent == null)
			return defaultValue;

		if (!parent.containsKey(field))
			return defaultValue;

		if (parent.get(field).getType() != TagType.TAG_BYTE)
			return defaultValue;

		return ((ByteTag) parent.get(field)).getValue();
	}

	public static int getInt(CompoundMap parent, String field, int defaultValue) {
		if (parent == null)
			return defaultValue;

		if (!parent.containsKey(field))
			return defaultValue;

		if (parent.get(field).getType() != TagType.TAG_INT)
			return defaultValue;

		return ((IntTag) parent.get(field)).getValue();
	}

	public static float getFloat(CompoundMap parent, String field, float defaultValue) {
		if (parent == null)
			return defaultValue;

		if (!parent.containsKey(field))
			return defaultValue;

		if (parent.get(field).getType() != TagType.TAG_FLOAT)
			return defaultValue;

		return ((FloatTag) parent.get(field)).getValue();
	}

	public static double getDouble(CompoundMap parent, String field, double defaultValue) {
		if (parent == null)
			return defaultValue;

		if (!parent.containsKey(field))
			return defaultValue;

		if (parent.get(field).getType() != TagType.TAG_DOUBLE)
			return defaultValue;

		return ((DoubleTag) parent.get(field)).getValue();
	}

	public static long getLong(CompoundMap parent, String field, long defaultValue) {
		if (parent == null)
			return defaultValue;

		if (!parent.containsKey(field))
			return defaultValue;

		if (parent.get(field).getType() != TagType.TAG_LONG)
			return defaultValue;

		return ((LongTag) parent.get(field)).getValue();
	}

	public static short getShort(CompoundMap parent, String field, short defaultValue) {
		if (parent == null)
			return defaultValue;

		if (!parent.containsKey(field))
			return defaultValue;

		if (parent.get(field).getType() != TagType.TAG_SHORT)
			return defaultValue;

		return ((ShortTag) parent.get(field)).getValue();
	}

	public static String getString(CompoundMap parent, String field, String defaultValue) {
		if (parent == null)
			return defaultValue;

		if (!parent.containsKey(field))
			return defaultValue;

		if (parent.get(field).getType() != TagType.TAG_STRING)
			return defaultValue;

		return ((StringTag) parent.get(field)).getValue();
	}
	
	public static byte[] getByteArray(CompoundMap parent, String field, byte[] defaultValue) {
		if (parent == null)
			return defaultValue;

		if (!parent.containsKey(field))
			return defaultValue;

		if (parent.get(field).getType() != TagType.TAG_BYTE_ARRAY)
			return defaultValue;

		return ((ByteArrayTag) parent.get(field)).getValue();
	}
	
	public static short[] getShortArray(CompoundMap parent, String field, short[] defaultValue) {
		if (parent == null)
			return defaultValue;

		if (!parent.containsKey(field))
			return defaultValue;

		if (parent.get(field).getType() != TagType.TAG_SHORT_ARRAY)
			return defaultValue;

		return ((ShortArrayTag) parent.get(field)).getValue();
	}
	
	public static int[] getIntArray(CompoundMap parent, String field, int[] defaultValue) {
		if (parent == null)
			return defaultValue;

		if (!parent.containsKey(field))
			return defaultValue;

		if (parent.get(field).getType() != TagType.TAG_INT_ARRAY)
			return defaultValue;

		return ((IntArrayTag) parent.get(field)).getValue();
	}
}