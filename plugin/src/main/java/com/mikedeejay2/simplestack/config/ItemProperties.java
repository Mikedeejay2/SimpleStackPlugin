package com.mikedeejay2.simplestack.config;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.Objects;

public final class ItemProperties {
    private final Material type;
    private final int amount;
    private final ItemMeta meta;

    public ItemProperties(ItemStack itemStack) {
        this.type = itemStack.getType();
        this.amount = itemStack.getAmount();
        this.meta = itemStack.hasItemMeta() ? itemStack.getItemMeta() : null;
    }

    private ItemProperties(Material type, int amount, ItemMeta meta) {
        this.type = type;
        this.amount = amount;
        this.meta = meta;
    }

    public ItemStack asItemStack() {
        final ItemStack item = new ItemStack(type, amount);
        item.setItemMeta(meta);
        return item;
    }

    public Material getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public ItemMeta getItemMeta() {
        return meta;
    }

    public boolean hasItemMeta() {
        return meta != null;
    }

    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
            .put("type", type.toString())
            .put("amount", amount)
            .put("meta", meta == null ? "none" : meta)
            .build();
    }

    public static ItemProperties deserialize(Map<String, Object> map) {
        Object metaObj = map.get("meta");
        ItemMeta meta = metaObj instanceof ItemMeta ? (ItemMeta) metaObj : null;

        return new ItemProperties(
            Material.valueOf((String) map.get("type")),
            (int) map.get("amount"),
            meta);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + type.hashCode();
        hash = hash * 31 + amount;
        hash = hash * 31 + (meta != null ? meta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ItemProperties)) return false;
        final ItemProperties other = (ItemProperties) obj;
        if(amount != other.amount) return false;
        if(type != other.type) return false;
        return Objects.equals(meta, other.meta);
    }

    @Override
    public String toString() {
        return "ItemProperties{" +
            "type=" + type +
            ", amount=" + amount +
            ", meta=" + meta +
            '}';
    }
}
