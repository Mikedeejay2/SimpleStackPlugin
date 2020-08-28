package com.mikedeejay2.simplestack;

import com.mikedeejay2.simplestack.commands.CommandManager;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.language.LangManager;
import com.mikedeejay2.simplestack.listeners.player.*;
import com.mikedeejay2.simplestack.listeners.InventoryMoveItemListener;
import com.mikedeejay2.simplestack.listeners.PrepareAnvilListener;
import com.mikedeejay2.simplestack.listeners.PrepareSmithingListener;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * Simple Stacking plugin by Mikedeejay2
 * If you find a bug, please report it to my Github:
 * https://github.com/Mikedeejay2/SimpleStackPlugin
 */
public final class Simplestack extends JavaPlugin
{
    private static Simplestack instance;
    private Config customConfig;

    private CommandManager commandManager;

    // A namespaced key for adding a small piece of NBT data that makes each item "Unique".
    // This has to happen because if we don't make each item unique then the InventoryClickEvent won't be called
    // when trying to stack 2 fully stacked items of the same type.
    // Certainly a hacky work around, but it works.
    private NamespacedKey key;

    // The Minecraft version for version safety down to 1.14
    public double MCVersion;

    private final String permission = "simplestack.use";

    // For managing languages
    private LangManager langManager;

    @Override
    public void onEnable()
    {
        // There's definitely an easier way to do this, create an issue in the GitHub
        // repository or a pull request if you know how.
        String verString = this.getServer().getVersion();
        if(verString.contains("1.16")) MCVersion = 1.16;
        else if(verString.contains("1.15")) MCVersion = 1.15;
        else if(verString.contains("1.14")) MCVersion = 1.14;

        setInstance(this);

        this.langManager = new LangManager();

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

    public Config getCustomConfig()
    {
        return customConfig;
    }

    public NamespacedKey getKey()
    {
        return key;
    }

    public double getMCVersion()
    {
        return MCVersion;
    }

    public String getPermission()
    {
        return permission;
    }

    public CommandManager getCommandManager()
    {
        return commandManager;
    }

    public LangManager lang()
    {
        return langManager;
    }
}
