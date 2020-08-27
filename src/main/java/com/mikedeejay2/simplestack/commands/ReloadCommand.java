package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.ChatUtils;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand extends SubCommand
{
    public static final Simplestack plugin = Simplestack.getInstance();

    /*
     * The reload command reloads the block list in the config class based on the current
     * config file. If the server was opened and then the config file was modified,
     * /simplestack reload could be run to reload the config in the server and make the
     * plugin function with the modified config.
     */
    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        if(!sender.hasPermission("simplestack.reload"))
        {
            ChatUtils.sendMessage(sender, "&c" + "Error: You don't have permission to reload the config.");
            return;
        }
        plugin.getCustomConfig().reload();
        ChatUtils.sendMessage(sender, "&e&lSuccess!&r &9The config has been reloaded.");
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1f);
    }

    @Override
    public String name()
    {
        return plugin.commandManager.reload;
    }

    @Override
    public String info()
    {
        return "Update config settings in game";
    }

    @Override
    public String[] aliases()
    {
        return new String[]{"rl"};
    }
}
