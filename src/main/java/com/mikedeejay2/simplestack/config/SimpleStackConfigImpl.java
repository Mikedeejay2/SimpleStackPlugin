package com.mikedeejay2.simplestack.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.mikedeejay2.mikedeejay2lib.data.json.JsonFile;
import com.mikedeejay2.mikedeejay2lib.data.section.SectionAccessor;
import com.mikedeejay2.mikedeejay2lib.data.yaml.YamlFile;
import com.mikedeejay2.mikedeejay2lib.text.PlaceholderFormatter;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.text.language.TranslationManager;
import com.mikedeejay2.mikedeejay2lib.util.debug.CrashReportSection;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Config class for holding all configuration values for Simple Stack and
 * managing file saving / loading.
 *
 * @author Mikedeejay2
 */
public class SimpleStackConfigImpl extends YamlFile implements SimpleStackConfig {
    private final SimpleStack plugin;
    //Variables
    // List mode of the material list. Either Blacklist of Whitelist.
    private boolean whitelist;
    // Material list of the config (Item Type list in config)
    private final List<Material> materialList;
    // Localization code specified in the config
    private String langLocale;
    // Item amounts based on the item's material (Item Type amounts list in config)
    private final List<MaterialAndAmount> itemAmounts;
    // Unique items list from the unique_items.json
    private final List<ItemStack> uniqueItemList;
    // The max amount for all items in minecraft
    private int maxAmount;
    // Whether stacked armor can be worn or not
    private boolean stackedArmorWearable;

    // Internal config data
    // The unique items json file
    private JsonFile uniqueItems;
    // Whether this config has been modified or not
    private boolean modified;
    // Whether this config has been loaded or not
    private boolean loaded;

    public SimpleStackConfigImpl(SimpleStack plugin) {
        super(plugin, "config.yml");
        this.plugin = plugin;
        this.modified = false;
        this.loaded = false;
        this.materialList = new ArrayList<>();
        this.itemAmounts = new ArrayList<>();
        this.uniqueItemList = new ArrayList<>();
        if(!fileExists()) {
            loadFromJar(true);
            setLocale(TranslationManager.SYSTEM_LOCALE);
            super.saveToDisk(true);
        } else loadFromDisk(true);
    }

    /**
     * Load all data from the config files into this class.
     */
    private void loadData() {
        langLocale = getDefaultLocale();
        TranslationManager.GLOBAL.setGlobalLocale(langLocale);

        loadDefaultAmount();
        loadListMode();
        loadMaterialList();
        loadItemList();
        loadItemAmounts();
        loadStackedArmor();

        loaded = true;
    }

    private void loadStackedArmor() {
        stackedArmorWearable = accessor.getBoolean("Stacked Armor Wearable");
    }

    /**
     * Load the default max amount into the <tt>maxAmount</tt> variable for this config
     */
    private void loadDefaultAmount() {
        maxAmount = accessor.getInt("Default Max Amount");
        if(maxAmount > 64 || maxAmount <= 0) {
            maxAmount = 64;
            plugin.sendMessage(Text.of("simplestack.warnings.invalid_max_amount"));
        }
    }

    /**
     * Load item amounts into the <tt>itemAmounts</tt> map for this config
     */
    private void loadItemAmounts() {
        itemAmounts.clear();
        SectionAccessor<YamlFile, Object> section = accessor.getSection("Item Amounts");
        Set<String> materialList = section.getKeys(false);
        for(String mat : materialList) {
            Material material = Material.matchMaterial(mat);
            if(material == null && !mat.equals("Example Item")) {
                plugin.sendWarning(Text.of("simplestack.warnings.invalid_material").placeholder(
                    PlaceholderFormatter.of("mat", mat)));
                continue;
            }
            int amount = section.getInt(mat);
            if(amount == 0 || amount > 64) {
                plugin.sendWarning(Text.of("simplestack.warnings.number_outside_of_range").placeholder(
                    PlaceholderFormatter.of("mat", mat)));
                continue;
            }
            if(material != null) itemAmounts.add(new MaterialAndAmount(material, amount));
        }
    }

