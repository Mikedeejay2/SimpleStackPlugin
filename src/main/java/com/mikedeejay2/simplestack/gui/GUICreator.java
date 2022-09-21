package com.mikedeejay2.simplestack.gui;

import com.mikedeejay2.mikedeejay2lib.gui.GUIConstructor;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification;
import com.mikedeejay2.mikedeejay2lib.gui.interact.GUIInteractHandler;
import com.mikedeejay2.mikedeejay2lib.gui.interact.normal.GUIInteractHandlerDefault;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.decoration.GUIAnimDecoratorModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.navigation.GUINavigatorModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.gui.debug.GUIDebugOpenerModule;
import com.mikedeejay2.simplestack.gui.events.GUISwitchLangEvent;
import com.mikedeejay2.simplestack.gui.modules.GUIConfigModule;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.mikedeejay2.mikedeejay2lib.gui.util.SlotMatcher.*;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification.Position;
import com.mikedeejay2.mikedeejay2lib.gui.animation.AnimationSpecification.Style;

/**
 * Miscellaneous static methods to create GUIs or GUI-related objects.
 *
 * @author Mikedeejay2
 */
public class GUICreator implements GUIConstructor {
    private final SimpleStack plugin;
    // The animated gui item seen on the main configuration screen.
    // It's stored so that it doesn't need to be created many times.
    private static final AnimatedGUIItem ANIMATED_GUI_ITEM =
        new AnimatedGUIItem(ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).setEmptyName().get(), true)
            .addFrame(ItemBuilder.of(Material.BLUE_STAINED_GLASS_PANE).setEmptyName().get(), 10)
            .addFrame(ItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE).setEmptyName().get(), 10)
            .addFrame(ItemBuilder.of(Material.MAGENTA_STAINED_GLASS_PANE).setEmptyName().get(), 10)
            .addFrame(ItemBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setEmptyName().get(), 10)
            .addFrame(ItemBuilder.of(Material.CYAN_STAINED_GLASS_PANE).setEmptyName().get(), 10)
            .addFrame(ItemBuilder.of(Material.PURPLE_STAINED_GLASS_PANE).setEmptyName().get(), 10);

    public GUICreator(SimpleStack plugin) {
        this.plugin = plugin;
    }

    @Override
    public GUIContainer get() {
        GUIContainer gui = new GUIContainer(plugin, Text.of("simplestack.gui.config.title"), 5);
        GUIAnimationModule animation = new GUIAnimationModule(plugin, 1);
        GUIAnimDecoratorModule outlineModule = new GUIAnimDecoratorModule(
            inRange(1, 1, 5, 1).or(inRange(1, 9, 5, 9)),
            ANIMATED_GUI_ITEM, new AnimationSpecification(Position.TOP_LEFT, Style.ROW));
        GUINavigatorModule naviModule = new GUINavigatorModule(plugin, "config");
        GUIConfigModule configModule = new GUIConfigModule(plugin);
        GUIDebugOpenerModule debugModule = new GUIDebugOpenerModule(plugin);
        gui.addModule(animation);
        gui.addModule(outlineModule);
        gui.addModule(naviModule);
        gui.addModule(configModule);
        gui.addModule(debugModule);
        GUIInteractHandler handler = new GUIInteractHandlerDefault(64);
        gui.setInteractionHandler(handler);

        return gui;
    }

    /**
     * Get the language list used in the Language Select GUI screen.
     *
     * @param plugin A reference to Simple Stack's plugin class
     * @return A list of GUIItems for each language
     */
    public static List<GUIItem> getLanguageList(SimpleStack plugin) {
        List<GUIItem> items = new ArrayList<>();
        Text clickMessage = Text.of("&f").concat(Text.of("simplestack.gui.language.language_select"));
        GUIItem english = new GUIItem(
            ItemBuilder.of(Base64Head.FLAG_UNITED_STATES.get())
                .setName("&bEnglish")
                .setLore(clickMessage, Text.of("&7en_us"))
                .get());
        english.addEvent(new GUISwitchLangEvent(plugin, "en_us"));

        GUIItem simplifiedChinese = new GUIItem(
            ItemBuilder.of(Base64Head.FLAG_CHINA.get())
                .setName("&b简体中文 (Simplified Chinese)")
                .setLore(clickMessage, Text.of("&7zh_cn"))
                .get());
        simplifiedChinese.addEvent(new GUISwitchLangEvent(plugin, "zh_cn"));

        GUIItem korean = new GUIItem(
            ItemBuilder.of(Base64Head.FLAG_SOUTH_KOREA.get())
                .setName("&b한국어 (Korean)")
                .setLore(clickMessage, Text.of("&7ko_kr"))
                .get());
        korean.addEvent(new GUISwitchLangEvent(plugin, "ko_kr"));

        GUIItem argentina = new GUIItem(
            ItemBuilder.of(Base64Head.FLAG_ARGENTINA.get())
                .setName("&bEspañol (Argentinian Spanish)")
                .setLore(clickMessage, Text.of("&7es_ar"))
                .get());
        argentina.addEvent(new GUISwitchLangEvent(plugin, "es_ar"));

        GUIItem chilean = new GUIItem(
            ItemBuilder.of(Base64Head.FLAG_CHILE.get())
                .setName("&bEspañol (Chilean Spanish)")
                .setLore(clickMessage, Text.of("&7es_cl"))
                .get());
        chilean.addEvent(new GUISwitchLangEvent(plugin, "es_cl"));

        GUIItem mexican = new GUIItem(
            ItemBuilder.of(Base64Head.FLAG_MEXICO.get())
                .setName("&bEspañol (Mexican Spanish)")
                .setLore(clickMessage, Text.of("&7es_mx"))
                .get());
        mexican.addEvent(new GUISwitchLangEvent(plugin, "es_mx"));

        GUIItem uruguay = new GUIItem(
            ItemBuilder.of(Base64Head.FLAG_URUGUAY.get())
                .setName("&bEspañol (Uruguayan Spanish)")
                .setLore(clickMessage, Text.of("&7es_uy"))
                .get());
        uruguay.addEvent(new GUISwitchLangEvent(plugin, "es_uy"));

        GUIItem venezuela = new GUIItem(
            ItemBuilder.of(Base64Head.FLAG_VENEZUELA.get())
                .setName("&bEspañol (Venezuelan Spanish)")
                .setLore(clickMessage, Text.of("&7es_ve"))
                .get());
        venezuela.addEvent(new GUISwitchLangEvent(plugin, "es_ve"));

        GUIItem german = new GUIItem(
            ItemBuilder.of(Base64Head.FLAG_GERMANY.get())
                .setName("&bDeutsch (German)")
                .setLore(clickMessage, Text.of("&7de_de"))
                .get());
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

        String currentLocale = plugin.config().getLangLocale();
        int index = 0;
        GUIItem curLangItem = null;
        String curLocale = null;
        for(int i = 0; i < items.size(); ++i) {
            GUIItem curItem = items.get(i);
            String locale = curItem.getEvent(GUISwitchLangEvent.class).getLocale();
            if(!locale.equals(currentLocale)) continue;
            index = i;
            curLangItem = curItem;
            curLocale = locale;
        }
        if(curLangItem == null) {
            curLangItem = items.get(0);
            curLocale = "en_us";
        }

        Text[] newLore = new Text[] {
            Text.of("&a").concat(Text.of("generic.enabled")),
            Text.of("&7" + curLocale)
        };

        ItemStack selectedItem = ItemBuilder.of(Base64Head.CONCRETE_LIME.get())
            .setName(curLangItem.getDisplayName())
            .setLore(newLore)
            .get();

        ItemStack selectedItem2 = ItemBuilder.of(curLangItem.get())
            .setLore(newLore)
            .get();

        AnimatedGUIItem newLangItem = new AnimatedGUIItem(selectedItem, true);
        newLangItem.setLore(newLore);

        newLangItem.addFrame(selectedItem, 10);
        newLangItem.addFrame(selectedItem2, 10);
        items.remove(index);
        items.add(index, newLangItem);
        return items;
    }
}
