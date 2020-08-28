package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.language.LangFile;
import com.mikedeejay2.simplestack.language.LangManager;
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

    /*
     * In game help command. It has a list of all other commands with a brief description
     * about them. The commands can be clicked on to run.
     */
    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        if(!sender.hasPermission("simplestack.help"))
        {
            ChatUtils.sendMessage(sender, "&c" + "Error: You don't have permission to access this command.");
            return;
        }

        LangManager lang = plugin.lang();
        String ver = plugin.getDescription().getVersion();
        String[] ssArr = lang.getText(sender, "simplestack.title").split(" ");
        String reloadInfo = lang.getText(sender, "simplestack.commands.reload.info");
        String verString = lang.getText(sender, "simplestack.version", new String[]{"VERSION"}, new String[]{ver});
        String reloadHover = lang.getText(sender, "simplestack.commands.click_to_run", new String[]{"COMMAND"}, new String[]{"/simplestack reload"});

        ArrayList<BaseComponent[]> lines = new ArrayList<>();
        BaseComponent[] line1 = ChatUtils.getBaseComponentArray("&b &m                                                                              \n");
        BaseComponent[] line2 = ChatUtils.getBaseComponentArray("                              &9&l" + ssArr[0] + " &d&l" + ssArr[1] + "&r                               \n");
        BaseComponent[] line3 = ChatUtils.getBaseComponentArray("                                                                               \n");
        BaseComponent[] line4 = ChatUtils.getBaseComponentArray("  &b/simplestack reload &d- &f" + reloadInfo + "\n");
        BaseComponent[] line5 = ChatUtils.getBaseComponentArray("                                                                               \n");
        BaseComponent[] line6 = ChatUtils.getBaseComponentArray("                               &7" + verString + "\n");
        BaseComponent[] line7 = ChatUtils.getBaseComponentArray("&b &m                                                                              ");

        ChatUtils.setClickEvent(line4, getClickEvent(ClickEvent.Action.RUN_COMMAND, "/simplestack reload"));
        ChatUtils.setHoverEvent(line4, getHoverEvent(HoverEvent.Action.SHOW_TEXT, reloadHover));

        BaseComponent[] combined = ChatUtils.combineComponents(line1, line2, line3, line4, line5, line6, line7);
        ChatUtils.printComponents(sender, combined);

        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.2f, 1f);
    }

    @Override
    public String name()
    {
        return plugin.getCommandManager().help;
    }

    @Override
    public String info()
    {
        return plugin.lang().getText("simplestack.commands.help.info");
    }

    @Override
    public String[] aliases()
    {
        return new String[0];
    }
}
