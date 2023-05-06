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
    protected final Set<ItemCheck> checks = EnumSet.noneOf(ItemCheck.class);

    protected final ItemProperties configItem;

    public ItemConfigValue(ItemStack configItem, ItemCheck... checks) {
        this.configItem = new ItemProperties(configItem);
        Collections.addAll(this.checks, checks);
    }

    public ItemConfigValue(ItemStack configItem, Collection<ItemCheck> checks) {
        this.configItem = new ItemProperties(configItem);
        this.checks.addAll(checks);
    }

    protected ItemConfigValue(ItemProperties item, Set<ItemCheck> checks) {
        this.configItem = item;
        this.checks.addAll(checks);
    }

    public boolean checkItem(ItemStack item) {
        for(ItemCheck check : checks) {
            if(!check.check(item, configItem)) return false;
        }
        return true;
    }

    public ItemBuilder asItemBuilder() {
        return ItemBuilder.of(configItem.asItemStack()).addLoreText(
            checks.stream()
                .map(ItemConfigValue.ItemCheck::getNameKey)
                .map(Text::of)
                .map(text -> Text.of("&a • ").concat(text).color())
                .collect(Collectors.toList()));
    }

    public ItemConfigValue addCheck(ItemCheck check) {
        checks.add(check);
        return this;
    }

    public ItemConfigValue removeCheck(ItemCheck check) {
        checks.remove(check);
        return this;
    }

    public ItemConfigValue setChecks(Collection<ItemCheck> checks) {
        this.checks.clear();
        this.checks.addAll(checks);
        return this;
    }

    public boolean canBeMetaMaterial() {
        return checks.size() == 2 && checks.contains(ItemCheck.MATERIAL) && checks.contains(ItemCheck.ITEM_META);
    }

    public boolean canBeMaterial() {
        return checks.size() == 1 && checks.contains(ItemCheck.MATERIAL);
    }

    public boolean canBeMaterialValue() {
        return checks.contains(ItemCheck.MATERIAL);
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

    public Set<ItemCheck> getChecks() {
        return checks;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + checks.hashCode();
        hash = hash * 31 + configItem.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ItemConfigValue)) return false;
        final ItemConfigValue other = (ItemConfigValue) obj;
        if(!checks.equals(other.checks)) return false;
        return configItem.equals(other.configItem);
    }

    public Map<String, Object> serialize() {
        return ImmutableMap.<String, Object>builder()
            .put("item", configItem.serialize())
            .put("checks", checks.stream()
                .map(Enum::toString)
                .collect(Collectors.toList()))
            .build();
    }

    public static ItemConfigValue deserialize(Map<String, Object> map) {
        return new ItemConfigValue(
            ItemProperties.deserialize((Map<String, Object>) map.get("item")),
            ((List<String>) map.get("checks")).stream()
                .map(ItemCheck::valueOf)
                .collect(Collectors.toSet()));
    }

    @Override
    public String toString() {
        return "ItemConfigValue{" +
            "checks=" + checks +
            ", configItem=" + configItem +
            '}';
    }

    public enum ItemCheck {
        MATERIAL("simplestack.config.item_checks.material") {
            @Override
            public boolean check(ItemStack item, ItemProperties configItem) {
                return item.getType() == configItem.getType();
            }
        },
        ITEM_META("simplestack.config.item_checks.item_meta") {
            @Override
            public boolean check(ItemStack item, ItemProperties configItem) {
                return configItem.getItemMeta().equals(FastItemMeta.getItemMeta(item));
            }
        };

        private final String nameKey;

        ItemCheck(String nameKey) {
            this.nameKey = nameKey;
        }

        public String getNameKey() {
            return nameKey;
        }

        public abstract boolean check(ItemStack item, ItemProperties configItem);
    }
}