    /**
     * Load the list mode into the <tt>ListMode</tt> variable for this config
     */
    private void loadListMode() {
        String listMode = accessor.getString("List Mode");
        try {
            whitelist = listMode.trim().equalsIgnoreCase("whitelist");
        } catch(Exception e) {
            plugin.sendWarning(Text.of("simplestack.warnings.invalid_list_mode").placeholder(
                PlaceholderFormatter.of("mode", listMode)));
            this.whitelist = false;
        }
    }

    /**
     * Load the material list into the <tt>materialList</tt> list for this config
     */
    private void loadMaterialList() {
        List<String> matList = accessor.getStringList("Item Types");
        materialList.clear();

        for(String mat : matList) {
            Material material = Material.matchMaterial(mat);
            if(material == null && !mat.equals("Example Item")) {
                plugin.sendWarning(Text.of("simplestack.warnings.invalid_material").placeholder(
                    PlaceholderFormatter.of("mat", mat)));
                continue;
            }
            if(material == null) continue;
            materialList.add(material);
        }
    }

    /**
     * Load the unique items list into the <tt>uniqueItems</tt> list for this config
     */
    private void loadItemList() {
        this.uniqueItems = new JsonFile(plugin, "unique_items.json");
        if(!uniqueItems.fileExists()) uniqueItems.saveToDisk(true);
        uniqueItems.loadFromDisk(true);
        List<ItemStack> itemList = uniqueItems.getAccessor().getItemStackList("items");
        uniqueItemList.clear();
        if(itemList == null) return;

        for(ItemStack item : itemList) {
            if(item == null || item.getType().isAir()) {
                plugin.sendWarning(Text.of("simplestack.warnings.invalid_unique_item"));
                continue;
            }
            uniqueItemList.add(item);
        }
    }

    /**
     * Get the default lang from the config. This is primarily for
     * the LangManager to use without reflection
     *
     * @return The default lang
     */
    public String getDefaultLocale() {
        return accessor.getString("Language");
    }

    /**
     * Overridden method from <tt>DataFile</tt> that loads from disk.
     * This method also loads the data into the above variables for quick access.
     * This method will also attempt to update the yaml file in case it is outdated.
     *
     * @param throwErrors Whether this method should throw errors it encounters or not
     * @return Whether the load was successful or not
     */
    @Override
    public boolean loadFromDisk(boolean throwErrors) {
        boolean success = super.loadFromDisk(throwErrors);
        updateFromJar(throwErrors);
        super.saveToDisk(throwErrors);
        loadData();
        return success;
    }

    /**
     * Overridden method from <tt>DataFile</tt> that loads from a jar file.
     * This method also loads the data into the above variables for quick access.
     *
     * @param throwErrors Whether this method should throw errors it encounters or not
     * @return Whether the load was successful or not
     */
    @Override
    public boolean loadFromJar(boolean throwErrors) {
        boolean success = super.loadFromJar(throwErrors);
        loadData();
        return success;
    }

