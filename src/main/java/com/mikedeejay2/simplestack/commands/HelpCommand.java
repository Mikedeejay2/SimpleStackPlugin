package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.SubCommand;
import com.mikedeejay2.mikedeejay2lib.commands.CommandManager;
import com.mikedeejay2.mikedeejay2lib.text.PlaceholderFormatter;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.chat.ChatConverter;
import com.mikedeejay2.simplestack.SimpleStack;
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
public class HelpCommand implements SubCommand {
    private final SimpleStack plugin;

    public HelpCommand(SimpleStack plugin) {
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
    public void onCommand(CommandSender sender, String[] args) {
        String version = plugin.getDescription().getVersion();
        CommandManager manager = plugin.getCommandManager();
        String[] commands = manager.getAllCommandStrings(false);
        ArrayList<BaseComponent[]> lines = new ArrayList<>();
        String lineString = "&b &m                                                                              ";
        String emptyString = "                                                                               \n";
        String titleString = "\n                              &9&lSimple &d&lStack&r                               \n";
        String versionString = Text.of("  &7").concat(
            Text.of("simplestack.version")
                .placeholder(PlaceholderFormatter.of("version", version)))
            .concat("\n").get(sender);

        BaseComponent[] lineComponents = ChatConverter.getBaseComponentArray(lineString);
        BaseComponent[] emptyComponents = ChatConverter.getBaseComponentArray(emptyString);
        BaseComponent[] titleComponents = ChatConverter.getBaseComponentArray(titleString);
        BaseComponent[] versionComponents = ChatConverter.getBaseComponentArray(versionString);

        lines.add(lineComponents);
        lines.add(titleComponents);
        lines.add(emptyComponents);

        for(int i = 1; i < commands.length; i++) {
            String command = commands[i];
            String commandInfo = manager.getSubcommand(command).getInfo(sender);
            String hoverText = Text.of("&d").concat(
                Text.of("simplestack.commands.click_to_run")
                    .placeholder(PlaceholderFormatter.of("command", "/simplestack " + command)))
                .get(sender);

            BaseComponent[] line = ChatConverter.getBaseComponentArray(String.format("  &b/simplestack %s &d- &f%s\n", command, commandInfo));

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
    public String getName() {
        return "help";
    }

    @Override
    public String getInfo(CommandSender sender) {
        return Text.of("simplestack.commands.help.info").get(sender);
    }

    @Override
    public String getPermission() {
        return "simplestack.help";
    }
}
