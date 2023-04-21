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
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
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
    private final ConfigValueBoolean whitelist = valueBoolean(WHITELIST_TYPE, "List Mode");
    // Material list of the config (Item Type list in config)
    private final ConfigValue<ReferenceSet<Material>> materialSet = collectionValue(MATERIAL_LIST_TYPE, "Item Types", new ReferenceLinkedOpenHashSet<>());
    // Localization code specified in the config
    private final ConfigValue<String> locale = value(LOCALE_TYPE, "Language");
    // Item amounts based on the item's material (Item Type amounts list in config)
    private final ConfigValue<Reference2IntMap<Material>> itemAmountMap = mapValue(ITEM_AMOUNTS_TYPE, "Item Amounts", new Reference2IntLinkedOpenHashMap<>());
    // Unique items list from the unique_items.json
    private final ConfigValue<Object2IntMap<ItemStack>> uniqueItemMap = uniqueItemFile.uniqueItemMap;
    // The max amount for all items in minecraft
    private final ConfigValueInteger maxAmount = valueInteger(MAX_AMOUNT_TYPE, "Default Max Amount");
    // Whether stacked armor can be worn or not
    private final ConfigValueBoolean stackedArmorWearable = valueBoolean(ValueType.BOOLEAN, "Stacked Armor Wearable");

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
        uniqueItemMap.get().removeInt(item);
    }

    @Override
    public boolean isWhitelist() {
        return whitelist.getBoolean();
    }

    @Override
    public void setListMode(boolean whitelist) {
        this.whitelist.setBoolean(whitelist);
        setModified(true);
    }

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

    private static final class UniqueItemFile extends ConfigFile {
        private final ConfigValue<Object2IntMap<ItemStack>> uniqueItemMap =
            mapValue(UNIQUE_ITEM_LIST_TYPE, "items", new Object2IntLinkedOpenCustomHashMap<>(new UniqueStrategy()));

        public UniqueItemFile(BukkitPlugin plugin) {
            super(plugin, "unique_items.yml", FileType.YAML, false);
            updater.convert("unique_items.json", "unique_items.yml", (oldAccessor, newAccessor) -> {
                newAccessor.setItemStackList("items", oldAccessor.getItemStackList("items"));
            }).rename("unique_items.json", "unique_items_old.json");
        }

        /**
         * A strategy to compare items quickly. Uses {@link MethodHandle} to get {@link ItemMeta}, since it avoids
         * cloning the item meta.
         * <p>
         * Benchmark of {@link ItemStack#getItemMeta()}, reflection, and method handle:
         * <table>
         *     <tr>
         *         <th>Ms/tick (1 minute)</th>
         *         <th>Minimum</th>
         *         <th>Medium</th>
         *         <th>Average</th>
         *         <th>95th percentile</th>
         *         <th>Maximum</th>
         *     </tr>
         *     <tr>
         *         <td>getItemMeta</td>
         *         <td>1.29ms</td>
         *         <td>1.52ms</td>
         *         <td>1.84ms</td>
         *         <td>4.13ms</td>
         *         <td>9.63ms</td>
         *     </tr>
         *     <tr>
         *         <td>Reflection</td>
         *         <td>1.08ms</td>
         *         <td>1.25ms</td>
         *         <td>1.49ms</td>
         *         <td>3.75ms</td>
         *         <td>8.90ms</td>
         *     </tr>
         *     <tr>
         *         <td>Handle</td>
         *         <td>1.11ms</td>
         *         <td>1.25ms</td>
         *         <td>1.45ms</td>
         *         <td>3.24ms</td>
         *         <td>5.89ms</td>
         *     </tr>
         * </table>
         *
         * @author Mikedeejay2
         */
        private static final class UniqueStrategy implements Hash.Strategy<ItemStack> {
            private static final MethodHandle metaHandle;

            static {
                final MethodHandles.Lookup lookup = MethodHandles.lookup();
                MethodHandle handle = null;
                try {
                    final Field metaField = ItemStack.class.getDeclaredField("meta");
                    metaField.setAccessible(true);
                    handle = lookup.unreflectGetter(metaField);
                } catch(NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                metaHandle = handle;
            }

            private static ItemMeta fastGetMeta(ItemStack stack) {
                // If not Bukkit ItemStack, get meta regularly
                if(stack.getClass().getSimpleName().length() != 9) return stack.hasItemMeta() ? stack.getItemMeta() : null;
                try {
                    return (ItemMeta) metaHandle.invoke(stack);
                } catch(Throwable e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public int hashCode(ItemStack o) {
                int hash = 1;

                hash = hash * 31 + o.getType().hashCode();
                hash = hash * 31 + 1; // Don't include amount
                hash = hash * 31; // Don't include durability
                final ItemMeta meta = fastGetMeta(o);
                hash = hash * 31 + (meta != null ? meta.hashCode() : 0);

                return hash;
            }

            @Override
            public boolean equals(ItemStack a, ItemStack b) {
                if(a == b) return true;
                if(a == null || b == null) return false;
                if(a.getType() != b.getType()) return false;
                final ItemMeta aMeta = fastGetMeta(a);
                final ItemMeta bMeta = fastGetMeta(b);
                if(aMeta == bMeta) return true;
                if(aMeta == null || bMeta == null) return false;
                return aMeta.equals(bMeta);
            }
        }
    }
}