    /**
     * Overridden method from <tt>DataFile</tt> that saves the current config file to the disk.
     * This method also saves the "unique_items.json" file that this config file controls.
     *
     * @param throwErrors Whether this method should throw errors it encounters or not
     * @return Whether the file save was successful or not
     */
    @Override
    public boolean saveToDisk(boolean throwErrors) {
        if(loaded) {
            accessor.setString("List Mode", whitelist ? "Whitelist" : "Blacklist");
            List<String> materials = new ArrayList<>();
            for(Material material : materialList) {
                if(material == null) continue;
                materials.add(material.toString());
            }
            accessor.setStringList("Item Types", materials);
            accessor.setString("Language", langLocale);
            accessor.setInt("Default Max Amount", maxAmount);
            accessor.setBoolean("Stacked Armor Wearable", stackedArmorWearable);

            accessor.delete("Item Amounts");
            SectionAccessor<YamlFile, Object> itemAmtAccessor = accessor.getSection("Item Amounts");
            for(MaterialAndAmount mata : itemAmounts) {
                Material material = mata.getMaterial();
                if(material == null || material == Material.AIR) continue;
                int amount = mata.getAmount();
                String materialStr = material.toString();
                itemAmtAccessor.setInt(materialStr, amount);
            }

            SectionAccessor<JsonFile, JsonElement> uniqueItemsAccessor = uniqueItems.getAccessor();
            uniqueItemsAccessor.setItemStackList("items", uniqueItemList);
        }
        setModified(false);

        boolean success;
        success = super.saveToDisk(throwErrors);
        if(uniqueItems != null) success = uniqueItems.saveToDisk(true) && success;

        return success;
    }

    /**
     * Overridden method from <tt>DataFile</tt> that resets the config to its default state.
     *
     * @param throwErrors Whether this method should throw errors it encounters or not
     * @return Whether the reset was successful or not
     */
    @Override
    public boolean resetFromJar(boolean throwErrors) {
        this.loaded = false;
        return super.resetFromJar(throwErrors);
    }

    /**
     * Overridden method from <tt>DataFile</tt> that attempts to update the current file with
     * any new values found inside of the jar. This method will also update old naming conventions
     * that might be found in the file as well.
     *
     * @param throwErrors Whether this method should throw errors it encounters or not
     * @return Whether the update from jar was successful or not
     */
    @Override
    public boolean updateFromJar(boolean throwErrors) {
        if(accessor.contains("Items")) {
            List<Material> matList = accessor.getMaterialList("Items");
            accessor.delete("Items");
            accessor.setMaterialList("Item Types", matList);
        }
        if(accessor.contains("ListMode")) {
            String listMode = accessor.getString("ListMode");
            accessor.delete("ListMode");
            accessor.setString("List Mode", listMode);
        }
        if(accessor.contains("Hopper Movement Checks")) {
            accessor.delete("Hopper Movement Checks");
        }
        if(accessor.contains("Ground Stacking Checks")) {
            accessor.delete("Ground Stacking Checks");
        }
        if(accessor.contains("Creative Item Dragging")) {
            accessor.delete("Creative Item Dragging");
        }
        return super.updateFromJar(throwErrors);
    }

    /**
     * Overridden method from <tt>DataFile</tt> that reloads the config from disk
     * if the file hasn't been modified in game, and saves the file to disk if the
     * file has been modified in game. This prevents data loss from reloads.
     *
     * @param throwErrors Whether this method should throw any errors it encounters to console or not
     * @return Whether the reload was successful or not
     */
    @Override
    public boolean reload(boolean throwErrors) {
        if(modified) {
            return saveToDisk(true);
        } else if(fileExists()) {
            return super.reload(throwErrors);
        }
        return loadFromJar(true) && super.saveToDisk(true);
    }

    public List<Material> getMaterialsRef() {
        return materialList;
    }

    public List<MaterialAndAmount> getItemAmountsRef() {
        return itemAmounts;
    }

    public List<ItemStack> getUniqueItemsRef() {
        return uniqueItemList;
    }

    /////////////////////////////////////
    // API methods below this point
    /////////////////////////////////////

    @Override
    public int getAmount(ItemStack item) {
        if(containsUniqueItem(item)) {
            return getUniqueItemAmount(item);
        }
        return getAmount(item.getType());
    }

    @Override
    public int getAmount(Material type) {
        if(containsCustomAmount(type)) {
            return itemAmounts.get(itemAmounts.indexOf(new MaterialAndAmount(type, 0))).getAmount();
        }
        if(isWhitelist() == containsMaterial(type)) {
            return getMaxAmount();
        }
        return -1;
    }

