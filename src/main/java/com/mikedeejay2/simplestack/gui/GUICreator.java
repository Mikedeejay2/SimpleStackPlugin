package com.mikedeejay2.simplestack.gui;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Miscellaneous static methods to create GUIs or GUI-related objects.
 *
 * @author Mikedeejay2
 */
public class GUICreator
{
    // The animated gui item seen on the main configuration screen.
    // It's stored so that it doesn't need to be created many times.
    private static AnimatedGUIItem animatedGUIItem = null;

    /**
     * Create the main configuration GUI
     *
     * @param plugin A reference to the Simple Stack plugin class
     * @param player The player requesting to open the GUI (For localization)
     * @return The <tt>GUIContainer</tt> for the main GUI
     */
    public static GUIContainer createMainGUI(Simplestack plugin, Player player)
    {
        GUIContainer       gui           = new GUIContainer(plugin, plugin.langManager().getText(player, "simplestack.gui.config.title"), 5);
        GUIAnimationModule animation     = new GUIAnimationModule(plugin, 1);
        GUIAnimStrips      outlineModule = new GUIAnimStrips(getAnimItem());
        GUINavigatorModule naviModule    = new GUINavigatorModule(plugin, "config");
        GUIConfigModule    configModule  = new GUIConfigModule(plugin);
        gui.addModule(animation);
        gui.addModule(outlineModule);
        gui.addModule(naviModule);
        gui.addModule(configModule);
        GUIInteractHandler handler = new GUIInteractHandlerDefault(64);
        gui.setInteractionHandler(handler);

        return gui;
    }

    /**
     * Get the animated GUI item for the main GUI screen.
     * If the item hasn't already been created a new item will be created.
     *
     * @return The animated item
     */
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

    /**
     * Get the language list used in the Language Select GUI screen.
     *
     * @param plugin A reference to Simple Stack's plugin class
     * @param player The player (For localization)
     * @return A list of GUIItems for each language
     */
    public static List<GUIItem> getLanguageList(Simplestack plugin, Player player)
    {
        List<GUIItem> items        = new ArrayList<>();
        String        clickMessage = Chat.chat("&f" + plugin.langManager().getText(player, "simplestack.gui.language.language_select"));
        GUIItem english = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_UNITED_STATES, 1,
                                                                 "&bEnglish", clickMessage, Chat.chat("&7en_us")));
        english.addEvent(new GUISwitchLangEvent(plugin, "en_us"));

        GUIItem simplifiedChinese = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_CHINA, 1,
                                                                           "&b简体中文 (Simplified Chinese)", clickMessage, Chat.chat("&7zh_cn")));
        simplifiedChinese.addEvent(new GUISwitchLangEvent(plugin, "zh_cn"));

        GUIItem korean = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_SOUTH_KOREA, 1,
                                                                "&b한국어 (Korean)", clickMessage, Chat.chat("&7ko_kr")));
        korean.addEvent(new GUISwitchLangEvent(plugin, "ko_kr"));

        GUIItem argentina = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_ARGENTINA, 1,
                                                                   "&bEspañol (Argentinian Spanish)", clickMessage, Chat.chat("&7es_ar")));
        argentina.addEvent(new GUISwitchLangEvent(plugin, "es_ar"));

        GUIItem chilean = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_CHILE, 1,
                                                                 "&bEspañol (Chilean Spanish)", clickMessage, Chat.chat("&7es_cl")));
        chilean.addEvent(new GUISwitchLangEvent(plugin, "es_cl"));

        GUIItem mexican = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_MEXICO, 1,
                                                                 "&bEspañol (Mexican Spanish)", clickMessage, Chat.chat("&7es_mx")));
        mexican.addEvent(new GUISwitchLangEvent(plugin, "es_mx"));

        GUIItem uruguay = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_URUGUAY, 1,
                                                                 "&bEspañol (Uruguayan Spanish)", clickMessage, Chat.chat("&7es_uy")));
        uruguay.addEvent(new GUISwitchLangEvent(plugin, "es_uy"));

        GUIItem venezuela = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_VENEZUELA, 1,
                                                                   "&bEspañol (Venezuelan Spanish)", clickMessage, Chat.chat("&7es_ve")));
        venezuela.addEvent(new GUISwitchLangEvent(plugin, "es_ve"));

        GUIItem german = new GUIItem(ItemCreator.createHeadItem(Base64Heads.FLAG_GERMANY, 1,
                                                                "&bDeutsch (German)", clickMessage, Chat.chat("&7de_de")));
        german.addEvent(new GUISwitchLangEvent(plugin, "de_de"));

        items.add(english);
        items.add(simplifiedChinese);
        items.add(korean);
        items.add(argentina);
        items.add(chilean);
        items.add(mexican);
        items.add(uruguay);
        items.add(venezuela);
        items.add(german);

        String  currentLocale = plugin.config().getLangLocale();
        int     index         = 0;
        GUIItem curLangItem   = null;
        String  curLocale     = null;
        for(int i = 0; i < items.size(); ++i)
        {
            GUIItem curItem = items.get(i);
            String  locale  = curItem.getEvent(GUISwitchLangEvent.class).getLocale();
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

        ItemStack    curItem     = curLangItem.getItemBase();
        ItemMeta     curItemMeta = curItem.getItemMeta();
        List<String> newLore     = new ArrayList<>();
        newLore.add(Chat.chat("&a" + plugin.langManager().getTextLib(player, "generic.enabled")));
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
