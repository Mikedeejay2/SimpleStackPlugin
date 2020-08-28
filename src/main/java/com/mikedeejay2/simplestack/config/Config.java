package com.mikedeejay2.simplestack.config;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Config
{
    private static final Simplestack plugin = Simplestack.getInstance();

    private FileConfiguration config;

    public Config()
    {
        this.config = plugin.getConfig();
    }

    //Variables
    public ListMode LIST_MODE;
    public List<Material> LIST;
    public String LANG_LOCALE;

    public void onEnable()
    {
        config.options().copyDefaults();
        plugin.saveDefaultConfig();

        LANG_LOCALE = config.getString("Language");

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

    public void onDisable()
    {
        //Since we're not modifying values in game, we don't have to save this on disable.
        // This also fixes the bug of the chance that they modify the config.yml while server is running
        // and then when it's restarted their old config.yml gets rolled back.
        //saveFile();
    }

    public void saveFile()
    {
        plugin.saveConfig();
    }

    public void reload()
    {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        onEnable();
    }
}
