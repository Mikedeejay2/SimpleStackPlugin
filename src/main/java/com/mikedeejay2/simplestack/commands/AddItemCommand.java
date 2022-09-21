package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.SubCommand;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.config.Config;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Command that adds the held item of the player running the command to the unique items list
 * of the config.
 *
 * @author Mikedeejay2
 */
public class AddItemCommand implements SubCommand {
    private final SimpleStack plugin;

    public AddItemCommand(SimpleStack plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds the item of the player's held item to the unique items list of the config.
     *
     * @param sender The CommandSender that sent the command
     * @param args   The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if(heldItem.getType() == Material.AIR) {
            plugin.sendMessage(player, Text.of("&c").concat("simplestack.warnings.held_item_required"));
            return;
        }
        Config config = plugin.config();
        config.addUniqueItem(player, heldItem);
        config.saveToDisk(true);
        plugin.sendMessage(sender, Text.of("&e&l%s&r &b%s").format(
            Text.of("generic.success"),
            Text.of("simplestack.commands.additem.success")));
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1f);
    }

    @Override
    public String getName() {
        return "additem";
    }

    @Override
    public String getInfo(CommandSender sender) {
        return Text.of("simplestack.commands.additem.info").get(sender);
    }

    @Override
    public String getPermission() {
        return "simplestack.additem";
    }

    @Override
    public boolean isPlayerRequired() {
        return true;
    }
}
