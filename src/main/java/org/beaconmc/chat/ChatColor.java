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
 package org.beaconmc.chat;

import com.google.common.collect.Maps;

import java.awt.Color;
import java.util.Map;

public enum ChatColor {

	BLACK('0', "black", new Color(0, 0, 0)),
	DARK_BLUE('1', "dark_blue", new Color(0, 0, 170)),
	DARK_GREEN('2', "dark_green", new Color(0, 170, 0)),
	DARK_AQUA('3', "dark_aqua", new Color(0, 170, 170)),
	DARK_RED('4', "dark_red", new Color(170, 0, 0)),
	DARK_PURPLE('5', "dark_purple", new Color(17, 0, 170)),
	GOLD('6',"gold", new Color(255, 170, 0)),
	GRAY('7', "gray", new Color(170, 170, 170)),
	DARK_GRAY('8', "dark_gray",	new Color(85, 85, 85)),
	BLUE('9', "blue", new Color(85, 85, 255)),
	GREEN('a', "green", new Color(85, 255,85)),
	AQUA('b', "aqua", new Color(85, 255, 225)),
	RED('c', "red", new Color(255, 85, 85)),
	LIGHT_PURPLE('d', "light_purple", new Color(255, 85, 225)),
	YELLOW('e', "yellow", new Color(255, 255, 85)),
	WHITE('f',"white", new Color(255, 255, 225)),
	MAGIC('k', "obfuscated"), 
	BOLD('l', "bold"),
	STRIKETHROUGH('m',"strikethrough"),
	UNDERLINE('n', "underline"),
	ITALIC('o', "italic"),
	RESET('r', "reset");

	private final static Map<Character, ChatColor> BY_CHAR = Maps.newHashMap();
	public final static char COLOR_CHAR = '\u00A7';

	private char colorchar;
	private final String name;
	private boolean isFormat;
	private Color color;

	private ChatColor(char colorchar, String name) {
		this.colorchar = colorchar;
		this.name = name;
		this.isFormat = true;
		this.color = null;
	}

	private ChatColor(char colorchar, String name, Color color) {
		this.colorchar = colorchar;
		this.name = name;
		this.isFormat = false;
		this.color = color;
	}

	public char getChar() {
		return this.colorchar;
	}

	public String getName() {
		return this.name;
	}

	public boolean isFormat() {
		return this.isFormat;
	}

	public Color getColor() {
		return this.color;
	}

	public boolean isColor() {
		return !this.isFormat && this != ChatColor.RESET;
	}

	@Override
	public String toString() {
		return new String(new char[] { COLOR_CHAR, this.colorchar });
	}

	public static ChatColor fromChar(char code) {
		return BY_CHAR.get(code);
	}

	public static String stripColor(String input) {
		return input.replaceAll("(" + COLOR_CHAR + "([a-f0-9klmnor]))", "");
	}

	public static String translateAlternateColorCodes(char altchar, String input) {
		return input.replaceAll("(" + altchar + "([a-f0-9klmnor]))", "§$2");
	}

	static {
		for (ChatColor color : values()) {
			BY_CHAR.put(color.getChar(), color);
		}
	}
}
