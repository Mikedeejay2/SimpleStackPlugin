package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.simplestack.Simplestack;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatUtils
{
    private static final Simplestack plugin = Simplestack.getInstance();

    /**
     *
     * @param s The input string to be formatted
     * @return The string formatted with Minecraft color codes
     */
    public static String chat(String s)
    {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    /**
     * Send a message in console with formatted chat colors
     *
     * @param s The input string
     */
    public static void sendMessage(String s)
    {
        Bukkit.getConsoleSender().sendMessage(chat(getTitleString() + s));
    }

    /**
     * Send a message in console with formatted chat colors
     * Default color is red so it's more visible
     *
     * @param s The input string
     */
    public static void debug(String s)
    {
        Bukkit.getConsoleSender().sendMessage(chat(getTitleString() + "&c" + s));
    }

    /**
     * Sends the player a formatted message
     *
     * @param p Input player that will receive the message
     * @param s The message to be printed (will be formatted with colors)
     */
    public static void sendMessage(Player p, String s)
    {
        p.sendMessage(chat(getTitleString(p) + s));
    }

    /**
     * Sends the command sender (player or console) a formatted message
     *
     * @param p Input player that will receive the message
     * @param s The message to be printed (will be formatted with colors)
     */
    public static void sendMessage(CommandSender p, String s)
    {
        p.sendMessage(chat(getTitleString(p) + s));
    }

    /**
     * Gets a title tag that can be added to the beginning of text to define
     * what this text is coming from
     * @return The title string
     */
    public static String getTitleString()
    {
        return "&b[&9" + plugin.lang().getText("simplestack.title") + "&b] &r";
    }

    /**
     * Gets a title tag that can be added to the beginning of text to define
     * what this text is coming from based on the language from the CommandSender
     *
     * @param sender Player / console to base the title's language off of
     * @return The title string
     */
    public static String getTitleString(CommandSender sender)
    {
        return "&b[&9" + plugin.lang().getText(sender, "simplestack.title") + "&b] &r";
    }

    /**
     * Gets a title tag that can be added to the beginning of text to define
     * what this text is coming from based on the language from the player
     *
     * @param player Player to base the title's language off of
     * @return The title string
     */
    public static String getTitleString(Player player)
    {
        return "&b[&9" + plugin.lang().getText(player, "simplestack.title") + "&b] &r";
    }

    /**
     * Converts strings into Bungee API baseComponent arrays
     *
     * @param strings The strings that will be converted to base components
     * @return BaseComponent array of converted strings
     */
    public static BaseComponent[] getBaseComponentArray(String... strings)
    {
        ArrayList<BaseComponent> baseComponents = new ArrayList<BaseComponent>();
        for(String str : strings)
        {
            baseComponents.addAll(Arrays.asList(TextComponent.fromLegacyText(ChatUtils.chat(str))));
        }
        return baseComponents.toArray(new BaseComponent[0]);
    }

    /**
     * Creates a Bungee API ClickEvent to do something with a command
     *
     * @param action The ClickEvent Action that should happen on click
     * @param command The command to be used on the ClickEvent
     * @return A new click event that can be used with BaseComponents
     */
    public static ClickEvent getClickEvent(ClickEvent.Action action, String command)
    {
        return new ClickEvent(action, command);
    }

    /**
     * Creates a Bungee API ClickEvent to do something with a command
     *
     * @param action The HoverEvent Action to be used when cursor is hovered over applied text
     * @param text The string of text that will be used in the hover event
     * @return
     */
    public static HoverEvent getHoverEvent(HoverEvent.Action action, String text)
    {
        return new HoverEvent(action, new Text(text));
    }

    /**
     * Applies a ClickEvent to an array of BaseComponents
     *
     * @param components An array of BaseComponents that will have a ClickEvent applied to them
     * @param event The ClickEvent to be added to the components
     * @return The same BaseComponents array but with the click events applied
     */
    public static BaseComponent[] setClickEvent(BaseComponent[] components, ClickEvent event)
    {
        for(BaseComponent component : components)
        {
            component.setClickEvent(event);
        }
        return components;
    }

    /**
     * Applies a HoverEvent to an array of BaseComponents
     *
     * @param components An array of BaseComponents that will have a HoverEvent applied to them
     * @param event The HoverEvent to be added to the components
     * @return The same BaseComponents array but with the hover events applied
     */
    public static BaseComponent[] setHoverEvent(BaseComponent[] components, HoverEvent event)
    {
        for(BaseComponent component : components)
        {
            component.setHoverEvent(event);
        }
        return components;
    }

    /**
     * Combines multiple BaseComponent arrays and combines them into 1 larger array
     *
     * @param components An array of BaseComponents arrays that will be combined into one base components array
     * @return A combined array of all BaseComponents
     */
    public static BaseComponent[] combineComponents(BaseComponent[]... components)
    {
        ArrayList<BaseComponent> componentsArrayList = new ArrayList<BaseComponent>();
        for(BaseComponent[] componentsArr : components)
        {
            componentsArrayList.addAll(Arrays.asList(componentsArr));
        }
        return componentsArrayList.toArray(new BaseComponent[0]);
    }

    /**
     * Print all BaseComponents to a CommandSender
     *
     * @param sender The CommandSender that will receive the message
     * @param components An Array of BaseComponents arrays that will be printed, Each BaseComponent array being 1 line
     */
    public static void printComponents(CommandSender sender, BaseComponent[]... components)
    {
        sender.spigot().sendMessage(combineComponents(components));
    }

    /**
     * Print all BaseComponents to a Player
     *
     * @param player The Player that will receive the message
     * @param components An Array of BaseComponents arrays that will be printed, Each BaseComponent array being 1 line
     */
    public static void printComponents(Player player, BaseComponent[]... components)
    {
        player.spigot().sendMessage(combineComponents(components));
    }
}
