package com.mikedeejay2.simplestack;

import com.mikedeejay2.simplestack.commands.CommandManager;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.listeners.player.InventoryDragListener;
import com.mikedeejay2.simplestack.listeners.InventoryMoveItemListener;
import com.mikedeejay2.simplestack.listeners.PrepareAnvilListener;
import com.mikedeejay2.simplestack.listeners.PrepareSmithingListener;
import com.mikedeejay2.simplestack.listeners.player.BlockBreakListener;
import com.mikedeejay2.simplestack.listeners.player.EntityPickupItemListener;
import com.mikedeejay2.simplestack.listeners.player.InventoryClickListener;
import com.mikedeejay2.simplestack.listeners.player.InventoryCloseListener;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * Simple Stacking plugin by Mikedeejay2
 * Visit the listeners class for all of the code
 * If you find a bug, please report it to my Github:
 * https://github.com/Mikedeejay2/SimpleStackPlugin
 */
public final class Simplestack extends JavaPlugin
{
    private static Simplestack instance;
    private static Config customConfig;

    public CommandManager commandManager;

    // A namespaced key for adding a small piece of NBT data that makes each item "Unique".
    // This has to happen because if we don't make each item unique then the InventoryClickEvent won't be called
    // when trying to stack 2 fully stacked items of the same type.
    // Certainly a hacky work around, but it works.
    private static NamespacedKey key;

    public static double MCVersion;

    private static final String permission = "simplestack.use";

    @Override
    public void onEnable()
    {
        String verString = this.getServer().getVersion();
        if(verString.contains("1.16")) MCVersion = 1.16;
        else if(verString.contains("1.15")) MCVersion = 1.15;
        else if(verString.contains("1.14")) MCVersion = 1.14;

        setInstance(this);

        key = new NamespacedKey(this, "simplestack");

        customConfig = new Config();
        customConfig.onEnable();

        this.commandManager = new CommandManager();
        commandManager.setup();

        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new InventoryClickListener(), this);
        manager.registerEvents(new EntityPickupItemListener(), this);
        manager.registerEvents(new BlockBreakListener(), this);
        manager.registerEvents(new InventoryMoveItemListener(), this);
        manager.registerEvents(new InventoryCloseListener(), this);
        manager.registerEvents(new PrepareAnvilListener(), this);
        if(MCVersion >= 1.16) manager.registerEvents(new PrepareSmithingListener(), this);
        manager.registerEvents(new InventoryDragListener(), this);
    }

    @Override
    public void onDisable()
    {
        customConfig.onDisable();
    }

    public static Simplestack getInstance()
    {
        return instance;
    }

    public static void setInstance(Simplestack instance)
    {
        Simplestack.instance = instance;
    }

    public static Config getCustomConfig()
    {
        return customConfig;
    }

    public NamespacedKey getKey()
    {
        return key;
    }

    public static double getMCVersion()
    {
        return MCVersion;
    }

    public static String getPermission()
    {
        return permission;
    }
}
