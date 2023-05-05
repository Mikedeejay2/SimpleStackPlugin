package com.mikedeejay2.simplestack.config;

import com.mikedeejay2.mikedeejay2lib.BukkitPlugin;
import com.mikedeejay2.mikedeejay2lib.config.ConfigFile;
import com.mikedeejay2.mikedeejay2lib.data.FileType;
import com.mikedeejay2.mikedeejay2lib.text.language.TranslationManager;
import com.mikedeejay2.mikedeejay2lib.util.debug.CrashReportSection;
import com.mikedeejay2.mikedeejay2lib.util.structure.list.SetAsList;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.mikedeejay2.simplestack.config.SimpleStackConfigTypes.*;

/**
 * Config class for holding all configuration values for Simple Stack and
 * managing file saving / loading.
 *
 * @author Mikedeejay2
 */
public class SimpleStackConfigImpl extends ConfigFile implements SimpleStackConfig {
    private final ItemsFile itemsFile = child(new ItemsFile(plugin));

//    //Variables
//    // List mode of the material list. Either Blacklist of Whitelist.
//    private final ConfigValueBoolean whitelist = valueBoolean(WHITELIST_TYPE, "List Mode");
//    // Material list of the config (Item Type list in config)
//    private final ConfigValue<ReferenceSet<Material>> materialSet = collectionValue(MATERIAL_LIST_TYPE, "Item Types", new ReferenceLinkedOpenHashSet<>());
    // Localization code specified in the config
    private final ConfigValue<String> locale = value(LOCALE_TYPE, "Language");
//    // Item amounts based on the item's material (Item Type amounts list in config)
//    private final ConfigValue<Reference2IntMap<Material>> itemAmountMap = mapValue(ITEM_AMOUNTS_TYPE, "Item Amounts", new Reference2IntLinkedOpenHashMap<>());
//    // Unique items list from the unique_items.json
//    private final ConfigValue<Object2IntMap<ItemStack>> uniqueItemMap = uniqueItemFile.uniqueItemMap;
    // The max amount for all items in minecraft
    private final ConfigValueInteger maxAmount = valueInteger(MAX_AMOUNT_TYPE, "Default Max Amount");
    // Whether stacked armor can be worn or not
    private final ConfigValueBoolean stackedArmorWearable = valueBoolean(ValueType.BOOLEAN, "Stacked Armor Wearable");

    private final ConfigValue<ItemMap> itemMap = itemsFile.itemMap;

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

//    public List<Material> getMaterialsRef() {
//        return new SetAsList<>(materialSet.get());
//    }
//
//    public List<Map.Entry<Material, Integer>> getItemAmountsRef() {
//        return new MapAsList<>(itemAmountMap.get());
//    }
//
//    public List<Map.Entry<ItemStack, Integer>> getUniqueItemsRef() {
//        return new MapAsList<>(uniqueItemMap.get());
//    }

    public List<ItemConfigValue> getItemsRef() {
        return itemMap.get().getList();
    }

    @Override
    public int getAmount(@NotNull ItemStack item) {
        return itemMap.get().getItemStack(item);
    }

    @Override
    public int getAmount(@NotNull Material type) {
        return itemMap.get().getMaterial(type);
    }

//    @Override
//    public int getUniqueItemAmount(@NotNull ItemStack item) {
//        final int amount = uniqueItemMap.get().getInt(item);
//        return amount > 0 ? amount : -1;
//    }
//
//    @Override
//    public boolean containsMaterial(@NotNull Material material) {
//        return materialSet.get().contains(material);
//    }
//
//    @Override
//    public boolean containsCustomAmount(@NotNull Material material) {
//        return itemAmountMap.get().containsKey(material);
//    }
//
//    @Override
//    public boolean containsUniqueItem(@NotNull ItemStack item) {
//        return uniqueItemMap.get().containsKey(item);
//    }
//
//    @Override
//    public @NotNull Set<Material> getMaterials() {
//        return ImmutableSet.copyOf(materialSet.get());
//    }
//
//    @Override
//    public @NotNull Map<Material, Integer> getItemAmounts() {
//        return ImmutableMap.copyOf(itemAmountMap.get());
//    }
//
//    @Override
//    public @NotNull Set<ItemStack> getUniqueItems() {
//        return ImmutableSet.copyOf(uniqueItemMap.get().keySet());
//    }
//
//    @Override
//    public void addMaterial(@NotNull Material material) {
//        if(containsMaterial(material)) return;
//        materialSet.get().add(material);
//        setModified(true);
//    }
//
//    @Override
//    public void addCustomAmount(@NotNull Material material, int amount) {
//        itemAmountMap.get().put(material, amount);
//        setModified(true);
//    }
//
//    @Override
//    public void addUniqueItem(@NotNull ItemStack item) {
//        uniqueItemMap.get().put(item, item.getAmount());
//        setModified(true);
//    }
//
//    @Override
//    public void removeMaterial(@NotNull Material material) {
//        materialSet.get().remove(material);
//        setModified(true);
//    }
//
//    @Override
//    public void removeCustomAmount(@NotNull Material material) {
//        itemAmountMap.get().removeInt(material);
//        setModified(true);
//    }
//
//    @Override
//    public void removeUniqueItem(@NotNull ItemStack item) {
//        uniqueItemMap.get().removeInt(item);
//    }
//
//    @Override
//    public boolean isWhitelist() {
//        return whitelist.getBoolean();
//    }
//
//    @Override
//    public void setListMode(boolean whitelist) {
//        this.whitelist.setBoolean(whitelist);
//        setModified(true);
//    }

    @Override
    public boolean isStackedArmorWearable() {
        return stackedArmorWearable.getBoolean();
    }

    @Override
    public void setStackedArmorWearable(boolean stackedArmorWearable) {
        this.stackedArmorWearable.setBoolean(stackedArmorWearable);
        setModified(true);
    }

    @Override
    public int getMaxAmount() {
        return maxAmount.getInteger();
    }

    @Override
    public void setMaxAmount(int maxAmount) {
        this.maxAmount.setInteger(maxAmount);
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

    private static final class ItemsFile extends ConfigFile {
        private final ConfigValue<ItemMap> itemMap = value(ITEM_MAP_TYPE, "items", new ItemMap());

        public ItemsFile(BukkitPlugin plugin) {
            super(plugin, "items.yml", FileType.YAML, false);
//            updater.convert("unique_items.json", "unique_items.yml", (oldAccessor, newAccessor) -> { // TODO: Update conversion
//                newAccessor.setItemStackList("items", oldAccessor.getItemStackList("items"));
//            }).rename("unique_items.json", "unique_items_old.json");
        }
    }
}
