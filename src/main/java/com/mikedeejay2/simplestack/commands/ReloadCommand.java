package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.SubCommand;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.config.Config;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReloadCommand implements SubCommand {
    private final SimpleStack plugin;

    public ReloadCommand(SimpleStack plugin) {
        this.plugin = plugin;
    }

    /**
     * The reload command reloads the config file from disk.
     * If the server was opened and then the config file was modified,
     * /simplestack reload could be run to reload the config in the server and make the
     * plugin function with the modified config.
     *
     * @param sender The <tt>CommandSender</tt> that sent the command
     * @param args   The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Config config = plugin.config();
        config.reload(true);
        plugin.sendMessage(sender,
                           "&e&l" + plugin.getLibLangManager().getText(sender, "generic.success") +
                               "&r &9" + plugin.getLangManager().getText(sender, "simplestack.reload.success"));
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1f);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getInfo(CommandSender sender) {
        return plugin.getLangManager().getText(sender, "simplestack.commands.reload.info");
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(Collections.singleton("rl"));
    }

    @Override
    public String getPermission() {
        return "simplestack.reload";
    }
}
