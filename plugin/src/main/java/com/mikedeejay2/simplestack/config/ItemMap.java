package com.mikedeejay2.simplestack.config;

import com.mikedeejay2.mikedeejay2lib.util.item.FastItemMeta;
import it.unimi.dsi.fastutil.objects.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ItemMap {
    private final Reference2IntMap<Material> materialMap = new Reference2IntLinkedOpenHashMap<>();
    private final ReferenceList<ItemProperties> metaList = new ReferenceArrayList<>();
    private final ReferenceList<ItemConfigValue> valueList = new ReferenceArrayList<>();
    private final ReferenceList<ItemConfigValue> consolidated = new ReferenceArrayList<>();

    private boolean shouldBuildMaps = true;

    public int getMaterial(Material material) {
        return materialMap.getOrDefault(material, -1);
    }

    public int getItemStack(ItemStack itemStack) {
        int result = getMetaAmount(itemStack);
        if(result != -1) return result;
        return getValueAmount(itemStack);
    }

    private int getMetaAmount(ItemStack itemStack) {
        for(ItemProperties cur : metaList) {
            if(cur.getType() != itemStack.getType()) continue;
            if(!Objects.equals(cur.getItemMeta(), FastItemMeta.getItemMeta(itemStack))) continue;
            return cur.getAmount();
        }
        return -1;
    }

    private int getValueAmount(ItemStack itemStack) {
        for(ItemConfigValue cur : valueList) {
            if(cur.checkItem(itemStack)) return cur.getAmount();
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

    public List<ItemConfigValue> getList() {
        return consolidated;
    }

    public void buildMaps() {
        if(!shouldBuildMaps) return;
        materialMap.clear();
        metaList.clear();
        valueList.clear();
        for(ItemConfigValue value : consolidated) {
            final int amount = value.getItem().getAmount();
            if(value.canBeMaterial()) {
                materialMap.put(value.asMaterial(), amount);
                continue;
            }
            if(value.canBeMetaMaterial()) {
                metaList.add(value.getItem());
                continue;
            }
            valueList.add(value);
        }
    }

    public List<Map<String, Object>> serialize() {
        return consolidated.stream()
                .map(ItemConfigValue::serialize)
                .collect(Collectors.toList());
    }

    public static ItemMap deserialize(List<Map<String, Object>> list) {
        final ItemMap itemMap = new ItemMap();
        if(list == null) return itemMap;
        itemMap.shouldBuildMaps = false;
        for(Map<String, Object> cur : list) {
            itemMap.addItem(ItemConfigValue.deserialize(cur));
        }
        itemMap.shouldBuildMaps = true;
        itemMap.buildMaps();
        return itemMap;
    }

    @Override
    public String toString() {
        return "ItemMap{" +
            consolidated +
            '}';
    }
}
