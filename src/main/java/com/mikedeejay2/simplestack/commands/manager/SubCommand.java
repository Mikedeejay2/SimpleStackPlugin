package com.mikedeejay2.simplestack.commands.manager;

import org.bukkit.command.CommandSender;

// Subcommand holds subcommands
public abstract class SubCommand
{
    public SubCommand() {}
    public abstract void onCommand(CommandSender sender, String[] args);
    public abstract String name();
    public abstract String info();
    public abstract String info(CommandSender sender);
    public abstract String[] aliases();
}
