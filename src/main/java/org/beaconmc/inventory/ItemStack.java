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
 package org.beaconmc.inventory;

import org.beaconmc.material.Material;
import org.spout.nbt.*;

import java.util.ArrayList;
import java.util.List;

public class ItemStack {

    private Material material;
    private int amount;
    private short damage;

    private int slot;
    private CompoundMap tag;

    public ItemStack(Material material, int amount, short damage){
        if(material == null)
            throw new IllegalArgumentException("Material cannot be null");
        this.material = material;
        this.amount = amount;
        this.damage = damage;
    }

    public ItemStack(String material, int amount){
        this(Material.byID(material), amount, (short)0);
    }

    public ItemStack(String material, int amount, short damage){
        this(Material.byID(material), amount, damage);
    }

    public ItemStack(Material material) {
        this(material, 1, (short)0);
    }

    public Material getMaterial(){
        return this.material;
    }

    public int getAmount(){
        return this.amount;
    }

    public void setAmount(int amount){
        this.amount = amount;
    }

    public short getDamage(){
        return this.damage;
    }

    public void setDamage(short damage){
        this.damage = damage;
    }

    public boolean hasTag(){
        return this.tag != null;
    }

    public CompoundMap getTag(){
        return this.hasTag() ? this.tag : (this.tag = new CompoundMap());
    }

    public void setTag(CompoundMap map){
        this.tag = map;
    }

    public void setTag(CompoundTag tag){
        this.tag = (tag == null) ? null : tag.getValue();
    }

    public boolean isUnbreakable(){
        return this.hasTag() && this.getTag().containsKey("Unbreakable") && ((ByteTag)this.getTag().get("Unbreakable")).getBooleanValue();
    }

    public void setUnbreakable(boolean unbreakable){
        this.getTag().put(new ByteTag("Unbreakable", unbreakable));
    }

    public List<StringTag> getCanDestroy(){
        return this.hasTag() && this.getTag().containsKey("CanDestroy") ? ((ListTag<StringTag>)this.getTag().get("CanDestroy")).getValue() : new ListTag<StringTag>("CanDestroy", StringTag.class, new ArrayList<StringTag>()).getValue();
    }

    public void setCanDestroy(List<StringTag> canDestroy){
        this.getTag().put(new ListTag<StringTag>("CanDetroy", StringTag.class, canDestroy));
    }

    public void addCanDestroy(ItemStack itemStack){
        List<StringTag> candestroy = this.getCanDestroy();
        candestroy.add(new StringTag(null, itemStack.getMaterial().getId()));
        this.setCanDestroy(candestroy);
    }

    public boolean hasDisplayTag(){
        return this.hasTag() && this.getTag().containsKey("display");
    }

    public CompoundMap getDisplayTag(){
        CompoundTag displayTag = (CompoundTag)this.getTag().get("display");
        if(displayTag == null){
            displayTag = new CompoundTag("display", new CompoundMap());
            this.getTag().put(displayTag);
        }
        return displayTag.getValue();
    }

    public int getSlot(){
        return this.slot;
    }

    public void setSlot(int slot){
        this.slot = slot;
    }

    public boolean hasDisplayName(){
        return this.hasDisplayTag() && this.getDisplayTag().containsKey("Name");
    }

    public String getDisplayName(){
        return ((StringTag)this.getDisplayTag().get("Name")).getValue();
    }

    public void setDisplayName(String displayName){
        this.getDisplayTag().put(new StringTag("Name", displayName));
    }

    public boolean hasLore(){
        return this.hasDisplayTag() && this.getDisplayTag().containsKey("Lore");
    }

    public List<StringTag> getLore(){
        return ((ListTag<StringTag>)this.getDisplayTag().get("Lore")).getValue();
    }

    public void setLore(List<StringTag> lore){
        this.getDisplayTag().put(new ListTag<StringTag>("Lore", StringTag.class, lore));
    }

    public boolean hasLeatherColor(){
        return this.hasDisplayTag() && this.getDisplayTag().containsKey("color");
    }

    public int getLeatherColor(){
        return ((IntTag)this.getDisplayTag().get("color")).getValue();
    }

    public void setLeatherColor(int color){// rot << 16 + grün << 8 + blau
        this.getDisplayTag().put(new IntTag("color", color));
    }

    public boolean hasHideFlags(){
        return this.hasTag() && this.getTag().containsKey("HideFlags");
    }

    public int getHideFlags(){
        return ((IntTag)this.getTag().get("HideFlags")).getValue();
    }

    public void setHideFlags(int hideFlags){
        this.getTag().put(new IntTag("HideFlags", hideFlags));
    }

    public void save(CompoundMap map){
        map.put(new ByteTag("Count", (byte)this.amount));
        map.put(new ByteTag("Slot", (byte)this.slot));
        map.put(new ShortTag("Damage", this.damage));
        map.put(new StringTag("id", this.material.getId()));
        if(this.hasTag())
            map.put(new CompoundTag("tag", this.getTag()));
    }

    public ItemStack load(CompoundMap map){
        this.amount = ((ByteTag)map.get("Count")).getValue();
        this.slot = ((ByteTag)map.get("Slot")).getValue();
        this.damage = ((ShortTag)map.get("Damage")).getValue();
        this.material = Material.byID(((StringTag)map.get("id")).getValue());
        if(map.containsKey("tag"))
            this.tag = ((CompoundTag)map.get("tag")).getValue();

        return this;
    }

    public static class EquipmentSlot{
        public final static short HAND = 0;
        public final static short BOOTS = 1;
        public final static short LEGGINGS = 2;
        public final static short CHESTPLATE = 3;
        public final static short HELMET = 4;
    }


}
