package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.SubCommand;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.config.Config;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Command that removes the held item of the player running the command from the unique items list
 * of the config.
 *
 * @author Mikedeejay2
 */
public class RemoveItemCommand implements SubCommand {
    private final SimpleStack plugin;

    public RemoveItemCommand(SimpleStack plugin) {
        this.plugin = plugin;
    }

    /**
     * Removes the item of the player's held item from the unique items list of the config.
     *
     * @param sender The CommandSender that sent the command
     * @param args   The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if(heldItem.getType() == Material.AIR) {
            plugin.sendMessage(player, "&c" + plugin.getLangManager().getText(player, "simplestack.warnings.held_item_required"));
            return;
        }
        Config config = plugin.config();
        config.removeUniqueItem(player, heldItem);
        config.saveToDisk(true);
        plugin.sendMessage(sender, String.format(
            "&e&l%s&r &b%s",
            plugin.getLibLangManager().getText(player, "generic.success"),
            plugin.getLangManager().getText(player, "simplestack.commands.removeitem.success")));
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1f);
    }

    @Override
    public String getName() {
        return "removeitem";
    }

    @Override
    public String getInfo(CommandSender sender) {
        return plugin.getLangManager().getText(sender, "simplestack.commands.removeitem.info");
    }

    @Override
    public String getPermission() {
        return "simplestack.removeitem";
    }

    @Override
    public boolean isPlayerRequired() {
        return true;
    }
}
