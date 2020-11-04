package com.mikedeejay2.simplestack.config;

import com.google.gson.JsonElement;
import com.mikedeejay2.mikedeejay2lib.file.json.JsonFile;
import com.mikedeejay2.mikedeejay2lib.file.section.SectionAccessor;
import com.mikedeejay2.mikedeejay2lib.file.yaml.YamlFile;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Config extends YamlFile
{
    //Variables
    private ListMode listMode;
    private List<Material> materialList;
    private String langLocale;
    private Map<Material, Integer> itemAmounts;
    private List<ItemStack> uniqueItemList;
    private int maxAmount;

    // Internal config data
    private JsonFile uniqueItems;
    private boolean modified;
    private boolean loaded;

    public Config(Simplestack plugin)
    {
        super(plugin, "config.yml");
        this.modified = false;
        this.loaded = false;
        if(!fileExists())
        {
            loadFromJar(true);
            saveToDisk(true);
        }
        loadFromDisk(true);
    }

    private void loadData()
    {
        langLocale = getDefaultLang();
        plugin.langManager().setDefaultLang(langLocale);

        loadDefaultAmount();
        loadListMode();
        loadMaterialList();
        loadItemList();
        loadItemAmounts();

        loaded = true;
    }

    private void loadDefaultAmount()
    {
        maxAmount = accessor.getInt("Default Max Amount");
        if(maxAmount > 64 || maxAmount <= 0)
        {
            maxAmount = 64;
            plugin.chat().sendMessageLang("simplestack.warnings.invalid_max_amount");
        }
    }

    private void loadItemAmounts()
    {
        itemAmounts = new HashMap<>();
        SectionAccessor<YamlFile, Object> section = accessor.getSection("Item Amounts");
        Set<String> materialList = section.getKeys(false);
        for(String mat : materialList)
        {
            Material material = Material.matchMaterial(mat);
            if(material == null && !mat.equals("Example Item"))
            {
                plugin.getLogger().warning(plugin.langManager().getText("simplestack.warnings.invalid_material", new String[]{"MAT"}, new String[]{mat}));
                continue;
            }
            int amount = section.getInt(mat);
            if(amount == 0 || amount > 64)
            {
                plugin.getLogger().warning(plugin.langManager().getText("simplestack.warnings.number_outside_of_range", new String[]{"MAT"}, new String[]{mat.toString()}));
                continue;
            }
            itemAmounts.put(material, amount);
        }
    }

    /**
     * Get the list mode that the list should operate in. Whitelist or blacklist.
     */
    private void loadListMode()
    {
        String listMode = accessor.getString("List Mode");
        try
        {
            this.listMode = ListMode.valueOf(listMode.toUpperCase().replaceAll(" ", "_"));
        }
        catch(Exception e)
        {
            plugin.getLogger().warning(
                    plugin.langManager().getText("simplestack.warnings.invalid_list_mode", new String[]{"MODE"}, new String[]{listMode})
            );
            this.listMode = ListMode.BLACKLIST;
        }
    }

    /**
     * Fills the LIST variable with materials specified in the config.
     */
    private void loadMaterialList()
    {
        List<String> matList = accessor.getStringList("Item Types");
        materialList = new ArrayList<>();

        for(String mat : matList)
        {
            Material material = Material.matchMaterial(mat);
            if(material == null && !mat.equals("Example Item"))
            {
                plugin.getLogger().warning(plugin.langManager().getText("simplestack.warnings.invalid_material", new String[]{"MAT"}, new String[]{mat}));
                continue;
            }
            materialList.add(material);
        }
    }

    /**
     * Fills the LIST variable with materials specified in the config.
     */
    private void loadItemList()
    {
        this.uniqueItems = new JsonFile(plugin, "unique_items.json");
        if(!uniqueItems.fileExists()) uniqueItems.saveToDisk(true);
        uniqueItems.loadFromDisk(true);
        List<ItemStack> itemList = uniqueItems.getAccessor().getItemStackList("items");
        uniqueItemList = new ArrayList<>();
        if(itemList == null) return;

        for(ItemStack item : itemList)
        {
            if(item == null || item.getType().isAir())
            {
                plugin.getLogger().warning(plugin.langManager().getText("simplestack.warnings.invalid_unique_item"));
                continue;
            }
            uniqueItemList.add(item);
        }
    }

    /**
     * Returns whether a material has a custom amount set in the config or not.
     *
     * @param material The material to search for
     * @return If this item has a custom amount set or not
     */
    public boolean hasCustomAmount(Material material)
    {
        return itemAmounts.containsKey(material);
    }

    /**
     * Get the custom amount of an item that has been set in the config.
     * A check is required before running this commands, see hasCustomAmount.
     *
     * @param item The item to get the custom amount for
     * @return The custom amount for this item.
     */
    public int getAmount(ItemStack item)
    {
        if(containsUniqueItem(item))
        {
            return getUniqueItem(item).getAmount();
        }
        else if(hasCustomAmount(item.getType()))
        {
            return itemAmounts.get(item.getType());
        }
        return getMaxAmount();
    }

    /**
     * Get the default lang from the config. This is primarily for
     * the LangManager to use without reflection
     *
     * @return The default lang
     */
    public String getDefaultLang()
    {
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
    public boolean loadFromDisk(boolean throwErrors)
    {
        boolean success = super.loadFromDisk(throwErrors);
        updateFromJar(throwErrors);
        saveToDisk(throwErrors);
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
    public boolean loadFromJar(boolean throwErrors)
    {
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
    public boolean saveToDisk(boolean throwErrors)
    {
        if(loaded)
        {
            accessor.setString("List Mode", listMode == ListMode.BLACKLIST ? "Blacklist" : "Whitelist");
            accessor.setMaterialList("Item Types", materialList);
            accessor.setString("Language", langLocale);
            accessor.setInt("Default Max Amount", maxAmount);

            SectionAccessor<YamlFile, Object> itemAmtAccessor = accessor.getSection("Item Amounts");
            itemAmounts.forEach((material, amount) -> {if(material != null) itemAmtAccessor.setInt(material.toString(), amount);});

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
    public boolean resetFromJar(boolean throwErrors)
    {
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
    public boolean updateFromJar(boolean throwErrors)
    {
        if(accessor.contains("Items"))
        {
            List<Material> matList = accessor.getMaterialList("Items");
            accessor.delete("Items");
            accessor.setMaterialList("Item Types", matList);
        }
        if(accessor.contains("ListMode"))
        {
            String listMode = accessor.getString("ListMode");
            accessor.delete("ListMode");
            accessor.setString("List Mode", listMode);
        }
        return super.updateFromJar(throwErrors);
    }

    /**
     * Get the Material list's <tt>ListMode</tt>.
     * The list mode is either:
     * <ul>
     *     <li>Whitelist</li>
     *     <li>Blacklist</li>
     * </ul>
     *
     * @return The current <tt>ListMode</tt>
     */
    public ListMode getListMode()
    {
        return listMode;
    }

    /**
     * Get a list of the materials from the config
     *
     * @return The list of materials
     */
    public List<Material> getMaterialList()
    {
        return materialList;
    }

    /**
     * Get the default lang locale specified in the config.
     *
     * @return The lang locale
     */
    public String getLangLocale()
    {
        return langLocale;
    }

    /**
     * Get a map of material to item amount
     *
     * @return A map of material to item amount
     */
    public Map<Material, Integer> getItemAmounts()
    {
        return itemAmounts;
    }

    /**
     * Return whether the config contains a unique item that matches the item specified
     *
     * @param item The <tt>ItemStack</tt> to search for
     * @return Whether the item was found in the config
     */
    public boolean containsUniqueItem(ItemStack item)
    {
        for(ItemStack curItem : uniqueItemList)
        {
            if(!ItemComparison.equalsEachOther(curItem, item)) continue;
            return true;
        }
        return false;
    }

    /**
     * Get a unique item from the config based off of a reference item of the same properties
     *
     * @param item The <tt>ItemStack</tt> to find in the config
     * @return The <tt>ItemStack</tt> found with the same properties in the config
     */
    public ItemStack getUniqueItem(ItemStack item)
    {
        for(ItemStack curItem : uniqueItemList)
        {
            if(!ItemComparison.equalsEachOther(curItem, item)) continue;
            return curItem;
        }
        return null;
    }

    /**
     * Return whether the material list contains a specific material or not
     *
     * @param material The material to search for
     * @return Whether the material was found or not
     */
    public boolean containsMaterial(Material material)
    {
        return materialList.contains(material);
    }

    /**
     * Get the default max amount for items
     *
     * @return The default max amount for items
     */
    public int getMaxAmount()
    {
        return maxAmount;
    }

    /**
     * Add a unique item to the config at the player's request. <p>
     * This method does not save the config, only modifies it.
     *
     * @param player The player that requested the action
     * @param item The item to add to the config
     */
    public void addUniqueItem(Player player, ItemStack item)
    {
        removeUniqueItem(player, item);
        uniqueItemList.add(item);
        setModified(true);
    }

    /**
     * Add a material to the config at the player's request. <p>
     * This method does not save the config, only modifies it.
     *
     * @param player The player that requested the action
     * @param material The material to add to the config
     * @return Whether the action was successful or not
     */
    public boolean addMaterial(Player player, Material material)
    {
        if(containsMaterial(material))
        {
            plugin.chat().sendMessageLang(player, "simplestack.warnings.material_already_exists");
            return false;
        }
        materialList.add(material);
        setModified(true);
        return true;
    }

    /**
     * Removes a unique item from the config at the player's request. <p>
     * This method does not save the config, only modifies it.
     *
     * @param player The player that requested the action
     * @param item The item to from from the config
     * @return Whether the action was successful or not
     */
    public boolean removeUniqueItem(Player player, ItemStack item)
    {
        for(ItemStack curItem : uniqueItemList)
        {
            if(!ItemComparison.equalsEachOther(item, curItem)) continue;
            uniqueItemList.remove(curItem);
            setModified(true);
            break;
        }
        return true;
    }

    /**
     * Removes a material from the config at the player's request. <p>
     * This method does not save the config, only modifies it.
     *
     * @param player The player that requested the action
     * @param material The material to remove from the config
     * @return Whether the action was successful or not
     */
    public boolean removeMaterial(Player player, Material material)
    {
        materialList.remove(material);
        setModified(true);
        return true;
    }

    /**
     * Set the <tt>ListMode</tt> of the config. <p>
     * This method does not save the config, only modifies it.
     *
     * @param newMode The new <tt>ListMode</tt> to use in the config
     */
    public void setListMode(ListMode newMode)
    {
        this.listMode = newMode;
        setModified(true);
    }

    /**
     * Set the lang locale of the config. This automatically updates the <tt>LangManager</tt>
     * as well. <p>
     * This method does not save the config, only modifies it.
     *
     * @param newLocale
     */
    public void setLangLocale(String newLocale)
    {
        this.langLocale = newLocale;
        plugin.langManager().setDefaultLang(newLocale);
        setModified(true);
    }

    /**
     * Add a material and custom amount to the config at the player's request. <p>
     * This method does not save the config, only modifies it.
     *
     * @param player The player that requested the action
     * @param material The material to add to the config
     * @param amount The new max amount of the item
     */
    public void addCustomAmount(Player player, Material material, int amount)
    {
        if(hasCustomAmount(material)) removeCustomAmount(player, material);
        itemAmounts.put(material, amount);
        setModified(true);
    }

    /**
     * Removes a material from the custom amount list at the player's request. <p>
     * This method does not save the config, only modifies it.
     *
     * @param player The player that requested the action
     * @param material The material to remove from the config
     */
    public void removeCustomAmount(Player player, Material material)
    {
        if(!hasCustomAmount(material))
        {
            plugin.chat().sendMessageLang(player, "simplestack.warnings.custom_amount_does_not_exist");
            return;
        }
        itemAmounts.remove(material);
        setModified(true);
    }

    /**
     * Get whether this file has been modified or not
     *
     * @return Whether this files has been modified
     */
    public boolean isModified()
    {
        return modified;
    }

    /**
     * set whether this file has been modified or not
     *
     * @param modified The new modified state of this file
     */
    public void setModified(boolean modified)
    {
        this.modified = modified;
    }
}
