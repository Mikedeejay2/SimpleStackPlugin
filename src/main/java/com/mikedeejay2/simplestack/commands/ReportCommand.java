package com.mikedeejay2.simplestack.commands;

import com.mikedeejay2.mikedeejay2lib.commands.SubCommand;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.chat.ChatConverter;
import com.mikedeejay2.mikedeejay2lib.util.debug.CrashReport;
import com.mikedeejay2.simplestack.SimpleStack;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.md_5.bungee.api.chat.ClickEvent.Action.*;
import static net.md_5.bungee.api.chat.HoverEvent.Action.*;

public class ReportCommand implements SubCommand {
    private final SimpleStack plugin;

    public ReportCommand(SimpleStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        final CrashReport report = new CrashReport(plugin, "Generated Simple Stack report", false, false);
        plugin.fillCrashReport(report);
        String reportStr = report.getReport()
            .replace("SimpleStack Crash Report", "Simple Stack Report");
        int cutIdxStart = reportStr.indexOf("A detailed walkthrough of the error, its code path and all known details is as follows:");
        int cutIdxEnd = reportStr.indexOf("-- Simple Stack Configuration --");
        reportStr = reportStr.substring(0, cutIdxStart).concat(reportStr.substring(cutIdxEnd));

        if(sender instanceof Player) {
            final Player player = (Player) sender;
            final Text clickHereText = Text.of("&e&n").concat("simplestack.command.report.click_here").color();
            final String clickHereStr = clickHereText.get(player);
            final BaseComponent[] components = TextComponent.fromLegacyText(clickHereStr);
            final ClickEvent clickEvent = ChatConverter.getClickEvent(COPY_TO_CLIPBOARD, reportStr);
            final HoverEvent hoverEvent = ChatConverter.getHoverEvent(SHOW_TEXT, clickHereStr);
            ChatConverter.setClickEvent(components, clickEvent);
            ChatConverter.setHoverEvent(components, hoverEvent);
            final BaseComponent[] prefix = TextComponent.fromLegacyText(plugin.getPrefix());

            ChatConverter.printComponents(player, prefix, components);
        } else {
            plugin.sendInfo("\n" + reportStr);
        }
    }

    @Override
    public String getName() {
        return "report";
    }

    @Override
    public String getInfo(CommandSender sender) {
        return Text.of("simplestack.commands.report.info").get(sender);
    }

    @Override
    public String getPermission() {
        return "simplestack.report";
    }
}
