package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class CustomTabCompleter implements TabCompleter
{
    private static final Simplestack plugin = Simplestack.getInstance();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        CommandManager manager = plugin.commandManager;
        if(!command.getName().equalsIgnoreCase(manager.main)) return null;
        ArrayList<String> commands = new ArrayList<>();
        switch(args.length)
        {
            case 1:
                commands.add(manager.help);
                commands.add(manager.reload);
                break;
        }
        return commands;
    }
}