    @Override
    public int getUniqueItemAmount(ItemStack item) {
        for(ItemStack curItem : uniqueItemList) {
            if(!ItemComparison.equalsEachOther(curItem, item)) continue;
            return curItem.getAmount();
        }
        return -1;
    }

    @Override
    public boolean containsMaterial(Material material) {
        return materialList.contains(material);
    }

    @Override
    public boolean containsCustomAmount(Material material) {
        return itemAmounts.contains(new MaterialAndAmount(material, 0));
    }

    @Override
    public boolean containsUniqueItem(ItemStack item) {
        for(ItemStack curItem : uniqueItemList) {
            if(!ItemComparison.equalsEachOther(curItem, item)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Set<Material> getMaterials() {
        return ImmutableSet.copyOf(materialList);
    }

    @Override
    public Map<Material, Integer> getItemAmounts() {
        ImmutableMap.Builder<Material, Integer> builder = ImmutableMap.builder();
        for(MaterialAndAmount entry : itemAmounts) {
            builder.put(entry.getMaterial(), entry.getAmount());
        }
        return builder.build();
    }

    @Override
    public Set<ItemStack> getUniqueItems() {
        return ImmutableSet.copyOf(uniqueItemList);
    }

    @Override
    public void addMaterial(Material material) {
        if(containsMaterial(material)) return;
        materialList.add(material);
        setModified(true);
    }

    @Override
    public void addCustomAmount(Material material, int amount) {
        if(containsCustomAmount(material)) removeCustomAmount(material);
        itemAmounts.add(new MaterialAndAmount(material, amount));
        setModified(true);
    }

    @Override
    public void addUniqueItem(ItemStack item) {
        removeUniqueItem(item);
        uniqueItemList.add(item);
        setModified(true);
    }

    @Override
    public void removeMaterial(Material material) {
        materialList.remove(material);
        setModified(true);
    }

    @Override
    public void removeCustomAmount(Material material) {
        itemAmounts.remove(new MaterialAndAmount(material, 0));
        setModified(true);
    }

    @Override
    public void removeUniqueItem(ItemStack item) {
        for(ItemStack curItem : uniqueItemList) {
            if(!ItemComparison.equalsEachOther(item, curItem)) continue;
            uniqueItemList.remove(curItem);
            setModified(true);
            break;
        }
    }

    @Override
    public boolean isWhitelist() {
        return whitelist;
    }

    @Override
    public void setListMode(boolean whitelist) {
        this.whitelist = whitelist;
        setModified(true);
    }

    @Override
    public boolean isStackedArmorWearable() {
        return stackedArmorWearable;
    }

    @Override
    public void setStackedArmorWearable(boolean stackedArmorWearable) {
        this.stackedArmorWearable = stackedArmorWearable;
        setModified(true);
    }

    @Override
    public int getMaxAmount() {
        return maxAmount;
    }

    @Override
    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
        setModified(true);
    }

    @Override
    public String getLocale() {
        return langLocale;
    }

    @Override
    public void setLocale(String newLocale) {
        this.langLocale = newLocale;
        TranslationManager.GLOBAL.setGlobalLocale(newLocale);
        setModified(true);
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void fillCrashReportSection(CrashReportSection section) {
        section.addDetail("Materials", getMaterials().toString());
        section.addDetail("Item Amounts", getItemAmounts().toString());
        section.addDetail("Unique Items", getUniqueItems().toString());
        section.addDetail("Is Whitelist Mode", String.valueOf(isWhitelist()));
        section.addDetail("Is Stacked Armor Wearable", String.valueOf(isStackedArmorWearable()));
        section.addDetail("Max Amount", String.valueOf(getMaxAmount()));
        section.addDetail("Locale", getLocale());
        section.addDetail("Is Config Modified", String.valueOf(isModified()));
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
}
