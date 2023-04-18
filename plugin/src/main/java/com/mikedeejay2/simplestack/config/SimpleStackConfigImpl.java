package com.mikedeejay2.simplestack.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mikedeejay2.mikedeejay2lib.BukkitPlugin;
import com.mikedeejay2.mikedeejay2lib.config.ConfigFile;
import com.mikedeejay2.mikedeejay2lib.text.language.TranslationManager;
import com.mikedeejay2.mikedeejay2lib.util.debug.CrashReportSection;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackConfig;
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
    private final ConfigValue<List<Material>> materialList = collectionValue(MATERIAL_LIST_TYPE, "Item Types", new ArrayList<>());
    // Localization code specified in the config
    private final ConfigValue<String> locale = value(LOCALE_TYPE, "Language");
    // Item amounts based on the item's material (Item Type amounts list in config)
    private final ConfigValue<List<MaterialAndAmount>> itemAmounts = collectionValue(ITEM_AMOUNTS_TYPE, "Item Amounts", new ArrayList<>());
    // Unique items list from the unique_items.json
    private final ConfigValue<List<ItemStack>> uniqueItemList = uniqueItemFile.uniqueItemList;
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
        return materialList.get();
    }

    public List<MaterialAndAmount> getItemAmountsRef() {
        return itemAmounts.get();
    }

    public List<ItemStack> getUniqueItemsRef() {
        return uniqueItemList.get();
    }

    @Override
    public int getAmount(@NotNull ItemStack item) {
        return getUniqueItemAmount(item);
    }

    @Override
    public int getAmount(@NotNull Material type) {
        if(containsCustomAmount(type)) {
            return itemAmounts.get().get(itemAmounts.get().indexOf(new MaterialAndAmount(type, 0))).getAmount();
        }
        if(isWhitelist() == containsMaterial(type)) {
            return getMaxAmount();
        }
        return -1;
    }

    @Override
    public int getUniqueItemAmount(@NotNull ItemStack item) {
        for(ItemStack curItem : uniqueItemList.get()) {
            if(!ItemComparison.equalsEachOther(curItem, item)) continue;
            return curItem.getAmount();
        }
        return -1;
    }

    @Override
    public boolean containsMaterial(@NotNull Material material) {
        return materialList.get().contains(material);
    }

    @Override
    public boolean containsCustomAmount(@NotNull Material material) {
        return itemAmounts.get().contains(new MaterialAndAmount(material, 0));
    }

    @Override
    public boolean containsUniqueItem(@NotNull ItemStack item) {
        for(ItemStack curItem : uniqueItemList.get()) {
            if(!ItemComparison.equalsEachOther(curItem, item)) continue;
            return true;
        }
        return false;
    }

    @Override
    public @NotNull Set<Material> getMaterials() {
        return ImmutableSet.copyOf(materialList.get());
    }

    @Override
    public @NotNull Map<Material, Integer> getItemAmounts() {
        ImmutableMap.Builder<Material, Integer> builder = ImmutableMap.builder();
        for(MaterialAndAmount entry : itemAmounts.get()) {
            builder.put(entry.getMaterial(), entry.getAmount());
        }
        return builder.build();
    }

    @Override
    public @NotNull Set<ItemStack> getUniqueItems() {
        return ImmutableSet.copyOf(uniqueItemList.get());
    }

    @Override
    public void addMaterial(@NotNull Material material) {
        if(containsMaterial(material)) return;
        materialList.get().add(material);
        setModified(true);
    }

    @Override
    public void addCustomAmount(@NotNull Material material, int amount) {
        if(containsCustomAmount(material)) removeCustomAmount(material);
        itemAmounts.get().add(new MaterialAndAmount(material, amount));
        setModified(true);
    }

    @Override
    public void addUniqueItem(@NotNull ItemStack item) {
        removeUniqueItem(item);
        uniqueItemList.get().add(item);
        setModified(true);
    }

    @Override
    public void removeMaterial(@NotNull Material material) {
        materialList.get().remove(material);
        setModified(true);
    }

    @Override
    public void removeCustomAmount(@NotNull Material material) {
        itemAmounts.get().remove(new MaterialAndAmount(material, 0));
        setModified(true);
    }

    @Override
    public void removeUniqueItem(@NotNull ItemStack item) {
        for(ItemStack curItem : uniqueItemList.get()) {
            if(!ItemComparison.equalsEachOther(item, curItem)) continue;
            uniqueItemList.get().remove(curItem);
            setModified(true);
            break;
        }
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

    public static final class MaterialAndAmount {
        private final Material material;
        private final int amount;

        public MaterialAndAmount(Material material, int amount) {
            this.material = material;
            this.amount = amount;
        }

        public Material getMaterial() {
            return material;
        }

        public int getAmount() {
            return amount;
        }

        @Override
        public int hashCode() {
            return material.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            Object material = obj;
            if(obj.getClass() == MaterialAndAmount.class) {
                material = ((MaterialAndAmount) obj).getMaterial();
            }
            return this.material == material;
        }
    }

    private static final class UniqueItemFile extends ConfigFile {
        private final ConfigValue<List<ItemStack>> uniqueItemList = collectionValue(UNIQUE_ITEM_LIST_TYPE, "items", new ArrayList<>());

        public UniqueItemFile(BukkitPlugin plugin) {
            super(plugin, "unique_items.yml", FileType.YAML, false);
            updater.convert("unique_items.json", "unique_items.yml", (oldAccessor, newAccessor) -> {
                newAccessor.setItemStackList("items", oldAccessor.getItemStackList("items"));
            }).rename("unique_items.json", "unique_items_old.json");
        }
    }
}
