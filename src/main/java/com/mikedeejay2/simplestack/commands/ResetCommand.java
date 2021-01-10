package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.SubCommand;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to reset the config to its default values easily.
 *
 * @author Mikedeejay2
 */
public class ResetCommand implements SubCommand
{
    private final Simplestack plugin;

    public ResetCommand(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Resets the config to the default values. Useful for if a player has messed up
     * their config and wants a fresh start without browsing through the files to delete
     * the config.yml file.
     *
     * @param sender The CommandSender that sent the command
     * @param args   The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        plugin.config().resetFromJar(true);
        plugin.chat().sendMessage(sender, "&e&l" + plugin.langManager().getTextLib(sender, "generic.success") + "&r &9" + plugin.langManager().getText(sender, "simplestack.reset.success"));
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1f);
    }

    @Override
    public String name()
    {
        return "reset";
    }

    @Override
    public String info(CommandSender sender)
    {
        return plugin.langManager().getText(sender, "simplestack.commands.reset.info");
    }

    @Override
    public String[] aliases()
    {
        return new String[0];
    }

    @Override
    public String permission()
    {
        return "simplestack.reset";
    }

    @Override
    public boolean playerRequired()
    {
        return false;
    }
}
