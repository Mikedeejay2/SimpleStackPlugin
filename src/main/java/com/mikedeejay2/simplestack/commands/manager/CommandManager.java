package com.mikedeejay2.simplestack.commands.manager;

import com.mikedeejay2.mikedeejay2lib.commands.AbstractCommandManager;
import com.mikedeejay2.simplestack.commands.HelpCommand;
import com.mikedeejay2.simplestack.commands.ReloadCommand;
import com.mikedeejay2.simplestack.commands.ResetCommand;
import com.mikedeejay2.simplestack.commands.SetAmountCommand;

public class CommandManager extends AbstractCommandManager
{
    public CommandManager()
    {
        super();
    }

    // Add new subcommands here:
    public String main = "simplestack";
    public String reload = "reload";
    public String help = "help";
    public String reset = "reset";
    public String setamount = "setamount";

    /**
     * Setup the command manager by initializing all subcommands
     */
    public void setup()
    {
        super.setup();
        // Add new subcommands here:
        this.commands.add(new HelpCommand());
        this.commands.add(new ReloadCommand());
        this.commands.add(new ResetCommand());
        this.commands.add(new SetAmountCommand());
    }
}
