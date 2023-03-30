package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.SubCommand;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.config.SimpleStackConfigImpl;
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
     * @param sender The <code>CommandSender</code> that sent the command
     * @param args   The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        SimpleStackConfigImpl config = ((SimpleStackConfigImpl) SimpleStackAPI.getConfig());
        config.reload(true);
        plugin.sendMessage(sender, Text.of("&e&l%s&r &b%s").format(
            Text.of("simplestack.generic.success"),
            Text.of("simplestack.reload.success")));
        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3f, 1f);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getInfo(CommandSender sender) {
        return Text.of("simplestack.commands.reload.info").get(sender);
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
