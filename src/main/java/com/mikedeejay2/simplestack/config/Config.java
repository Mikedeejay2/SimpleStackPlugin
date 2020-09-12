package com.mikedeejay2.simplestack.config;

import com.mikedeejay2.mikedeejay2lib.yaml.YamlBase;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationOptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Config extends YamlBase
{
    private static final Simplestack plugin = Simplestack.getInstance();

    public Config()
    {

    }

    //Variables
    public ListMode LIST_MODE;
    public List<Material> LIST;
    public String LANG_LOCALE;
    public HashMap<Material, Integer> ITEM_AMOUNTS;

    /**
     * Enable the config. This loads all of the values into the above variables.
     */
    public void onEnable()
    {
        LANG_LOCALE = config.getString("Language");

        getListMode();
        fillList();
        fillItemAmounts();
    }

    private void fillItemAmounts()
    {
        ITEM_AMOUNTS = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("Item Amounts");
        Set<String> materialList = section.getValues(false).keySet();
        for(String mat : materialList)
        {
            Material material = Material.matchMaterial(mat);
            if(material == null && !mat.equals("Example Item"))
            {
                plugin.getLogger().warning(plugin.lang().getText("simplestack.warnings.invalid_material", new String[]{"MAT"}, new String[]{mat}));
                continue;
            }
            int amount = section.getInt(mat);
            if(amount == 0 || amount > Simplestack.getMaxStack())
            {
                plugin.getLogger().warning(plugin.lang().getText("simplestack.warnings.number_outside_of_range", new String[]{"MAT"}, new String[]{mat.toString()}));
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
        String listMode = config.getString("ListMode");
        try
        {
            LIST_MODE = ListMode.valueOf(listMode.toUpperCase().replaceAll(" ", "_"));
        }
        catch(Exception e)
        {
            plugin.getLogger().warning(
                    plugin.lang().getText("simplestack.warnings.invalid_list_mode", new String[]{"MODE"}, new String[]{listMode})
            );
            LIST_MODE = ListMode.BLACKLIST;
        }
    }

    /**
     * Fills the LIST variable with materials specified in the config.
     */
    private void fillList()
    {
        List<String> matList = config.getStringList("Items");
        LIST = new ArrayList<>();

        for(String mat : matList)
        {
            Material material = Material.matchMaterial(mat);
            if(material == null && !mat.equals("Example Item"))
            {
                plugin.getLogger().warning(plugin.lang().getText("simplestack.warnings.invalid_material", new String[]{"MAT"}, new String[]{mat}));
                continue;
            }
            LIST.add(material);
        }
    }

    /**
     * Disable the config. This isn't being used but it might be used in the future.
     */
    public void onDisable()
    {
        //Since we're not modifying values in game, we don't have to save this on disable.
        // This also fixes the bug of the chance that they modify the config.yml while server is running
        // and then when it's restarted their old config.yml gets rolled back.
        //saveFile();
    }

    /**
     * Save the config's file to disk.
     */
    public void saveFile()
    {
        plugin.saveConfig();
    }

    /**
     * Reload the config from the file if it was modified from outside the game.
     */
    public void reload()
    {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        onEnable();
    }

    /**
     * Reset the config and reload it to a fresh config with default values.
     */
    public void reset()
    {
        plugin.getResource("config.yml");
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        configFile.delete();
        reload();
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
}
