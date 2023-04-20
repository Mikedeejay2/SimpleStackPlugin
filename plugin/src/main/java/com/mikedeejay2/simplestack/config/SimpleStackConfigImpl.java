package com.mikedeejay2.simplestack.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mikedeejay2.mikedeejay2lib.BukkitPlugin;
import com.mikedeejay2.mikedeejay2lib.config.ConfigFile;
import com.mikedeejay2.mikedeejay2lib.data.FileType;
import com.mikedeejay2.mikedeejay2lib.text.language.TranslationManager;
import com.mikedeejay2.mikedeejay2lib.util.debug.CrashReportSection;
import com.mikedeejay2.mikedeejay2lib.util.structure.list.MapAsList;
import com.mikedeejay2.mikedeejay2lib.util.structure.list.SetAsList;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackConfig;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.mikedeejay2.simplestack.config.SimpleStackConfigTypes.*;

/**
 * Config class for holding all configuration values for Simple Stack and
 * managing file saving / loading.
 *
 * @author Mikedeejay2
 */
public class SimpleStackConfigImpl extends ConfigFile implements SimpleStackConfig {
    private final UniqueItemFile uniqueItemFile = child(new UniqueItemFile(plugin));

    //Variables
    // List mode of the material list. Either Blacklist of Whitelist.
    private final ConfigValue<Boolean> whitelist = value(WHITELIST_TYPE, "List Mode");
    // Material list of the config (Item Type list in config)
    private final ConfigValue<ReferenceSet<Material>> materialSet = collectionValue(MATERIAL_LIST_TYPE, "Item Types", new ReferenceLinkedOpenHashSet<>());
    // Localization code specified in the config
    private final ConfigValue<String> locale = value(LOCALE_TYPE, "Language");
    // Item amounts based on the item's material (Item Type amounts list in config)
    private final ConfigValue<Reference2IntMap<Material>> itemAmountMap = mapValue(ITEM_AMOUNTS_TYPE, "Item Amounts", new Reference2IntLinkedOpenHashMap<>());
    // Unique items list from the unique_items.json
    private final ConfigValue<Object2IntMap<ItemStack>> uniqueItemMap = uniqueItemFile.uniqueItemMap;
    // The max amount for all items in minecraft
    private final ConfigValue<Integer> maxAmount = value(MAX_AMOUNT_TYPE, "Default Max Amount");
    // Whether stacked armor can be worn or not
    private final ConfigValue<Boolean> stackedArmorWearable = value(ValueType.BOOLEAN, "Stacked Armor Wearable");

    public SimpleStackConfigImpl(SimpleStack plugin) {
        super(plugin, "config.yml", FileType.YAML, true);

        // Remove or relocate legacy config values
        updater.relocate("Items", "Item Types")
            .relocate("ListMode", "List Mode")
            .remove("Hopper Movement Checks")
            .remove("Ground Stacking Checks")
            .remove("Creative Item Dragging");
    }

    @Override
    protected boolean internalLoadFromJar() {
        boolean success = super.internalLoadFromJar();
        if(success) setLocale(TranslationManager.SYSTEM_LOCALE);
        return success;
    }

    public List<Material> getMaterialsRef() {
        return new SetAsList<>(materialSet.get());
    }

    public List<Map.Entry<Material, Integer>> getItemAmountsRef() {
        return new MapAsList<>(itemAmountMap.get());
    }

    public List<Map.Entry<ItemStack, Integer>> getUniqueItemsRef() {
        return new MapAsList<>(uniqueItemMap.get());
    }

    @Override
    public int getAmount(@NotNull ItemStack item) {
        return getUniqueItemAmount(item);
    }

    @Override
    public int getAmount(@NotNull Material type) {
        final int customAmount = itemAmountMap.get().getInt(type);
        if(customAmount != 0) return customAmount;
        if(isWhitelist() == containsMaterial(type)) {
            return getMaxAmount();
        }
        return -1;
    }

    @Override
    public int getUniqueItemAmount(@NotNull ItemStack item) {
        final int amount = uniqueItemMap.get().getInt(item);
        return amount > 0 ? amount : -1;
    }

    @Override
    public boolean containsMaterial(@NotNull Material material) {
        return materialSet.get().contains(material);
    }

    @Override
    public boolean containsCustomAmount(@NotNull Material material) {
        return itemAmountMap.get().containsKey(material);
    }

