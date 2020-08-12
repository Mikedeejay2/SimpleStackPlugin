package com.mikedeejay2.simplestack;

import org.bukkit.plugin.java.JavaPlugin;

public final class Simplestack extends JavaPlugin
{
    private static Simplestack instance;

    @Override
    public void onEnable()
    {
        setInstance(this);

        this.getServer().getPluginManager().registerEvents(new Listeners(), this);
    }

    @Override
    public void onDisable()
    {

    }

    public static Simplestack getInstance()
    {
        return instance;
    }

    public static void setInstance(Simplestack instance)
    {
        Simplestack.instance = instance;
    }
}
