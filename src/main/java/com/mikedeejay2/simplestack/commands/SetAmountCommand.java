package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.commands.manager.SubCommand;
import com.mikedeejay2.simplestack.util.ChatUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetAmountCommand extends SubCommand
{
    public static final Simplestack plugin = Simplestack.getInstance();

    /**
     * Sets the item amount of the player's main hand item to the
     * amount specified in args[1].
     *
     * @param sender The CommandSender that sent the command
     * @param args The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        if(!sender.hasPermission("simplestack.setamount"))
        {
            ChatUtils.sendMessage(sender, "&c" + plugin.lang().getText(sender, "simplestack.errors.nopermission.reload"));
            return;
        }
        if(!(sender instanceof Player))
        {
            ChatUtils.sendMessage(sender, "&c" + plugin.lang().getText(sender, "simplestack.errors.player_required"));
            return;
        }
        if(args.length < 2)
        {
            ChatUtils.sendMessage(sender, "&c" + plugin.lang().getText(sender, "simplestack.errors.number_required") + "\n" +
                    plugin.lang().getText("simplestack.commands.setamount.format"));
            return;
        }
        if(!NumberUtils.isNumber(args[1]))
        {
            ChatUtils.sendMessage(sender, "&c" + plugin.lang().getText(sender, "simplestack.errors.not_a_number"));
            return;
        }
        int amount = Integer.parseInt(args[1]);
        if(amount < 0)
        {
            ChatUtils.sendMessage(sender, "&c" + plugin.lang().getText(sender, "simplestack.errors.number_less_than_zero"));
            return;
        }
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.getType() == Material.AIR)
        {
            ChatUtils.sendMessage(sender, "&c" + plugin.lang().getText(sender, "simplestack.errors.invalid_item_held"));
            return;
        }
        item.setAmount(amount);
        if(amount > Simplestack.getMaxStack())
        {
            ChatUtils.sendMessage(sender, "&e" + plugin.lang().getText(sender, "simplestack.warnings.big_number"));
        }
        ChatUtils.sendMessage(sender, "&e&l" + plugin.lang().getText(sender, "simplestack.success") + "&r &9" +
                plugin.lang().getText(sender, "simplestack.commands.setamount.success"));
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1f);
    }

    @Override
    public String name()
    {
        return plugin.commandManager().setamount;
    }

    @Override
    public String info()
    {
        return plugin.lang().getText("simplestack.commands.setamount.info");
    }

    public String info(CommandSender sender)
    {
        return plugin.lang().getText(sender, "simplestack.commands.setamount.info");
    }

    @Override
    public String[] aliases()
    {
        return new String[]{"rl"};
    }
}
