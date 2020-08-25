package com.mikedeejay2.simplestack;

import com.mikedeejay2.simplestack.commands.CommandManager;
import com.mikedeejay2.simplestack.config.Config;
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

    @Override
    public void onEnable()
    {
        setInstance(this);

        customConfig = new Config();
        customConfig.onEnable();

        this.commandManager = new CommandManager();
        commandManager.setup();

        this.getServer().getPluginManager().registerEvents(new Listeners(), this);
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
}
