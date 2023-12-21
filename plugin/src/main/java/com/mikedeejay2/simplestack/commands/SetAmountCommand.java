package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.SubCommand;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Command that sets the amount of the player's held item to the specified amount in args[1].
 * Basically just a helper command for if a player wants an item that they can't normally stack to.
 *
 * @author Mikedeejay2
 */
public class SetAmountCommand implements SubCommand {
    private final SimpleStack plugin;

    public SetAmountCommand(SimpleStack plugin) {
        this.plugin = plugin;
    }

    /**
     * Sets the item amount of the player's main hand item to the
     * amount specified in args[1].
     *
     * @param sender The CommandSender that sent the command
     * @param args   The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length < 2) {
            plugin.sendMessage(sender, Text.of("&c")
                .concat("simplestack.errors.number_required")
                .concat("\n")
                .concat("simplestack.commands.setamount.format"));
            return;
        }
        if(!NumberUtils.isCreatable(args[1])) {
            plugin.sendMessage(sender, Text.of("&c").concat("simplestack.errors.not_a_number"));
            return;
        }
        int amount = Integer.parseInt(args[1]);
        if(amount < 0) {
            plugin.sendMessage(sender, Text.of("&c").concat("simplestack.errors.number_less_than_zero"));
            return;
        }
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.getType() == Material.AIR) {
            plugin.sendMessage(sender, Text.of("&c").concat("simplestack.errors.invalid_item_held"));
            return;
        }
        item.setAmount(amount);
        if(amount > SimpleStackAPI.getConfig().getMaxStackOverride()) {
            plugin.sendMessage(sender, Text.of("&e").concat("simplestack.warnings.big_number"));
        }
        plugin.sendMessage(sender, Text.of("&e&l%s&r &b%s").format(
            Text.of("simplestack.generic.success"),
            Text.of("simplestack.commands.setamount.success")));
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1f);
    }

    @Override
    public String getName() {
        return "setamount";
    }

    @Override
    public String getInfo(CommandSender sender) {
        return Text.of("simplestack.commands.setamount.info").get(sender);
    }

    @Override
    public String getPermission() {
        return "simplestack.setamount";
    }

    @Override
    public boolean isPlayerRequired() {
        return true;
    }
}
