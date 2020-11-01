package com.mikedeejay2.simplestack.config;

import com.mikedeejay2.mikedeejay2lib.file.DataFile;
import com.mikedeejay2.mikedeejay2lib.file.FileManager;
import com.mikedeejay2.mikedeejay2lib.file.json.JsonFile;
import com.mikedeejay2.mikedeejay2lib.file.section.SectionAccessor;
import com.mikedeejay2.mikedeejay2lib.file.yaml.YamlFile;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;
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

    public Config(Simplestack plugin)
    {
        super(plugin, "config.yml");
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

        loadListMode();
        loadMaterialList();
        loadItemList();
        loadItemAmounts();
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
            if(amount == 0 || amount > Simplestack.getMaxStack())
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
        String listMode = accessor.getString("ListMode");
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
        List<String> matList = accessor.getStringList("Items");
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
        FileManager fileManager = plugin.fileManager();
        if(!fileManager.containsFile("unique_items.json"))
        {
            DataFile uniqueItems = new JsonFile(plugin, "unique_items.json");
            fileManager.addDataFile(uniqueItems);
        }
        JsonFile json = (JsonFile) fileManager.getDataFile("unique_items.json");
        if(!json.fileExists()) json.saveToDisk(true);
        json.loadFromDisk(true);
        List<ItemStack> itemList = json.getAccessor().getItemStackList("items");
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
     * Returns whether an item has a custom amount set in the config or not.
     *
     * @param item The item to search for
     * @return If this item has a custom amount set or not
     */
    public boolean hasCustomAmount(ItemStack item)
    {
        return itemAmounts.containsKey(item.getType());
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
        else if(hasCustomAmount(item))
        {
            return itemAmounts.get(item.getType());
        }
        return Simplestack.getMaxStack();
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

    @Override
    public boolean loadFromDisk(boolean throwErrors)
    {
        boolean success = super.loadFromDisk(throwErrors);
        updateFromJar(throwErrors);
        saveToDisk(throwErrors);
        loadData();
        return success;
    }

    @Override
    public boolean loadFromJar(boolean throwErrors)
    {
        boolean success = super.loadFromJar(throwErrors);
        loadData();
        return success;
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

    public boolean containsUniqueItem(ItemStack item)
    {
        for(ItemStack curItem : uniqueItemList)
        {
            if(!ItemComparison.equalsEachOther(curItem, item)) continue;
            return true;
        }
        return false;
    }

    public ItemStack getUniqueItem(ItemStack item)
    {
        for(ItemStack curItem : uniqueItemList)
        {
            if(!ItemComparison.equalsEachOther(curItem, item)) continue;
            return curItem;
        }
        return null;
    }
}
