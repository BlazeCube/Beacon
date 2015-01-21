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

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.beaconmc.BeaconServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ChatElement {

    private static final Pattern url_pattern = Pattern.compile("((https?:)?//)?([a-z0-9_öäüß]+\\.)+([a-z]{2,3})(/([\\w;?^#äöüß.=/]+)?)?");
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(ChatElement.class, new ChatElementTypeAdapter()).create();

	private String text;
	private ArrayList<ChatElement> extra;
	private ClickElement clickEvent;
	private HoverElement hoverEvent;
	private String color = ChatColor.WHITE.getName();
	private boolean bold = false;
	private boolean underlined = false;
	private boolean italic = false;
	private boolean strikethrough = false;
	private boolean obfuscated = false;
	private String insertion;

    public ChatElement(){
        this.text = "";
    }

	public ChatElement(String text) {
		generate(text);
	}

	public void setText(String text) {
		generate(text);
	}

	private void generate(String text) {
		String color = "";

		StringBuilder sb = new StringBuilder();
		int count = 0;

        text = text.replace("§", ChatColor.COLOR_CHAR+"");
		while (count < text.length()) {
			char character = text.charAt(count);
			if (character == ChatColor.COLOR_CHAR && count != text.length() - 1) {
                ChatColor cc = ChatColor.fromChar(text.charAt(count + 1));

                if (cc != null && sb.length() > 0) {
                    if (this.text == null) {
                        this.text = sb.toString();
                        this.applyColor(color);
                    } else {
                        add(new ChatElement(sb.toString()).applyColor(color));
                    }

                    sb = new StringBuilder();
                }

                if (cc != null && cc.isFormat()) {
                    color = color + cc.toString();
                }
                if (cc != null && cc.isColor()) {
                    color = cc.toString();
                }
                if(cc != null && cc == ChatColor.RESET){
                    color = "";
                }
                count += 2;
            } else if(character != ' ' && url_pattern.matcher(text.substring(count, (text.indexOf(' ', count) != -1)?text.indexOf(' ', count):text.length())).matches()){
                if (sb.length() > 0) {
                    if (this.text == null) {
                        this.text = sb.toString();
                        this.applyColor(color);
                    } else {
                        add(new ChatElement(sb.toString()).applyColor(color));
                    }
                }

                String url_text = text.substring(count, (text.indexOf(' ', count) != -1)?text.indexOf(' ', count):text.length());

                String url = (!url_text.toLowerCase().startsWith("http://") && !url_text.toLowerCase().startsWith("https://")) ? "http://"+(url_text.toLowerCase().startsWith("//")?url_text.substring(2):url_text):url_text;

                if(this.text == null){
                    this.text = url_text;
                    this.setClickEvent(new ClickElement(ClickElement.Action.OPEN_URL, url));
                    this.applyColor(color);
                }else{
                    add(new ChatElement(url_text).applyColor(color).setClickEvent(new ClickElement(ClickElement.Action.OPEN_URL, url)));
                }

                sb = new StringBuilder();
                count +=url_text.length();
			} else {
				sb.append(character);
				count++;
			}
		}
		if (sb.length() > 0) {
			if (this.text == null) {
				this.text = sb.toString();
				this.applyColor(color);
			} else {
				add(new ChatElement(sb.toString()).applyColor(color));
			}
		}
        if(this.text == null)
            this.text = "";
	}

	public ChatElement applyColor(String color) {
		String[] split = color.split(ChatColor.COLOR_CHAR + "");
		for (String part : split) {
			if (part.length() == 0 || part.equalsIgnoreCase(ChatColor.COLOR_CHAR + "")) continue;
			ChatColor chatcolor = ChatColor.fromChar(part.replace(ChatColor.COLOR_CHAR + "", "").charAt(0));

			if (chatcolor != null) {
				if (chatcolor.isColor()) {
					this.color = chatcolor.getName();
				} else {
					switch (chatcolor) {
						case BOLD:
							this.bold = true;
							break;
						case UNDERLINE:
							this.underlined = true;
							break;
						case ITALIC:
							this.italic = true;
							break;
						case STRIKETHROUGH:
							this.strikethrough = true;
							break;
						case MAGIC:
							this.obfuscated = true;
							break;
					}
				}
			}
		}
		return this;
	}

	public String getText() {
		return this.text;
	}

	public ClickElement getClickEvent() {
		return this.clickEvent;
	}

	public HoverElement getHoverEvent() {
		return this.hoverEvent;
	}

	public ChatElement setClickEvent(ClickElement element) {
		this.clickEvent = element;
		return this;
	}

	public ChatElement setHoverEvent(HoverElement element) {
		this.hoverEvent = element;
		return this;
	}

	public ChatElement add(ChatElement... element) {
		if (extra == null) extra = new ArrayList<ChatElement>();

		for (ChatElement elemen : element)
			extra.add(extra.size(), elemen);

		return this;
	}

    public ArrayList<ChatElement> getExtra(){
        return this.extra;
    }

    public boolean hasExtra(){
        return this.extra != null && !this.extra.isEmpty();
    }

	@Override
	public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(this.getText());
        if(this.hasExtra())
            for(ChatElement element: this.extra)
                sb.append(' ').append(element.toString());

        return sb.toString();
	}

	public String toJSON() {
		return gson.toJson(this);
	}

    public static ChatElement fromJSON(String json){
        return gson.fromJson(json, ChatElement.class);
    }

	public static class HoverElement {

		private String action;
		private String value;

		public HoverElement(Action action, String value) {
			this.action = action.toString();
			this.value = value;
		}

		public Action getAction() {
			return Action.from(this.action);
		}

		public String getValue() {
			return this.value;
		}

		public static enum Action {
			SHOW_ACHIEVEMENT("show_achievement"),
			SHOW_ITEM("shop_item"),
			SHOW_TEXT("show_text");

			private String name;

			private Action(String name) {
				this.name = name;
			}

			@Override
			public String toString() {
				return this.name;
			}


			public static Action from(String name) {
				for (Action action : values())
					if (action.toString().equalsIgnoreCase(name)) return action;

				return null;
			}
		}
	}

	public static class ClickElement {

		private String action;
		private String value;

		public ClickElement(Action action, String value) {
			this.action = action.toString();
			this.value = value;
		}

		public Action getAction() {
			return Action.from(this.action);
		}

		public String getValue() {
			return this.value;
		}

		public static enum Action {
			OPEN_URL("open_url"),
			OPEN_FILE("open_file"),
			RUN_COMMAND("run_command"),
			SUGGEST_COMMAND("suggest_command"),
			TWITCH_USER_INFO("twitch_user_info");

			private String name;

			private Action(String name) {
				this.name = name;
			}

			@Override
			public String toString() {
				return this.name;
			}


			public static Action from(String name) {
				for (Action action : values())
					if (action.toString().equalsIgnoreCase(name)) return action;

				return null;
			}
		}
	}

    private static class ChatElementTypeAdapter extends TypeAdapter<ChatElement>{

        @Override
        public void write(JsonWriter out, ChatElement chatElement) throws IOException {
            out.beginObject();
            out.name("text").value(chatElement.getText());
            if(chatElement.getClickEvent() != null){
                out.name("clickEvent");
                gson.toJson(gson.toJsonTree(chatElement.getClickEvent()), out);
            }
            if(chatElement.getHoverEvent() != null){
                out.name("hoverEvent");
                gson.toJson(gson.toJsonTree(chatElement.getHoverEvent()), out);
            }
            if(!chatElement.color.equals(ChatColor.WHITE.getName()))
                out.name("color").value(chatElement.color);
            if(chatElement.bold)
                out.name("bold").value(true);
            if(chatElement.underlined)
                out.name("underlined").value(true);
            if(chatElement.italic)
                out.name("italic").value(true);
            if(chatElement.strikethrough)
                out.name("strikethrough").value(true);
            if(chatElement.obfuscated)
                out.name("obfuscated").value(true);
            if(chatElement.hasExtra()){
                out.name("extra");
                out.beginArray();
                for(ChatElement element: Lists.newArrayList(chatElement.getExtra()))
                    gson.toJson(gson.toJsonTree(element), out);
                out.endArray();
            }
            out.endObject();
        }

        @Override
        public ChatElement read(JsonReader jsonReader) throws IOException {
            return BeaconServer.gson.fromJson(jsonReader, ChatElement.class);
        }
    }

}
