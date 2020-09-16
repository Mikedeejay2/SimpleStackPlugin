package com.mikedeejay2.simplestack;

import com.mikedeejay2.mikedeejay2lib.PluginBase;
import com.mikedeejay2.simplestack.commands.HelpCommand;
import com.mikedeejay2.simplestack.commands.ReloadCommand;
import com.mikedeejay2.simplestack.commands.ResetCommand;
import com.mikedeejay2.simplestack.commands.SetAmountCommand;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.listeners.player.*;
import com.mikedeejay2.simplestack.listeners.InventoryMoveItemListener;
import com.mikedeejay2.simplestack.listeners.PrepareAnvilListener;
import com.mikedeejay2.simplestack.listeners.PrepareSmithingListener;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.PluginManager;

import java.util.Arrays;

/*
 * Simple Stacking plugin by Mikedeejay2
 * If you find a bug, please report it to my Github:
 * https://github.com/Mikedeejay2/SimpleStackPlugin
 */
public final class Simplestack extends PluginBase
{
    private final String permission = "simplestack.use";

    // Max stack size. Changing this produces some really weird results because
    // Minecraft really doesn't know how to handle anything higher than 64.
    private static final int MAX_AMOUNT_IN_STACK = 64;

    private Config config;

    @Override
    public void onEnable()
    {
        super.onEnable();
        this.commandManager.setup("simplestack");

        this.commandManager.addSubcommand(new HelpCommand());
        this.commandManager.addSubcommand(new ReloadCommand());
        this.commandManager.addSubcommand(new ResetCommand());
        this.commandManager.addSubcommand(new SetAmountCommand());

        config = new Config();
        fileManager.addDataFile(config);


        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new InventoryClickListener(), this);
        manager.registerEvents(new EntityPickupItemListener(), this);
        manager.registerEvents(new BlockBreakListener(), this);
        manager.registerEvents(new InventoryMoveItemListener(), this);
        manager.registerEvents(new InventoryCloseListener(), this);
        manager.registerEvents(new PrepareAnvilListener(), this);
        manager.registerEvents(new InventoryDragListener(), this);
        manager.registerEvents(new PlayerBucketEmptyListener(), this);
        if(getMCVersion()[1] >= 16)
        {
            manager.registerEvents(new PrepareSmithingListener(), this);
        }
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }

    /**
     * Get the simplestack.use permission as a string
     *
     * @return simplestack.use String permission
     */
    public String getPermission()
    {
        return permission;
    }

    /**
     * Gets 64.
     *
     * @return
     */
    public static int getMaxStack()
    {
        return MAX_AMOUNT_IN_STACK;
    }

    public static Simplestack getInstance()
    {
        return (Simplestack)PluginBase.getInstance();
    }

    public Config config()
    {
        return config;
    }
}
