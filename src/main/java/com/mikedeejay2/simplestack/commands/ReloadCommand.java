package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.AbstractSubCommand;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.commands.manager.CommandManager;
import com.mikedeejay2.simplestack.util.ChatUtils;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand extends AbstractSubCommand
{
    public static final Simplestack plugin = Simplestack.getInstance();

    /**
     * The reload command reloads the block list in the config class based on the current
     * config file. If the server was opened and then the config file was modified,
     * /simplestack reload could be run to reload the config in the server and make the
     * plugin function with the modified config.
     *
     * @param sender The CommandSender that sent the command
     * @param args The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        if(!sender.hasPermission("simplestack.reload"))
        {
            ChatUtils.sendMessage(sender, "&c" + plugin.lang().getText(sender, "simplestack.errors.nopermission.reload"));
            return;
        }
        plugin.config().reload();
        ChatUtils.sendMessage(sender, "&e&l" + plugin.lang().getText(sender, "simplestack.success") + "&r &9" + plugin.lang().getText(sender, "simplestack.reload.success"));
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1f);
    }

    @Override
    public String name()
    {
        return plugin.commandManager().reload;
    }

    @Override
    public String info()
    {
        return plugin.lang().getText("simplestack.commands.reload.info");
    }

    @Override
    public String info(CommandSender sender)
    {
        return plugin.lang().getText(sender, "simplestack.commands.reload.info");
    }

    @Override
    public String[] aliases()
    {
        return new String[]{"rl"};
    }
}
