package com.mikedeejay2.simplestack.config;

import com.google.common.collect.ImmutableMap;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.mikedeejay2lib.util.item.FastItemMeta;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class ItemConfigValue {
    public static final String DATA_KEY = "item_config_value";

    protected final Set<ItemMatcher> matchers = EnumSet.noneOf(ItemMatcher.class);

    protected final ItemProperties configItem;

    public ItemConfigValue(ItemStack configItem, ItemMatcher... matchers) {
        this.configItem = new ItemProperties(configItem);
        Collections.addAll(this.matchers, matchers);
    }

    public ItemConfigValue(ItemStack configItem, Collection<ItemMatcher> matchers) {
        this.configItem = new ItemProperties(configItem);
        this.matchers.addAll(matchers);
    }

    protected ItemConfigValue(ItemProperties item, Set<ItemMatcher> matchers) {
        this.configItem = item;
        this.matchers.addAll(matchers);
    }

    public boolean matchItem(ItemStack item) {
        if(matchers.size() == 0) return false;
        for(ItemMatcher check : matchers) {
            if(!check.check(item, configItem)) return false;
        }
        return true;
    }

    public ItemBuilder asItemBuilder() {
        ItemBuilder builder = ItemBuilder.of(Base64Head.QUESTION_MARK_CYAN.get())
            .setName(Text.of("&r&f").concat("Any Item").color()) // TODO: Localization
            .addLore("");
        for(ItemMatcher matcher : matchers) {
            builder = matcher.addToItem(builder, configItem);
        }
        if(!matchers.isEmpty()) {
            builder = builder.addLore(Text.of("&7").concat("Matches").concat(": ").color()) // TODO: Localization
                .addLoreText(
                    matchers.stream()
                        .map(ItemMatcher::getNameKey)
                        .map(Text::of)
                        .map(text -> Text.of("&a • ").concat(text).color())
                        .collect(Collectors.toList()));
        } else {
            builder = builder.addLore(Text.of("&c⚠ ").concat("No matchers on this item").color()); // TODO: Localization
        }
        return builder;
    }

    public ItemConfigValue addMatcher(ItemMatcher matcher) {
        matchers.add(matcher);
        return this;
    }

    public ItemConfigValue removeMatcher(ItemMatcher matcher) {
        matchers.remove(matcher);
        return this;
    }

    public ItemConfigValue setMatchers(Collection<ItemMatcher> matchers) {
        this.matchers.clear();
        this.matchers.addAll(matchers);
        return this;
    }

    public boolean containsMatch(ItemMatcher matcher) {
        return matchers.contains(matcher);
    }

    public boolean canBeMetaMaterial() {
        return matchers.size() == 2 && containsMatch(ItemMatcher.MATERIAL) && containsMatch(ItemMatcher.ITEM_META);
    }

    public boolean canBeMaterial() {
        return matchers.size() == 1 && matchers.contains(ItemMatcher.MATERIAL);
    }

    public boolean canBeMaterialValue() {
        return matchers.contains(ItemMatcher.MATERIAL);
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

    public Set<ItemMatcher> getMatchers() {
        return matchers;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + matchers.hashCode();
        hash = hash * 31 + configItem.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ItemConfigValue)) return false;
        final ItemConfigValue other = (ItemConfigValue) obj;
        if(!matchers.equals(other.matchers)) return false;
        return configItem.equals(other.configItem);
    }

    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
            .put("item", configItem.serialize())
            .put("matchers", matchers.stream()
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
            "matchers=" + matchers +
            ", configItem=" + configItem +
            '}';
    }

    public enum ItemMatcher {
        MATERIAL("simplestack.config.item_checks.material") {
            @Override
            public boolean check(ItemStack item, ItemProperties configItem) {
                return item.getType() == configItem.getType();
            }

            @Override
            public ItemBuilder addToItem(ItemBuilder builder, ItemProperties properties) {
                return builder.setType(properties.getType()).setName("");
            }
        },
        ITEM_META("simplestack.config.item_checks.item_meta") {
            @Override
            public boolean check(ItemStack item, ItemProperties configItem) {
                return Objects.equals(configItem.getItemMeta(), FastItemMeta.getItemMeta(item));
            }

            @Override
            public ItemBuilder addToItem(ItemBuilder builder, ItemProperties properties) {
                return properties.hasItemMeta() ? builder.setMeta(properties.getItemMeta()) : builder;
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

        public abstract ItemBuilder addToItem(ItemBuilder builder, ItemProperties properties);
    }
}
