package com.mikedeejay2.simplestack;

import com.mikedeejay2.mikedeejay2lib.PluginBase;
import com.mikedeejay2.mikedeejay2lib.runnable.EnhancedRunnable;
import com.mikedeejay2.simplestack.commands.*;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.listeners.*;
import com.mikedeejay2.simplestack.listeners.player.*;
import com.mikedeejay2.simplestack.runnables.GroundItemStacker;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;

/**
 * Simple Stack plugin for Minecraft 1.14 - 1.16.4
 * If you find a bug, please report it to the Github:
 * https://github.com/Mikedeejay2/SimpleStackPlugin
 *
 * @author Mikedeejay2
 */
public final class Simplestack extends PluginBase
{
    // Permission for general Simple Stack use
    private final String permission = "simplestack.use";

    // The config of Simple Stack which stores all customizable data
    private Config config;

    @Override
    public void onEnable()
    {
        super.onEnable();

        this.bStats.init(9379);
        this.updateChecker.init("Mikedeejay2", "SimpleStackPlugin");
        this.updateChecker.checkForUpdates(10);

        this.commandManager.setup("simplestack");

        this.commandManager.addSubcommand(new HelpCommand(this));
        this.commandManager.addSubcommand(new ReloadCommand(this));
        this.commandManager.addSubcommand(new ResetCommand(this));
        this.commandManager.addSubcommand(new SetAmountCommand(this));
        this.commandManager.addSubcommand(new ConfigCommand(this));
        // These commands are disabled because they don't function very well.
//        this.commandManager.addSubcommand(new AddItemCommand(this));
//        this.commandManager.addSubcommand(new RemoveItemCommand(this));

        this.config = new Config(this);
        fileManager.addDataFile(config);

        listenerManager.addListener(new InventoryClickListener(this));
        listenerManager.addListener(new InventoryClickListener(this));
        listenerManager.addListener(new EntityPickupItemListener(this));
        listenerManager.addListener(new BlockBreakListener(this));
        listenerManager.addListener(new InventoryMoveItemListener(this));
        listenerManager.addListener(new InventoryCloseListener(this));
        listenerManager.addListener(new PrepareAnvilListener(this));
        listenerManager.addListener(new InventoryDragListener(this));
        listenerManager.addListener(new PlayerBucketEmptyListener(this));
        listenerManager.addListener(new ItemMergeListener(this));
        listenerManager.addListener(new InventoryPickupItemListener(this));
        listenerManager.addListener(new PlayerItemConsumeListener(this));
        if(getMCVersion().getVersionShort() >= 16)
        {
            listenerManager.addListener(new PrepareSmithingListener(this));
        }
        listenerManager.registerAll();

        GroundItemStacker stacker = new GroundItemStacker(this);
        stacker.runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        if(config.isModified())
        {
            config.saveToDisk(true);
        }
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
     * Get the Config file for Simple Stack
     *
     * @return The config of Simple Stack
     */
    public Config config()
    {
        return config;
    }
}
