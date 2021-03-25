package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.SubCommand;
import com.mikedeejay2.simplestack.Simplestack;
import org.apache.commons.lang.math.NumberUtils;
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
public class SetAmountCommand implements SubCommand
{
    private final Simplestack plugin;

    public SetAmountCommand(Simplestack plugin)
    {
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
    public void onCommand(CommandSender sender, String[] args)
    {
        if(args.length < 2)
        {
            plugin.sendMessage(sender, "&c" + plugin.getLibLangManager().getText(sender, "errors.number_required") + "\n" +
                    plugin.getLangManager().getText("simplestack.commands.setamount.format"));
            return;
        }
        if(!NumberUtils.isNumber(args[1]))
        {
            plugin.sendMessage(sender, "&c" + plugin.getLibLangManager().getText(sender, "errors.not_a_number"));
            return;
        }
        int amount = Integer.parseInt(args[1]);
        if(amount < 0)
        {
            plugin.sendMessage(sender, "&c" + plugin.getLibLangManager().getText(sender, "errors.number_less_than_zero"));
            return;
        }
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.getType() == Material.AIR)
        {
            plugin.sendMessage(sender, "&c" + plugin.getLibLangManager().getText(sender, "errors.invalid_item_held"));
            return;
        }
        item.setAmount(amount);
        if(amount > plugin.config().getMaxAmount())
        {
            plugin.sendMessage(sender, "&e" + plugin.getLibLangManager().getText(sender, "warnings.big_number"));
        }
        plugin.sendMessage(sender, "&e&l" + plugin.getLibLangManager().getText(sender, "generic.success") + "&r &9" +
                plugin.getLangManager().getText(sender, "simplestack.commands.setamount.success"));
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1f);
    }

    @Override
    public String name()
    {
        return "setamount";
    }

    public String info(CommandSender sender)
    {
        return plugin.getLangManager().getText(sender, "simplestack.commands.setamount.info");
    }

    @Override
    public String[] aliases()
    {
        return new String[]{"rl"};
    }

    @Override
    public String permission()
    {
        return "simplestack.setamount";
    }

    @Override
    public boolean playerRequired()
    {
        return true;
    }
}
