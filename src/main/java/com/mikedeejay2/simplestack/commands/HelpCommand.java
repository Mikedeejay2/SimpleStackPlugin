package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.commands.manager.CommandManager;
import com.mikedeejay2.simplestack.commands.manager.SubCommand;
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

    /**
     * In game help command. It has a list of all other commands with a brief description
     * about them. The commands can be clicked on to run.
     *
     * @param sender The CommandSender that sent the command
     * @param args The arguments for the command (subcommands)
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
        String version = lang.getText(sender, "simplestack.version", new String[]{"VERSION"}, new String[]{ver});
        CommandManager manager = plugin.commandManager();
        String[] commands = manager.getAllCommandStrings(false);
        ArrayList<BaseComponent[]> lines = new ArrayList<>();
        String lineString = "&b &m                                                                              \n";
        String emptyString = "                                                                               \n";
        String titleString = "                              &9&l" + ssArr[0] + " &d&l" + ssArr[1] + "&r                               \n";
        String versionString = "                               &7" + version + "\n";

        BaseComponent[] lineComponents = ChatUtils.getBaseComponentArray(lineString);
        BaseComponent[] emptyComponents = ChatUtils.getBaseComponentArray(emptyString);
        BaseComponent[] titleComponents = ChatUtils.getBaseComponentArray(titleString);
        BaseComponent[] versionComponents = ChatUtils.getBaseComponentArray(versionString);

        lines.add(lineComponents);
        lines.add(titleComponents);
        lines.add(emptyComponents);

        for(int i = 1; i < commands.length; i++)
        {
            String command = commands[i];
            String commandInfo = manager.get(command).info(sender);
            String hoverText = "&d" + lang.getText(sender, "simplestack.commands.click_to_run", new String[]{"COMMAND"}, new String[]{"/simplestack " + command});

            BaseComponent[] line = ChatUtils.getBaseComponentArray("  &b/simplestack " + command + " &d- &f" + commandInfo + "\n");

            ChatUtils.setClickEvent(line, getClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/simplestack " + command));
            ChatUtils.setHoverEvent(line, getHoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));

            lines.add(line);
        }

        lines.add(emptyComponents);
        lines.add(versionComponents);
        lines.add(lineComponents);

        BaseComponent[] combined = ChatUtils.combineComponents(lines.toArray(new BaseComponent[0][0]));
        ChatUtils.printComponents(sender, combined);

        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.2f, 1f);
    }

    @Override
    public String name()
    {
        return plugin.commandManager().help;
    }

    @Override
    public String info()
    {
        return plugin.lang().getText("simplestack.commands.help.info");
    }

    @Override
    public String info(CommandSender sender)
    {
        return plugin.lang().getText(sender, "simplestack.commands.help.info");
    }

    @Override
    public String[] aliases()
    {
        return new String[0];
    }
}
