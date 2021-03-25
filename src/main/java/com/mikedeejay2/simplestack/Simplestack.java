package com.mikedeejay2.simplestack;

import com.mikedeejay2.mikedeejay2lib.PluginBase;
import com.mikedeejay2.mikedeejay2lib.text.language.LangManager;
import com.mikedeejay2.mikedeejay2lib.util.bstats.BStats;
import com.mikedeejay2.mikedeejay2lib.util.recipe.RecipeUtil;
import com.mikedeejay2.mikedeejay2lib.util.update.UpdateChecker;
import com.mikedeejay2.simplestack.commands.*;
import com.mikedeejay2.simplestack.config.Config;
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
public final class Simplestack extends PluginBase
{
    // Permission for general Simple Stack use
    private final String permission = "simplestack.use";

    // The config of Simple Stack which stores all customizable data
    private Config config;

    private BStats bStats;
    private UpdateChecker updateChecker;
    private LangManager langManager;

    @Override
    public void onEnable()
    {
        super.onEnable();
        this.prefix = "&b[&9" + this.getDescription().getName() + "&b] &r";

        this.langManager = new LangManager(this, "lang");

        this.bStats = new BStats(this);
        this.bStats.init(9379);
        this.updateChecker = new UpdateChecker(this);
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
        dataManager.addFile(config);

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
        listenerManager.addListener(new PrepareItemCraftListener(this));
        if(getMCVersion().getVersionShort() >= 16)
        {
            listenerManager.addListener(new PrepareSmithingListener(this));
        }
        listenerManager.registerAll();

        GroundItemStacker stacker = new GroundItemStacker(this);
        stacker.runTaskTimer(this, 0, 20);

        RecipeUtil.preload(this, 0);
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
}