    @Override
    public boolean containsUniqueItem(@NotNull ItemStack item) {
        return uniqueItemMap.get().containsKey(item);
    }

    @Override
    public @NotNull Set<Material> getMaterials() {
        return ImmutableSet.copyOf(materialSet.get());
    }

    @Override
    public @NotNull Map<Material, Integer> getItemAmounts() {
        return ImmutableMap.copyOf(itemAmountMap.get());
    }

    @Override
    public @NotNull Set<ItemStack> getUniqueItems() {
        return ImmutableSet.copyOf(uniqueItemMap.get().keySet());
    }

    @Override
    public void addMaterial(@NotNull Material material) {
        if(containsMaterial(material)) return;
        materialSet.get().add(material);
        setModified(true);
    }

    @Override
    public void addCustomAmount(@NotNull Material material, int amount) {
        itemAmountMap.get().put(material, amount);
        setModified(true);
    }

    @Override
    public void addUniqueItem(@NotNull ItemStack item) {
        uniqueItemMap.get().put(item, item.getAmount());
        setModified(true);
    }

    @Override
    public void removeMaterial(@NotNull Material material) {
        materialSet.get().remove(material);
        setModified(true);
    }

    @Override
    public void removeCustomAmount(@NotNull Material material) {
        itemAmountMap.get().removeInt(material);
        setModified(true);
    }

    @Override
    public void removeUniqueItem(@NotNull ItemStack item) {
        uniqueItemMap.get().remove(item);
    }

    @Override
    public boolean isWhitelist() {
        return whitelist.get();
    }

    @Override
    public void setListMode(boolean whitelist) {
        this.whitelist.set(whitelist);
        setModified(true);
    }

    @Override
    public boolean isStackedArmorWearable() {
        return stackedArmorWearable.get();
    }

    @Override
    public void setStackedArmorWearable(boolean stackedArmorWearable) {
        this.stackedArmorWearable.set(stackedArmorWearable);
        setModified(true);
    }

    @Override
    public int getMaxAmount() {
        return maxAmount.get();
    }

    @Override
    public void setMaxAmount(int maxAmount) {
        this.maxAmount.set(maxAmount);
        setModified(true);
    }

    @Override
    public @NotNull String getLocale() {
        return locale.get();
    }

    @Override
    public void setLocale(@NotNull String newLocale) {
        this.locale.set(newLocale);
        TranslationManager.GLOBAL.setGlobalLocale(newLocale);
        setModified(true);
    }

    public void fillCrashReportSection(CrashReportSection section) {
        fillCrashReportSection_(section, this);
        for(ConfigFile child : children) {
            fillCrashReportSection_(section, child);
        }
        section.addDetail("Is Config Loaded", String.valueOf(isLoaded()));
        section.addDetail("Is Config Modified", String.valueOf(isModified()));
    }

    private void fillCrashReportSection_(CrashReportSection section, ConfigFile file) {
        for(ConfigValue<?> value : file.getValues()) {
            section.addDetail(value.getPath(), String.valueOf(value.get()));
        }
    }

    private static final class UniqueItemFile extends ConfigFile {
        private final ConfigValue<Object2IntMap<ItemStack>> uniqueItemMap =
            mapValue(UNIQUE_ITEM_LIST_TYPE, "items", new Object2IntLinkedOpenCustomHashMap<>(new UniqueStrategy()));

        public UniqueItemFile(BukkitPlugin plugin) {
            super(plugin, "unique_items.yml", FileType.YAML, false);
            updater.convert("unique_items.json", "unique_items.yml", (oldAccessor, newAccessor) -> {
                newAccessor.setItemStackList("items", oldAccessor.getItemStackList("items"));
            }).rename("unique_items.json", "unique_items_old.json");
        }

        private static final class UniqueStrategy implements Hash.Strategy<ItemStack> {
            @Override
            public int hashCode(ItemStack o) {
                int hash = 1;

                hash = hash * 31 + o.getType().hashCode();
                hash = hash * 31 + 1; // Don't include amount
                hash = hash * 31; // Don't include durability
                hash = hash * 31 + (o.hasItemMeta() ? o.getItemMeta().hashCode() : 0);

                return hash;
            }

            @Override
            public boolean equals(ItemStack a, ItemStack b) {
                if(a == b) return true;
                if(a == null || b == null) return false;
                if(a.getType() != b.getType()) return false;
                return a.getItemMeta().equals(b.getItemMeta());
            }
        }
    }
}
