package com.mikedeejay2.simplestack.config;

import com.mikedeejay2.mikedeejay2lib.file.yaml.YamlFile;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Config extends YamlFile
{
    //Variables
    public ListMode LIST_MODE;
    public List<Material> LIST;
    public String LANG_LOCALE;
    public HashMap<Material, Integer> ITEM_AMOUNTS;

    public Config(Simplestack plugin)
    {
        super(plugin, "config.yml");
        if(!fileExists())
        {
            loadFromJar(true);
            saveToDisk(true);
        }
        loadFromDisk(true);
        loadData();
    }

    private void loadData()
    {
        LANG_LOCALE = getDefaultLang();
        plugin.langManager().setDefaultLang(LANG_LOCALE);

        getListMode();
        fillList();
        fillItemAmounts();
    }

    private void fillItemAmounts()
    {
        ITEM_AMOUNTS = new HashMap<>();
        ConfigurationSection section = yamlFile.getConfigurationSection("Item Amounts");
        Set<String> materialList = section.getValues(false).keySet();
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
            ITEM_AMOUNTS.put(material, amount);
        }
    }

    /**
     * Get the list mode that the list should operate in. Whitelist or blacklist.
     */
    private void getListMode()
    {
        String listMode = yamlFile.getString("ListMode");
        try
        {
            LIST_MODE = ListMode.valueOf(listMode.toUpperCase().replaceAll(" ", "_"));
        }
        catch(Exception e)
        {
            plugin.getLogger().warning(
                    plugin.langManager().getText("simplestack.warnings.invalid_list_mode", new String[]{"MODE"}, new String[]{listMode})
            );
            LIST_MODE = ListMode.BLACKLIST;
        }
    }

    /**
     * Fills the LIST variable with materials specified in the config.
     */
    private void fillList()
    {
        List<String> matList = yamlFile.getStringList("Items");
        LIST = new ArrayList<>();

        for(String mat : matList)
        {
            Material material = Material.matchMaterial(mat);
            if(material == null && !mat.equals("Example Item"))
            {
                plugin.getLogger().warning(plugin.langManager().getText("simplestack.warnings.invalid_material", new String[]{"MAT"}, new String[]{mat}));
                continue;
            }
            LIST.add(material);
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
        return ITEM_AMOUNTS.containsKey(material);
    }

    /**
     * Get the custom amount of a material that has been set in the config.
     * A check is required before running this commands, see hasCustomAmount.
     *
     * @param material The material to get the custom amount for
     * @return The custom amount for this item.
     */
    public int getAmount(Material material)
    {
        return ITEM_AMOUNTS.get(material);
    }

    /**
     * Get the default lang from the config. This is primarily for
     * the LangManager to use without reflection
     *
     * @return
     */
    public String getDefaultLang()
    {
        return yamlFile.getString("Language");
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
}
