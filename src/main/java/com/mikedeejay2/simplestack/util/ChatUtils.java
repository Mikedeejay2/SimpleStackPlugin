package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.simplestack.Simplestack;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatUtils
{
    private static final Simplestack plugin = Simplestack.getInstance();

    public static String chat(String s)
    {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void sendMessage(String s)
    {
        Bukkit.getConsoleSender().sendMessage("[EventSystem] " + chat(s));
    }

    public static void debug(String s)
    {
        Bukkit.getConsoleSender().sendMessage(chat("&c[EventSystem] " + s));
    }

    public static void sendMessage(Player p, String s)
    {
        p.sendMessage(chat(s));
    }

    // Converts strings into Bungee API baseComponent arrays
    public static BaseComponent[] getBaseComponentArray(String... strings)
    {
        ArrayList<BaseComponent> baseComponents = new ArrayList<BaseComponent>();
        for(String str : strings)
        {
            baseComponents.addAll(Arrays.asList(TextComponent.fromLegacyText(ChatUtils.chat(str))));
        }
        return baseComponents.toArray(new BaseComponent[0]);
    }

    // Creates a Bungee API ClickEvent to do something with a command
    public static ClickEvent getClickEvent(ClickEvent.Action action, String command)
    {
        return new ClickEvent(action, command);
    }

    // Applies a ClickEvent to an array of BaseComponents
    public static BaseComponent[] setClickEvent(BaseComponent[] components, ClickEvent event)
    {
        for(BaseComponent component : components)
        {
            component.setClickEvent(event);
        }
        return components;
    }

    // Combines multiple BaseComponent arrays and combines them into 1 larger array
    public static BaseComponent[] combineComponents(BaseComponent[]... components)
    {
        ArrayList<BaseComponent> componentsArrayList = new ArrayList<BaseComponent>();
        for(BaseComponent[] componentsArr : components)
        {
            componentsArrayList.addAll(Arrays.asList(componentsArr));
        }
        return componentsArrayList.toArray(new BaseComponent[0]);
    }
}
