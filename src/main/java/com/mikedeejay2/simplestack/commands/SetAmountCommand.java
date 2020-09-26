package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.AbstractSubCommand;
import com.mikedeejay2.mikedeejay2lib.util.chat.Chat;
import com.mikedeejay2.simplestack.Simplestack;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetAmountCommand extends AbstractSubCommand<Simplestack>
{
    public SetAmountCommand(Simplestack plugin)
    {
        super(plugin);
    }

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
        if(args.length < 2)
        {
            plugin.chat().sendMessage(sender, "&c" + plugin.langManager().getTextLib(sender, "errors.number_required") + "\n" +
                    plugin.langManager().getText("simplestack.commands.setamount.format"));
            return;
        }
        if(!NumberUtils.isNumber(args[1]))
        {
            plugin.chat().sendMessage(sender, "&c" + plugin.langManager().getTextLib(sender, "errors.not_a_number"));
            return;
        }
        int amount = Integer.parseInt(args[1]);
        if(amount < 0)
        {
            plugin.chat().sendMessage(sender, "&c" + plugin.langManager().getTextLib(sender, "errors.number_less_than_zero"));
            return;
        }
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.getType() == Material.AIR)
        {
            plugin.chat().sendMessage(sender, "&c" + plugin.langManager().getTextLib(sender, "errors.invalid_item_held"));
            return;
        }
        item.setAmount(amount);
        if(amount > Simplestack.getMaxStack())
        {
            plugin.chat().sendMessage(sender, "&e" + plugin.langManager().getTextLib(sender, "warnings.big_number"));
        }
        plugin.chat().sendMessage(sender, "&e&l" + plugin.langManager().getTextLib(sender, "generic.success") + "&r &9" +
                plugin.langManager().getText(sender, "simplestack.commands.setamount.success"));
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1f);
    }

    @Override
    public String name()
    {
        return "setamount";
    }

    public String info(CommandSender sender)
    {
        return plugin.langManager().getText(sender, "simplestack.commands.setamount.info");
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
