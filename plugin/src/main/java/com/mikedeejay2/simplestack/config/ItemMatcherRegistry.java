package com.mikedeejay2.simplestack.config;

import com.mikedeejay2.simplestack.api.ItemMatcher;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ItemMatcherRegistry {
    public static final Map<ItemMatcher, ItemMatcherImpl> ALL_MATCHERS = new LinkedHashMap<>();

    public static final ItemMatcherImpl MATERIAL_MATCHER = new ItemMatcherImpl.Dynamic(ItemMatcher.MATERIAL)
        .setNameKey("simplestack.config.item_matchers.material")
        .setAddToItem((builder, properties) -> builder.setType(properties.getType()).setName(""))
        .setCheck((material, itemMeta, properties) -> properties.getType() == material);

    public static final ItemMatcherImpl ITEM_META = new ItemMatcherImpl.Dynamic(ItemMatcher.ITEM_META)
        .setNameKey("simplestack.config.item_matchers.item_meta")
        .setAddToItem((builder, properties) -> properties.hasItemMeta() ? builder.setMeta(properties.getItemMeta()) : builder)
        .setCheck((material, itemMeta, properties) -> Bukkit.getItemFactory().equals(itemMeta, properties.getItemMeta()))
        .setIncompatible(configValue -> configValue.getMatchersMap().values().stream().anyMatch(cur -> cur instanceof ItemMatcherImpl.Meta));

    private ItemMatcherRegistry() {
        throw new UnsupportedOperationException("ItemMatcherRegistry cannot be instantiated as an object.");
    }
}
