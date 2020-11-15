package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.AbstractSubCommand;
import com.mikedeejay2.mikedeejay2lib.gui.manager.PlayerGUI;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.gui.GUICreator;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigCommand extends AbstractSubCommand
{
    private final Simplestack plugin;

    public ConfigCommand(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Resets the config to the default values. Useful for if a player has messed up
     * their config and wants a fresh start without browsing through the files to delete
     * the config.yml file.
     *
     * @param sender The CommandSender that sent the command
     * @param args The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        PlayerGUI playerGUI = plugin.guiManager().getPlayer(player);
        if(playerGUI.getGUI() == null)
        {
            playerGUI.setGUI(GUICreator.createMainGUI(plugin));
        }
        else if(playerGUI.getGUI().containsModule(GUINavigatorModule.class))
        {
            playerGUI.openGUI();
        }
        else
        {
            playerGUI.setGUI(GUICreator.createMainGUI(plugin));
        }
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1f);
    }

    @Override
    public String name()
    {
        return "config";
    }

    @Override
    public String info(CommandSender sender)
    {
        return plugin.langManager().getText(sender, "simplestack.commands.config.info");
    }

    @Override
    public String[] aliases()
    {
        return new String[0];
    }

    @Override
    public String permission()
    {
        return "simplestack.config";
    }

    @Override
    public boolean playerRequired()
    {
        return true;
    }
}
