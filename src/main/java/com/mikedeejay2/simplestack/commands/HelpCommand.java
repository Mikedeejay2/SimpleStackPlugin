package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.SubCommand;
import com.mikedeejay2.mikedeejay2lib.commands.CommandManager;
import com.mikedeejay2.mikedeejay2lib.text.language.LangManager;
import com.mikedeejay2.mikedeejay2lib.util.chat.ChatConverter;
import com.mikedeejay2.simplestack.Simplestack;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Command for helping the player find the command that they're looking for.
 *
 * @author Mikedeejay2
 */
public class HelpCommand implements SubCommand
{
    private final Simplestack plugin;

    public HelpCommand(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * In game help command. It has a list of all other commands with a brief description
     * about them. The commands can be clicked on to run.
     *
     * @param sender The CommandSender that sent the command
     * @param args   The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        LangManager                lang          = plugin.getLangManager();
        String                     ver           = plugin.getDescription().getVersion();
        String[]                   ssArr         = {"Simple", "Stack"};
        String                     version       = lang.getText(sender, "simplestack.version", new String[]{"VERSION"}, new String[]{ver});
        CommandManager             manager       = plugin.commandManager();
        String[]                   commands      = manager.getAllCommandStrings(false);
        ArrayList<BaseComponent[]> lines         = new ArrayList<>();
        String                     lineString    = "&b &m                                                                              ";
        String                     emptyString   = "                                                                               \n";
        String                     titleString   = "\n                              &9&l" + ssArr[0] + " &d&l" + ssArr[1] + "&r                               \n";
        String                     versionString = "  &7" + version + "\n";

        BaseComponent[] lineComponents    = ChatConverter.getBaseComponentArray(lineString);
        BaseComponent[] emptyComponents   = ChatConverter.getBaseComponentArray(emptyString);
        BaseComponent[] titleComponents   = ChatConverter.getBaseComponentArray(titleString);
        BaseComponent[] versionComponents = ChatConverter.getBaseComponentArray(versionString);

        lines.add(lineComponents);
        lines.add(titleComponents);
        lines.add(emptyComponents);

        for(int i = 1; i < commands.length; i++)
        {
            String command = commands[i];
            String commandInfo = manager.getSubcommand(command).info(sender);
            String hoverText = "&d" + lang.getText(sender, "simplestack.commands.click_to_run", new String[]{"COMMAND"}, new String[]{"/simplestack " + command});

            BaseComponent[] line = ChatConverter.getBaseComponentArray("  &b/simplestack " + command + " &d- &f" + commandInfo + "\n");

            ChatConverter.setClickEvent(line, ChatConverter.getClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/simplestack " + command));
            ChatConverter.setHoverEvent(line, ChatConverter.getHoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));

            lines.add(line);
        }

        lines.add(emptyComponents);
        lines.add(versionComponents);
        lines.add(lineComponents);

        BaseComponent[] combined = ChatConverter.combineComponents(lines.toArray(new BaseComponent[0][0]));
        ChatConverter.printComponents(sender, combined);

        if(!(sender instanceof Player)) return;
        Player player = (Player) sender;
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.2f, 1f);
    }

    @Override
    public String name()
    {
        return "help";
    }

    @Override
    public String info(CommandSender sender)
    {
        return plugin.getLangManager().getText(sender, "simplestack.commands.help.info");
    }

    @Override
    public String[] aliases()
    {
        return new String[0];
    }

    @Override
    public String permission()
    {
        return "simplestack.help";
    }

    @Override
    public boolean playerRequired()
    {
        return false;
    }
}
