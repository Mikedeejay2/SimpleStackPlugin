package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.ChatUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static com.mikedeejay2.simplestack.util.ChatUtils.*;

public class HelpCommand extends SubCommand
{
    public static final Simplestack plugin = Simplestack.getInstance();

    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        if(!sender.hasPermission("simplestack.help"))
        {
            ChatUtils.sendMessage(sender, "&cError: You don't have permission to access this command.");
            return;
        }
        String ver = plugin.getDescription().getVersion();
        ArrayList<BaseComponent[]> lines = new ArrayList<>();
        BaseComponent[] line1 = ChatUtils.getBaseComponentArray("&b &m                                                                              \n");
        BaseComponent[] line2 = ChatUtils.getBaseComponentArray("                              &9&lSimple &d&lStack&r                               \n");
        BaseComponent[] line3 = ChatUtils.getBaseComponentArray("                                                                               \n");
        BaseComponent[] line4 = ChatUtils.getBaseComponentArray("  &b/simplestack reload &d- &fUpdate config settings in game.\n");
        BaseComponent[] line5 = ChatUtils.getBaseComponentArray("                                                                               \n");
        BaseComponent[] line6 = ChatUtils.getBaseComponentArray("                               &7Version: " + ver + "\n");
        BaseComponent[] line7 = ChatUtils.getBaseComponentArray("&b &m                                                                              ");

        ChatUtils.setClickEvent(line4, getClickEvent(ClickEvent.Action.RUN_COMMAND, "/simplestack reload"));
        ChatUtils.setHoverEvent(line4, getHoverEvent(HoverEvent.Action.SHOW_TEXT, "Click to run /simplestack reload"));

        lines.add(line1);
        lines.add(line2);
        lines.add(line3);
        lines.add(line4);
        lines.add(line5);
        lines.add(line6);
        lines.add(line7);
        ChatUtils.printComponents(sender, lines.toArray(new BaseComponent[0][0]));

        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.2f, 1f);
    }

    @Override
    public String name()
    {
        return plugin.commandManager.help;
    }

    @Override
    public String info()
    {
        return "Help for simplestack commands";
    }

    @Override
    public String[] aliases()
    {
        return new String[0];
    }
}
