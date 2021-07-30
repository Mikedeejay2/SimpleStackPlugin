package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.SubCommand;
import com.mikedeejay2.mikedeejay2lib.gui.manager.PlayerGUI;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.gui.GUICreator;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command that opens a GUI for the player to configure the Simple Stack's config.
 *
 * @author Mikedeejay2
 */
public class ConfigCommand implements SubCommand
{
    private final Simplestack plugin;

    public ConfigCommand(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Opens a configuration GUI for the player that ran the command.
     *
     * @param sender The CommandSender that sent the command
     * @param args   The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        PlayerGUI playerGUI = plugin.getGUIManager().getPlayer(player);
        if(playerGUI.getGUI() == null)
        {
            GUICreator.createMainGUI(plugin, player).open(player);
        }
        else if(playerGUI.getGUI().containsModule(GUINavigatorModule.class))
        {
            playerGUI.getGUI().open(player);
        }
        else
        {
            GUICreator.createMainGUI(plugin, player).open(player);
        }
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1f);
    }

    @Override
    public String getName()
    {
        return "config";
    }

    @Override
    public String getInfo(CommandSender sender)
    {
        return plugin.getLangManager().getText(sender, "simplestack.commands.config.info");
    }

    @Override
    public String getPermission()
    {
        return "simplestack.config";
    }

    @Override
    public boolean isPlayerRequired()
    {
        return true;
    }
}