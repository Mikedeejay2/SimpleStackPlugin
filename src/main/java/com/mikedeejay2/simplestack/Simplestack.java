package com.mikedeejay2.simplestack;

import com.mikedeejay2.mikedeejay2lib.BukkitPlugin;
import com.mikedeejay2.mikedeejay2lib.commands.CommandManager;
import com.mikedeejay2.mikedeejay2lib.text.language.LangManager;
import com.mikedeejay2.mikedeejay2lib.util.bstats.BStats;
import com.mikedeejay2.mikedeejay2lib.util.update.UpdateChecker;
import com.mikedeejay2.simplestack.commands.*;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.listeners.*;
import com.mikedeejay2.simplestack.listeners.player.*;
import com.mikedeejay2.simplestack.runnables.GroundItemStacker;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;

/**
 * Simple Stack plugin for Minecraft 1.14 - 1.17.1
 * If you find a bug, please report it to the Github:
 * https://github.com/Mikedeejay2/SimpleStackPlugin
 *
 * @author Mikedeejay2
 */
public final class Simplestack extends BukkitPlugin
{
    // Permission for general Simple Stack use
    private final String permission = "simplestack.use";

    // The config of Simple Stack which stores all customizable data
    private Config config;

    private CommandManager commandManager;
    private UpdateChecker updateChecker;
    private BStats bStats;
    private LangManager langManager;

    @Override
    public void onEnable()
    {
        super.onEnable();

        setPrefix("&b[&9" + this.getDescription().getName() + "&b]&r");

        this.commandManager = new CommandManager(this, "simplestack");
        commandManager.setDefaultSubCommand("help");
        registerCommand(commandManager);
        this.updateChecker = new UpdateChecker(this);
        this.bStats = new BStats(this);
        this.langManager = new LangManager(this, "lang");

        this.bStats.init(9379);
        this.updateChecker.init("Mikedeejay2", "SimpleStackPlugin");
        this.updateChecker.checkForUpdates(10);

        this.commandManager.addSubcommand(new HelpCommand(this));
        this.commandManager.addSubcommand(new ReloadCommand(this));
        this.commandManager.addSubcommand(new ResetCommand(this));
        this.commandManager.addSubcommand(new SetAmountCommand(this));
        this.commandManager.addSubcommand(new ConfigCommand(this));
        // These commands are disabled because they don't function very well.
//        this.commandManager.addSubcommand(new AddItemCommand(this));
//        this.commandManager.addSubcommand(new RemoveItemCommand(this));

        this.config = new Config(this);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            registerEvent(new InventoryClickListener(this));
            registerEvent(new EntityPickupItemListener(this));
            registerEvent(new BlockBreakListener(this));
            registerEvent(new InventoryMoveItemListener(this));
            registerEvent(new InventoryCloseListener(this));
            registerEvent(new PrepareAnvilListener(this));
            registerEvent(new InventoryDragListener(this));
            registerEvent(new PlayerBucketEmptyListener(this));
            registerEvent(new ItemMergeListener(this));
            registerEvent(new InventoryPickupItemListener(this));
            registerEvent(new PlayerItemConsumeListener(this));
            registerEvent(new ItemSpawnListener(this));
            if(getMCVersion().getVersionShort() >= 16)
            {
                registerEvent(new PrepareSmithingListener(this));
            }
        }, 2);

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

    public LangManager getLangManager()
    {
        return langManager;
    }

    public CommandManager getCommandManager()
    {
        return commandManager;
    }
}
