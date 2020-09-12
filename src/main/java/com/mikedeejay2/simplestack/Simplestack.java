package com.mikedeejay2.simplestack;

import com.mikedeejay2.mikedeejay2lib.PluginBase;
import com.mikedeejay2.mikedeejay2lib.commands.AbstractCommandManager;
import com.mikedeejay2.mikedeejay2lib.language.LangManager;
import com.mikedeejay2.mikedeejay2lib.yaml.YamlBase;
import com.mikedeejay2.simplestack.commands.manager.CommandManager;
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
    // A namespaced key for adding a small piece of NBT data that makes each item "Unique".
    // This has to happen because if we don't make each item unique then the InventoryClickEvent won't be called
    // when trying to stack 2 fully stacked items of the same type.
    // Certainly a hacky work around, but it works.
    private NamespacedKey key;

    private final String permission = "simplestack.use";

    // Max stack size. Changing this produces some really weird results because
    // Minecraft really doesn't know how to handle anything higher than 64.
    private static final int MAX_AMOUNT_IN_STACK = 64;

    @Override
    public void onEnable()
    {
        super.onEnable(new Config(), new CommandManager(), "simplestack");
        key = new NamespacedKey(this, "simplestack");

        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new InventoryClickListener(), this);
        manager.registerEvents(new EntityPickupItemListener(), this);
        manager.registerEvents(new BlockBreakListener(), this);
        manager.registerEvents(new InventoryMoveItemListener(), this);
        manager.registerEvents(new InventoryCloseListener(), this);
        manager.registerEvents(new PrepareAnvilListener(), this);
        if(getMCVersion()[1] >= 16) manager.registerEvents(new PrepareSmithingListener(), this);
        manager.registerEvents(new InventoryDragListener(), this);
        manager.registerEvents(new PlayerBucketEmptyListener(), this);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }

    /**
     * Get the namespaced key for making items unique
     *
     * @return The unique NamespacedKey
     */
    public NamespacedKey getKey()
    {
        return key;
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

    @Override
    public Config config()
    {
        return (Config) super.config();
    }

    @Override
    public LangManager lang()
    {
        return super.lang();
    }

    @Override
    public CommandManager commandManager()
    {
        return (CommandManager) super.commandManager();
    }
}
