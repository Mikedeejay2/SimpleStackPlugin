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
import com.mikedeejay2.simplestack.util.*;
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
    private StackUtils stackUtils;
    private MoveUtils moveUtils;
    private ClickUtils clickUtils;
    private CheckUtils checkUtils;
    private CancelUtils cancelUtils;

    @Override
    public void onEnable()
    {
        super.onEnable();

        this.stackUtils = new StackUtils(this);
        this.moveUtils = new MoveUtils(this);
        this.clickUtils = new ClickUtils(this);
        this.checkUtils = new CheckUtils(this);
        this.cancelUtils = new CancelUtils(this);

        this.commandManager.setup("simplestack");

        this.commandManager.addSubcommand(new HelpCommand(this));
        this.commandManager.addSubcommand(new ReloadCommand(this));
        this.commandManager.addSubcommand(new ResetCommand(this));
        this.commandManager.addSubcommand(new SetAmountCommand(this));

        config = new Config(this);
        fileManager.addDataFile(config);


        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new InventoryClickListener(this), this);
        manager.registerEvents(new EntityPickupItemListener(this), this);
        manager.registerEvents(new BlockBreakListener(this), this);
        manager.registerEvents(new InventoryMoveItemListener(this), this);
        manager.registerEvents(new InventoryCloseListener(this), this);
        manager.registerEvents(new PrepareAnvilListener(this), this);
        manager.registerEvents(new InventoryDragListener(this), this);
        manager.registerEvents(new PlayerBucketEmptyListener(this), this);
        if(getMCVersion()[1] >= 16)
        {
            manager.registerEvents(new PrepareSmithingListener(this), this);
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
     * @return 64
     */
    public static int getMaxStack()
    {
        return MAX_AMOUNT_IN_STACK;
    }

    public Config config()
    {
        return config;
    }

    public StackUtils stackUtils()
    {
        return stackUtils;
    }

    public MoveUtils moveUtils()
    {
        return moveUtils;
    }

    public ClickUtils clickUtils()
    {
        return clickUtils;
    }

    public CheckUtils checkUtils()
    {
        return checkUtils;
    }

    public CancelUtils cancelUtils()
    {
        return cancelUtils;
    }
}
