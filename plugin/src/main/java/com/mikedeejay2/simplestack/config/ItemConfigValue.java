package com.mikedeejay2.simplestack.config;

import com.google.common.collect.ImmutableMap;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.item.FastItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class ItemConfigValue {
    public static final String DATA_KEY = "item_config_value";

    protected final Set<ItemMatcher> matches = EnumSet.noneOf(ItemMatcher.class);

    protected final ItemProperties configItem;

    public ItemConfigValue(ItemStack configItem, ItemMatcher... matches) {
        this.configItem = new ItemProperties(configItem);
        Collections.addAll(this.matches, matches);
    }

    public ItemConfigValue(ItemStack configItem, Collection<ItemMatcher> matches) {
        this.configItem = new ItemProperties(configItem);
        this.matches.addAll(matches);
    }

    protected ItemConfigValue(ItemProperties item, Set<ItemMatcher> matches) {
        this.configItem = item;
        this.matches.addAll(matches);
    }

    public boolean matchItem(ItemStack item) {
        if(matches.size() == 0) return false;
        for(ItemMatcher check : matches) {
            if(!check.check(item, configItem)) return false;
        }
        return true;
    }

    public ItemBuilder asItemBuilder() {
        return ItemBuilder.of(configItem.asItemStack())
            .addLore("")
            .addLore(Text.of("&7Matches: ").color())
            .addLoreText(
                matches.stream()
                    .map(ItemMatcher::getNameKey)
                    .map(Text::of)
                    .map(text -> Text.of("&a • ").concat(text).color())
                    .collect(Collectors.toList()));
    }

    public ItemConfigValue addMatcher(ItemMatcher matcher) {
        matches.add(matcher);
        return this;
    }

    public ItemConfigValue removeMatcher(ItemMatcher matcher) {
        matches.remove(matcher);
        return this;
    }

    public ItemConfigValue setMatches(Collection<ItemMatcher> matches) {
        this.matches.clear();
        this.matches.addAll(matches);
        return this;
    }

    public boolean canBeMetaMaterial() {
        return matches.size() == 2 && matches.contains(ItemMatcher.MATERIAL) && matches.contains(ItemMatcher.ITEM_META);
    }

    public boolean canBeMaterial() {
        return matches.size() == 1 && matches.contains(ItemMatcher.MATERIAL);
    }

    public boolean canBeMaterialValue() {
        return matches.contains(ItemMatcher.MATERIAL);
    }

    public Material asMaterial() {
        return configItem.getType();
    }

    public ItemProperties getItem() {
        return configItem;
    }

    public int getAmount() {
        return configItem.getAmount();
    }

    public Set<ItemMatcher> getMatches() {
        return matches;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + matches.hashCode();
        hash = hash * 31 + configItem.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ItemConfigValue)) return false;
        final ItemConfigValue other = (ItemConfigValue) obj;
        if(!matches.equals(other.matches)) return false;
        return configItem.equals(other.configItem);
    }

    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
            .put("item", configItem.serialize())
            .put("matchers", matches.stream()
                .map(Enum::toString)
                .collect(Collectors.toList()))
            .build();
    }

    public static ItemConfigValue deserialize(Map<String, Object> map) {
        if(!map.containsKey("item")) return null;
        if(!map.containsKey("matchers")) return null;
        return new ItemConfigValue(
            ItemProperties.deserialize((Map<String, Object>) map.get("item")),
            ((List<String>) map.get("matchers")).stream()
                .map(ItemMatcher::valueOf)
                .collect(Collectors.toSet()));
    }

    @Override
    public String toString() {
        return "ItemConfigValue{" +
            "checks=" + matches +
            ", configItem=" + configItem +
            '}';
    }

    public enum ItemMatcher {
        MATERIAL("simplestack.config.item_checks.material") {
            @Override
            public boolean check(ItemStack item, ItemProperties configItem) {
                return item.getType() == configItem.getType();
            }
        },
        ITEM_META("simplestack.config.item_checks.item_meta") {
            @Override
            public boolean check(ItemStack item, ItemProperties configItem) {
                return Objects.equals(configItem.getItemMeta(), FastItemMeta.getItemMeta(item));
            }
        };

        private final String nameKey;

        ItemMatcher(String nameKey) {
            this.nameKey = nameKey;
        }

        public String getNameKey() {
            return nameKey;
        }

        public abstract boolean check(ItemStack item, ItemProperties configItem);
    }
}
