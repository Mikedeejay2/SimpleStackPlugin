package com.mikedeejay2.simplestack;

import com.mikedeejay2.mikedeejay2lib.BukkitPlugin;
import com.mikedeejay2.mikedeejay2lib.commands.CommandManager;
import com.mikedeejay2.mikedeejay2lib.text.language.LangManager;
import com.mikedeejay2.mikedeejay2lib.util.bstats.BStats;
import com.mikedeejay2.mikedeejay2lib.util.recipe.RecipeUtil;
import com.mikedeejay2.mikedeejay2lib.util.update.UpdateChecker;
import com.mikedeejay2.simplestack.commands.*;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.config.DebugConfig;
import com.mikedeejay2.simplestack.listeners.*;
import com.mikedeejay2.simplestack.listeners.player.*;
import com.mikedeejay2.simplestack.runnables.GroundItemStacker;

/**
 * Simple Stack plugin for Minecraft 1.14 - 1.16
 * <p>
 * The source code for Simple Stack can be found here:
 * <a href="https://github.com/Mikedeejay2/SimpleStackPlugin">https://github.com/Mikedeejay2/SimpleStackPlugin</a>
 * <p>
 * Simple Stack is powered by Mikedeejay2Lib.
 * <p>
 * The source code for Mikedeejay2Lib can be found here:
 * <a href="https://github.com/Mikedeejay2/Mikedeejay2Lib">https://github.com/Mikedeejay2/Mikedeejay2Lib</a>
 *
 * @author Mikedeejay2
 */
public final class Simplestack extends BukkitPlugin
{
    private static final int MINIMUM_VERSION = 14;

    // Permission for general Simple Stack use
    private final String permission = "simplestack.use";

    // The config of Simple Stack which stores all customizable data
    private Config config;
    private DebugConfig debugConfig;

    private BStats bStats;
    private UpdateChecker updateChecker;
    private LangManager langManager;
    private CommandManager commandManager;

    @Override
    public void onEnable()
    {
        super.onEnable();
        setPrefix("&b[&9" + this.getDescription().getName() + "&b]&r");
        if(checkVersion()) return;

        this.langManager = new LangManager(this, "lang");

        this.bStats = new BStats(this);
        this.bStats.init(9379);
        this.updateChecker = new UpdateChecker(this);
        this.updateChecker.init("Mikedeejay2", "SimpleStackPlugin");
        this.updateChecker.checkForUpdates(10);

        this.commandManager = new CommandManager(this, "simplestack");

        this.commandManager.addSubcommand(new HelpCommand(this));
        this.commandManager.addSubcommand(new ReloadCommand(this));
        this.commandManager.addSubcommand(new ResetCommand(this));
        this.commandManager.addSubcommand(new SetAmountCommand(this));
        this.commandManager.addSubcommand(new ConfigCommand(this));
        registerCommand(commandManager);
        // These commands are disabled because they don't function very well.
//        this.commandManager.addSubcommand(new AddItemCommand(this));
//        this.commandManager.addSubcommand(new RemoveItemCommand(this));

        this.config = new Config(this);
        this.debugConfig = new DebugConfig();

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
        registerEvent(new PrepareItemCraftListener(this));
        if(getMCVersion().getVersionShort() >= 16)
        {
            registerEvent(new PrepareSmithingListener(this));
        }

        GroundItemStacker stacker = new GroundItemStacker(this);
        stacker.runTaskTimer(this, 0, 20);

        RecipeUtil.preload(this, 0);
    }

    /**
     * Helper method to check the Minecraft version that the Minecraft server is running on.
     * <p>
     * If the version is not compatible, disable this plugin.
     *
     * @return Whether the plugin has been disabled.
     */
    private boolean checkVersion()
    {
        if(getMCVersion().getVersionShort() < MINIMUM_VERSION)
        {
            sendSevere(String.format("Simple Stack %s is not compatible Minecraft version %s!",
                                     this.getDescription().getVersion(),
                                     getMCVersion().getVersionString()));
            disablePlugin(this);
            return true;
        }
        return false;
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

    public DebugConfig getDebugConfig()
    {
        return debugConfig;
    }
}
