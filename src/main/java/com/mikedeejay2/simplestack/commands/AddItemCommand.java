package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.AbstractSubCommand;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.config.Config;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AddItemCommand extends AbstractSubCommand
{
    private final Simplestack plugin;

    public AddItemCommand(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Resets the config to the default values. Useful for if a player has messed up
     * their config and wants a fresh start without browsing through the files to delete
     * the config.yml file.
     *
     * @param sender The CommandSender that sent the command
     * @param args The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if(heldItem.getType() == Material.AIR)
        {
            plugin.chat().sendMessage(player, "&c" + plugin.langManager().getText(player, "simplestack.warnings.held_item_required"));
            return;
        }
        Config config = plugin.config();
        config.addUniqueItem(player, heldItem);
        config.saveToDisk(true);
        plugin.chat().sendMessage(sender,
                "&e&l" + plugin.langManager().getTextLib(player, "generic.success") +
                        "&r &9" + plugin.langManager().getText(player, "simplestack.commands.additem.success"));
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1f);
    }

    @Override
    public String name()
    {
        return "additem";
    }

    @Override
    public String info(CommandSender sender)
    {
        return plugin.langManager().getText(sender, "simplestack.commands.additem.info");
    }

    @Override
    public String[] aliases()
    {
        return new String[0];
    }

    @Override
    public String permission()
    {
        return "simplestack.additem";
    }

    @Override
    public boolean playerRequired()
    {
        return true;
    }
}
