package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.commands.manager.SubCommand;
import com.mikedeejay2.simplestack.util.ChatUtils;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCommand extends SubCommand
{
    public static final Simplestack plugin = Simplestack.getInstance();

    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        if(!sender.hasPermission("simplestack.reset"))
        {
            ChatUtils.sendMessage(sender, "&c" + plugin.lang().getText(sender, "simplestack.errors.nopermission.general"));
            return;
        }
        plugin.getCustomConfig().reset();
        ChatUtils.sendMessage(sender, "&e&l" + plugin.lang().getText(sender, "simplestack.success") + "&r &9" + plugin.lang().getText(sender, "simplestack.reset.success"));
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1f);
    }

    @Override
    public String name()
    {
        return plugin.getCommandManager().reset;
    }

    @Override
    public String info()
    {
        return plugin.lang().getText("simplestack.commands.reset.info");
    }

    @Override
    public String info(CommandSender sender)
    {
        return plugin.lang().getText(sender, "simplestack.commands.reset.info");
    }

    @Override
    public String[] aliases()
    {
        return new String[0];
    }
}
