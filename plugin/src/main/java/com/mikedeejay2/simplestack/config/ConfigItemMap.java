package com.mikedeejay2.simplestack.config;

import com.mikedeejay2.mikedeejay2lib.util.item.FastItemMeta;
import it.unimi.dsi.fastutil.objects.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public final class ConfigItemMap {
    private final EnumMap<Material, Integer> material2AmountMap = new EnumMap<>(Material.class);
    private final EnumMap<Material, Object2IntMap<ItemMeta>> material2Meta2AmountMap = new EnumMap<>(Material.class);
    private final EnumMap<Material, ReferenceList<ItemConfigValue>> material2ValueMap = new EnumMap<>(Material.class);
    private final ReferenceList<ItemConfigValue> wildcardValueList = new ReferenceArrayList<>();

    private final ReferenceList<ItemConfigValue> consolidated = new ReferenceArrayList<>();

    private boolean shouldBuildMaps = true;

    public int getMaterial(Material material) {
        return material2AmountMap.getOrDefault(material, -1);
    }

    public int getItemStack(ItemStack itemStack) {
        final Material material = itemStack.getType();
        int result = getMetaAmount(itemStack, material);
        if(result != -1) return result;
        result = getValueAmount(itemStack, material);
        if(result != -1) return result;
        return getWildcardAmount(itemStack);
    }

    private int getMetaAmount(ItemStack itemStack, Material material) {
        if(!itemStack.hasItemMeta() || !material2Meta2AmountMap.containsKey(material)) return -1;
        final ItemMeta meta = FastItemMeta.getItemMeta(itemStack);
        return material2Meta2AmountMap.get(material).getOrDefault(meta, -1);
    }

    private int getValueAmount(ItemStack itemStack, Material material) {
        if(!material2ValueMap.containsKey(material)) return -1;
        for(ItemConfigValue value : material2ValueMap.get(material)) {
            if(value.matchItem(itemStack)) return value.getAmount();
        }
        return -1;
    }

    private int getWildcardAmount(ItemStack itemStack) {
        for(ItemConfigValue value : wildcardValueList) {
            if(value.matchItem(itemStack)) return value.getAmount();
        }
        return -1;
    }

    public void addItem(ItemConfigValue value) {
        consolidated.add(value);
        buildMaps();
    }

    public void removeItem(ItemConfigValue value) {
        consolidated.remove(value);
        buildMaps();
    }

    public boolean containsItem(ItemConfigValue value) {
        return consolidated.contains(value);
    }

    public List<ItemConfigValue> getList() {
        return consolidated;
    }

    public void buildMaps() {
        if(!shouldBuildMaps) return;
        material2AmountMap.clear();
        material2Meta2AmountMap.clear();
        material2ValueMap.clear();
        wildcardValueList.clear();

        for(ItemConfigValue value : consolidated) {
            if(value.getMatchers().size() == 0) continue;
            final int amount = value.getItem().getAmount();
            ItemProperties item = value.getItem();
            if(value.canBeMaterial()) {
                if(material2AmountMap.containsKey(item.getType())) continue;
                material2AmountMap.put(item.getType(), amount);
                continue;
            }
            if(value.canBeMetaMaterial()) {
                if(!material2Meta2AmountMap.containsKey(item.getType())) {
                    material2Meta2AmountMap.put(item.getType(), new Object2IntLinkedOpenHashMap<>());
                }
                material2Meta2AmountMap.get(item.getType()).put(item.getItemMeta(), item.getAmount());
                continue;
            }
            if(value.canBeMaterialValue()) {
                if(!material2ValueMap.containsKey(item.getType())) {
                    material2ValueMap.put(item.getType(), new ReferenceArrayList<>());
                }
                material2ValueMap.get(item.getType()).add(value);
                continue;
            }
            wildcardValueList.add(value);
        }
    }

    public List<Map<String, Object>> serialize() {
        return consolidated.stream()
                .map(ItemConfigValue::serialize)
                .collect(Collectors.toList());
    }

    public static ConfigItemMap deserialize(List<Map<String, Object>> list) {
        final ConfigItemMap configItemMap = new ConfigItemMap();
        if(list == null) return configItemMap;
        configItemMap.shouldBuildMaps = false;
        for(Map<String, Object> cur : list) {
            ItemConfigValue value = ItemConfigValue.deserialize(cur);
            if(value != null) configItemMap.addItem(value);
        }
        configItemMap.shouldBuildMaps = true;
        configItemMap.buildMaps();
        return configItemMap;
    }

    @Override
    public String toString() {
        return "ItemMap{" +
            consolidated +
            '}';
    }
}