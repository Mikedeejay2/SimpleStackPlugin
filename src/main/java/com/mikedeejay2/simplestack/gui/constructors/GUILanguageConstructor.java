package com.mikedeejay2.simplestack.gui.constructors;

import com.google.common.collect.ImmutableList;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.gui.events.GUISwitchLangEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class GUILanguageConstructor extends GUIAbstractListConstructor<GUIItem> {
    private static final Function<GUIItem, GUIItem> MAPPER = (item) -> item;
    public static final GUILanguageConstructor INSTANCE = new GUILanguageConstructor(SimpleStack.getInstance());

    private final List<GUIItem> languageItems;

    private GUILanguageConstructor(SimpleStack plugin) {
        super(plugin, Text.of("simplestack.gui.language.title"), 5, MAPPER, MAPPER);

        Text clickMessage = Text.of("&f").concat(Text.of("simplestack.gui.language.language_select"));
        this.languageItems = ImmutableList.of(
            new GUIItem(ItemBuilder.of(Base64Head.FLAG_UNITED_STATES.get())
                            .setName("&bEnglish").setLore(clickMessage, Text.of("&7en_us")))
                .addEvent(new GUISwitchLangEvent(plugin, "en_us")),
            new GUIItem(ItemBuilder.of(Base64Head.FLAG_CHINA.get())
                            .setName("&b简体中文 (Simplified Chinese)").setLore(clickMessage, Text.of("&7zh_cn")))
                .addEvent(new GUISwitchLangEvent(plugin, "zh_cn")),
            new GUIItem(ItemBuilder.of(Base64Head.FLAG_SOUTH_KOREA.get())
                            .setName("&b한국어 (Korean)").setLore(clickMessage, Text.of("&7ko_kr")))
                .addEvent(new GUISwitchLangEvent(plugin, "ko_kr")),
            new GUIItem(ItemBuilder.of(Base64Head.FLAG_ARGENTINA.get())
                            .setName("&bEspañol (Argentinian Spanish)").setLore(clickMessage, Text.of("&7es_ar")))
                .addEvent(new GUISwitchLangEvent(plugin, "es_ar")),
            new GUIItem(ItemBuilder.of(Base64Head.FLAG_CHILE.get())
                            .setName("&bEspañol (Chilean Spanish)").setLore(clickMessage, Text.of("&7es_cl")))
                .addEvent(new GUISwitchLangEvent(plugin, "es_cl")),
            new GUIItem(ItemBuilder.of(Base64Head.FLAG_MEXICO.get())
                            .setName("&bEspañol (Mexican Spanish)").setLore(clickMessage, Text.of("&7es_mx")))
                .addEvent(new GUISwitchLangEvent(plugin, "es_mx")),
            new GUIItem(ItemBuilder.of(Base64Head.FLAG_URUGUAY.get())
                            .setName("&bEspañol (Uruguayan Spanish)").setLore(clickMessage, Text.of("&7es_uy")))
                .addEvent(new GUISwitchLangEvent(plugin, "es_uy")),
            new GUIItem(ItemBuilder.of(Base64Head.FLAG_VENEZUELA.get())
                            .setName("&bEspañol (Venezuelan Spanish)").setLore(clickMessage, Text.of("&7es_ve")))
                .addEvent(new GUISwitchLangEvent(plugin, "es_ve")),
            new GUIItem(ItemBuilder.of(Base64Head.FLAG_GERMANY.get())
                            .setName("&bDeutsch (German)").setLore(clickMessage, Text.of("&7de_de")))
                .addEvent(new GUISwitchLangEvent(plugin, "de_de")));
    }

    @Override
    public GUIContainer get() {
        GUIContainer gui = super.get();
        GUIAnimationModule animModule = new GUIAnimationModule(plugin, 1);
        gui.addModule(animModule);
        return gui;
    }

    @Override
    protected Collection<GUIItem> getUnmappedList() {
        return getLanguageList(plugin);
    }

    /**
     * Get the language list used in the Language Select GUI screen.
     *
     * @param plugin A reference to Simple Stack's plugin class
     * @return A list of GUIItems for each language
     */
    public List<GUIItem> getLanguageList(SimpleStack plugin) {
        List<GUIItem> items = new ArrayList<>(languageItems);

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
            break;
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
