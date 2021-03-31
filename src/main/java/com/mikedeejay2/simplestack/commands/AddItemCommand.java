package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.SubCommand;
import com.mikedeejay2.simplestack.Simplestack;
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
public class AddItemCommand implements SubCommand
{
    private final Simplestack plugin;

    public AddItemCommand(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Adds the item of the player's held item to the unique items list of the config.
     *
     * @param sender The CommandSender that sent the command
     * @param args   The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if(heldItem.getType() == Material.AIR)
        {
            plugin.sendMessage(player, "&c" + plugin.getLangManager().getText(player, "simplestack.warnings.held_item_required"));
            return;
        }
        Config config = plugin.config();
        config.addUniqueItem(player, heldItem);
        config.saveToDisk(true);
        plugin.sendMessage(sender,
                "&e&l" + plugin.getLibLangManager().getText(player, "generic.success") +
                        "&r &9" + plugin.getLangManager().getText(player, "simplestack.commands.additem.success"));
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1f);
    }

    @Override
    public String getName()
    {
        return "additem";
    }

    @Override
    public String getInfo(CommandSender sender)
    {
        return plugin.getLangManager().getText(sender, "simplestack.commands.additem.info");
    }

    @Override
    public String getPermission()
    {
        return "simplestack.additem";
    }

    @Override
    public boolean isPlayerRequired()
    {
        return true;
    }
}
