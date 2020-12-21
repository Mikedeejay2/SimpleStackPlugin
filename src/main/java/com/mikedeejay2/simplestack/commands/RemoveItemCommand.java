package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.AbstractSubCommand;
import com.mikedeejay2.simplestack.Simplestack;
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
public class RemoveItemCommand extends AbstractSubCommand
{
    private final Simplestack plugin;

    public RemoveItemCommand(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Removes the item of the player's held item from the unique items list of the config.
     *
     * @param sender The CommandSender that sent the command
     * @param args   The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        Player    player   = (Player) sender;
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if(heldItem.getType() == Material.AIR)
        {
            plugin.chat().sendMessage(player, "&c" + plugin.langManager().getText(player, "simplestack.warnings.held_item_required"));
            return;
        }
        Config config = plugin.config();
        config.removeUniqueItem(player, heldItem);
        config.saveToDisk(true);
        plugin.chat().sendMessage(sender,
                                  "&e&l" + plugin.langManager().getTextLib(player, "generic.success") +
                                          "&r &9" + plugin.langManager().getText(player, "simplestack.commands.removeitem.success"));
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1f);
    }

    @Override
    public String name()
    {
        return "removeitem";
    }

    @Override
    public String info(CommandSender sender)
    {
        return plugin.langManager().getText(sender, "simplestack.commands.removeitem.info");
    }

    @Override
    public String[] aliases()
    {
        return new String[0];
    }

    @Override
    public String permission()
    {
        return "simplestack.removeitem";
    }

    @Override
    public boolean playerRequired()
    {
        return true;
    }
}
