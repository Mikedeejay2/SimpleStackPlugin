package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.AbstractSubCommand;
import com.mikedeejay2.mikedeejay2lib.commands.CommandManager;
import com.mikedeejay2.mikedeejay2lib.language.LangManager;
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
public class HelpCommand extends AbstractSubCommand
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
     * @param args The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        LangManager lang = plugin.langManager();
        String ver = plugin.getDescription().getVersion();
        String[] ssArr = {"Simple", "Stack"};
        String version = lang.getText(sender, "simplestack.version", new String[]{"VERSION"}, new String[]{ver});
        CommandManager manager = plugin.commandManager();
        String[] commands = manager.getAllCommandStrings(false);
        ArrayList<BaseComponent[]> lines = new ArrayList<>();
        String lineString = "&b &m                                                                              ";
        String emptyString = "                                                                               \n";
        String titleString = "\n                              &9&l" + ssArr[0] + " &d&l" + ssArr[1] + "&r                               \n";
        String versionString = "                               &7" + version + "\n";

        BaseComponent[] lineComponents = plugin.chat().getBaseComponentArray(lineString);
        BaseComponent[] emptyComponents = plugin.chat().getBaseComponentArray(emptyString);
        BaseComponent[] titleComponents = plugin.chat().getBaseComponentArray(titleString);
        BaseComponent[] versionComponents = plugin.chat().getBaseComponentArray(versionString);

        lines.add(lineComponents);
        lines.add(titleComponents);
        lines.add(emptyComponents);

        for(int i = 1; i < commands.length; i++)
        {
            String command = commands[i];
            String commandInfo = manager.getSubcommand(command).info(sender);
            String hoverText = "&d" + lang.getText(sender, "simplestack.commands.click_to_run", new String[]{"COMMAND"}, new String[]{"/simplestack " + command});

            BaseComponent[] line = plugin.chat().getBaseComponentArray("  &b/simplestack " + command + " &d- &f" + commandInfo + "\n");

            plugin.chat().setClickEvent(line, plugin.chat().getClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/simplestack " + command));
            plugin.chat().setHoverEvent(line, plugin.chat().getHoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));

            lines.add(line);
        }

        lines.add(emptyComponents);
        lines.add(versionComponents);
        lines.add(lineComponents);

        BaseComponent[] combined = plugin.chat().combineComponents(lines.toArray(new BaseComponent[0][0]));
        plugin.chat().printComponents(sender, combined);

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
        return plugin.langManager().getText(sender, "simplestack.commands.help.info");
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
