package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.SubCommand;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.api.SimpleStackConfig;
import com.mikedeejay2.simplestack.config.SimpleStackConfigImpl;
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
            plugin.sendMessage(player, Text.of("&c")
                .concat(Text.of("simplestack.warnings.held_item_required")));
            return;
        }
        SimpleStackConfig config = SimpleStackAPI.getConfig();
        config.removeUniqueItem(heldItem);
        plugin.sendMessage(sender, Text.of("&e&l%s&r &b%s").format(
            Text.of("simplestack.generic.success"),
            Text.of("simplestack.commands.removeitem.success")));
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3f, 1f);
    }

    @Override
    public String getName() {
        return "removeitem";
    }

    @Override
    public String getInfo(CommandSender sender) {
        return Text.of("simplestack.commands.removeitem.info").get(sender);
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
