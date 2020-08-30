package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class CommandManager implements CommandExecutor
{
    private ArrayList<SubCommand> commands = new ArrayList<SubCommand>();
    private static final Simplestack plugin = Simplestack.getInstance();
    private CustomTabCompleter completer;

    public CommandManager() {}

    // Add new subcommands here:
    public String main = "simplestack";
    public String reload = "reload";
    public String help = "help";
    public String reset = "reset";

    public void setup()
    {
        plugin.getCommand(main).setExecutor(this);

        completer = new CustomTabCompleter();
        plugin.getCommand(main).setTabCompleter(completer);

        // Add new subcommands here:
        this.commands.add(new HelpCommand());
        this.commands.add(new ReloadCommand());
        this.commands.add(new ResetCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(command.getName().equalsIgnoreCase(main))
        {
            if(args.length == 0)
            {
                args = new String[1];
                args[0] = "help";
            }

            SubCommand target = this.get(args[0]);

            if(target == null)
            {
                ChatUtils.sendMessage(sender, "&c" + plugin.lang().getText(sender, "simplestack.errors.invalid_subcommand"));
                return true;
            }

            ArrayList<String> arrayList = new ArrayList<String>();

            arrayList.addAll(Arrays.asList(args));

            arrayList.remove(0);

            try
            {
                target.onCommand(sender, args);
            }
            catch(Exception e)
            {
                ChatUtils.sendMessage(sender, "&c" + plugin.lang().getText(sender, "simplestack.errors.general"));
                e.printStackTrace();
            }
        }

        return true;
    }

    public SubCommand get(String name)
    {
        Iterator<SubCommand> subcommands = this.commands.iterator();

        while(subcommands.hasNext())
        {
            SubCommand sc = (SubCommand)subcommands.next();

            if(sc.name().equalsIgnoreCase(name))
            {
                return sc;
            }

            String[] aliases;
            int length = (aliases = sc.aliases()).length;

            for(int var5 = 0; var5 < length; ++var5)
            {
                String alias = aliases[var5];

                if(name.equalsIgnoreCase(alias))
                {
                    return sc;
                }
            }
        }
        return null;
    }

    public String[] getAllCommandStrings(boolean aliases)
    {
        ArrayList<String> strings = new ArrayList<>();
        for(SubCommand command : commands)
        {
            strings.add(command.name());
            if(aliases) Collections.addAll(strings, command.aliases());
        }
        return strings.toArray(new String[0]);
    }
}
