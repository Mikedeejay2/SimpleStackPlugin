package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.SubCommand;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.structure.NavigationHolder;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.gui.config.constructors.GUIConfigConstructor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command that opens a GUI for the player to configure the Simple Stack's config.
 *
 * @author Mikedeejay2
 */
public class ConfigCommand implements SubCommand {
    private final SimpleStack plugin;

    public ConfigCommand(SimpleStack plugin) {
        this.plugin = plugin;
    }

    /**
     * Opens a configuration GUI for the player that ran the command.
     *
     * @param sender The CommandSender that sent the command
     * @param args   The arguments for the command (subcommands)
     */
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        GUIContainer gui = plugin.getGUIManager().getPlayer(player).getGUI();
        if(gui != null && gui.containsModule(GUINavigatorModule.class)) {
            gui.open(player);
        } else {
            NavigationHolder<GUIContainer> navigation = plugin.getGUIManager().getPlayer(player).getNavigation("config");
            navigation.clearBack();
            navigation.clearForward();
            GUIConfigConstructor.INSTANCE.get().open(player);
        }
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3f, 1f);
    }

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getInfo(CommandSender sender) {
        return Text.of("simplestack.commands.config.info").get(sender);
    }

    @Override
    public String getPermission() {
        return "simplestack.config";
    }

    @Override
    public boolean isPlayerRequired() {
        return true;
    }
}
