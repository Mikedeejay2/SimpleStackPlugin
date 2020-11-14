package com.mikedeejay2.simplestack.gui;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.event.list.GUISwitchListPageEvent;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractHandlerDefault;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimStrips;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.util.chat.Chat;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Heads;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemCreator;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.gui.events.GUISwitchLangEvent;
import com.mikedeejay2.simplestack.gui.modules.GUIConfigModule;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GUICreator
{
    private static AnimatedGUIItem animatedGUIItem = null;

    public static GUIContainer createMainGUI(Simplestack plugin)
    {
        GUIContainer gui = new GUIContainer(plugin, "Configuration GUI", 5);
        GUIAnimationModule animation = new GUIAnimationModule(plugin, 1);
        GUIAnimStrips outlineModule = new GUIAnimStrips(getAnimItem());
        GUINavigatorModule naviModule = new GUINavigatorModule(plugin, "config");
        GUIConfigModule configModule = new GUIConfigModule(plugin);
        gui.addModule(animation);
        gui.addModule(outlineModule);
        gui.addModule(naviModule);
        gui.addModule(configModule);
        GUIInteractHandler handler = new GUIInteractHandlerDefault(64);
        gui.setInteractionHandler(handler);

        return gui;
    }

    public static AnimatedGUIItem getAnimItem()
    {
        if(animatedGUIItem == null)
        {
            AnimatedGUIItem item = new AnimatedGUIItem(ItemCreator.createItem(Material.BLUE_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), true);
            item.addFrame(ItemCreator.createItem(Material.BLUE_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 10);
            item.addFrame(ItemCreator.createItem(Material.PURPLE_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 10);
            item.addFrame(ItemCreator.createItem(Material.MAGENTA_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 10);
            item.addFrame(ItemCreator.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 10);
            item.addFrame(ItemCreator.createItem(Material.CYAN_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 10);
            item.addFrame(ItemCreator.createItem(Material.PURPLE_STAINED_GLASS_PANE, 1, GUIContainer.EMPTY_NAME), 10);
            animatedGUIItem = item;
        }
        return animatedGUIItem;
    }

    public static List<GUIItem> getLanguageList(Simplestack plugin)
    {
        List<GUIItem> items = new ArrayList<>();
        String clickMessage = Chat.chat("&f" + "Click to make this language the default language");
        GUIItem english = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_UNITED_STATES, 1,
                "&fEnglish", clickMessage, Chat.chat("&7en_us")));
        english.addEvent(new GUISwitchLangEvent(plugin, "en_us"));

        GUIItem simplifiedChinese = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_CHINA, 1,
                "&f简体中文 (Simplified Chinese)", clickMessage, Chat.chat("&7zh_cn")));
        simplifiedChinese.addEvent(new GUISwitchLangEvent(plugin, "zh_cn"));

        GUIItem korean = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_SOUTH_KOREA, 1,
                "&f한국어 (Korean)", clickMessage, Chat.chat("&7ko_kr")));
        korean.addEvent(new GUISwitchLangEvent(plugin, "ko_kr"));

        GUIItem argentina = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_ARGENTINA, 1,
                "&fEspañol (Argentinian Spanish)", clickMessage, Chat.chat("&7es_ar")));
        argentina.addEvent(new GUISwitchLangEvent(plugin, "es_ar"));

        GUIItem chilean = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_CHILE, 1,
                "&fEspañol (Chilean Spanish)", clickMessage, Chat.chat("&7es_cl")));
        chilean.addEvent(new GUISwitchLangEvent(plugin, "es_cl"));

        GUIItem mexican = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_MEXICO, 1,
                "&fEspañol (Mexican Spanish)", clickMessage, Chat.chat("&7es_mx")));
        mexican.addEvent(new GUISwitchLangEvent(plugin, "es_mx"));

        GUIItem uruguay = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_URUGUAY, 1,
                "&fEspañol (Uruguayan Spanish)", clickMessage, Chat.chat("&7es_uy")));
        uruguay.addEvent(new GUISwitchLangEvent(plugin, "es_uy"));

        GUIItem venezuela = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_VENEZUELA, 1,
                "&fEspañol (Venezuelan Spanish)", clickMessage, Chat.chat("&7es_ve")));
        venezuela.addEvent(new GUISwitchLangEvent(plugin, "es_ve"));

        items.add(english);
        items.add(simplifiedChinese);
        items.add(korean);
        items.add(argentina);
        items.add(chilean);
        items.add(mexican);
        items.add(uruguay);
        items.add(venezuela);

        String currentLocale = plugin.config().getLangLocale();
        int index = 0;
        GUIItem curLangItem = null;
        String curLocale = null;
        for(int i = 0; i < items.size(); ++i)
        {
            GUIItem curItem = items.get(i);
            String locale = curItem.getEvent(GUISwitchLangEvent.class).getLocale();
            if(!locale.equals(currentLocale)) continue;
            index = i;
            curLangItem = curItem;
            curLocale = locale;
        }
        if(curLangItem == null)
        {
            curLangItem = items.get(0);
            curLocale = "en_us";
        }
        AnimatedGUIItem newLangItem = new AnimatedGUIItem(curLangItem.getItemBase(), true);

        ItemStack curItem = curLangItem.getItemBase();
        ItemMeta curItemMeta = curItem.getItemMeta();
        List<String> newLore = new ArrayList<>();
        newLore.add(Chat.chat("&aSelected"));
        newLore.add(Chat.chat("&7" + curLocale));
        curItemMeta.setLore(newLore);
        curItem.setItemMeta(curItemMeta);

        newLangItem.addFrame(curItem, 10);
        String[] lore = curItemMeta.getLore().toArray(new String[0]);
        newLangItem.addFrame(ItemCreator.createHeadItem(Base64Heads.CONCRETE_LIME, 1, curItemMeta.getDisplayName(), lore), 10);
        items.remove(index);
        items.add(index, newLangItem);
        return items;
    }
}
