package com.mikedeejay2.simplestack.gui.constructors;

import com.google.common.collect.ImmutableList;
import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.event.GUIEventInfo;
import com.mikedeejay2.mikedeejay2lib.gui.event.sound.GUIPlaySoundEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.AnimatedGUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.animation.GUIAnimationModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.list.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.item.ItemBuilder;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Head;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackAPI;
import com.mikedeejay2.simplestack.api.SimpleStackConfig;
import com.mikedeejay2.simplestack.config.SimpleStackConfigImpl;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GUILanguageConstructor extends GUIAbstractListConstructor<GUIItem> {
    private static final Function<GUIItem, GUIItem> MAPPER = (item) -> item;
    public static final GUILanguageConstructor INSTANCE = new GUILanguageConstructor(SimpleStack.getInstance());

    private static final Text CLICK_MESSAGE = Text.of("&f").concat(Text.of("simplestack.gui.language.language_select"));
    private static final List<GUIItem> languageItems = ImmutableList.of(
        new GUIItem(ItemBuilder.of(Base64Head.FLAG_UNITED_STATES.get())
                        .setName("&bEnglish").setLore(CLICK_MESSAGE, Text.of("&7en_us")))
            .addEvent(new GUISwitchLangEvent("en_us")),
        new GUIItem(ItemBuilder.of(Base64Head.FLAG_CHINA.get())
                        .setName("&b简体中文 (Simplified Chinese)").setLore(CLICK_MESSAGE, Text.of("&7zh_cn")))
            .addEvent(new GUISwitchLangEvent("zh_cn")),
        new GUIItem(ItemBuilder.of(Base64Head.FLAG_SOUTH_KOREA.get())
                        .setName("&b한국어 (Korean)").setLore(CLICK_MESSAGE, Text.of("&7ko_kr")))
            .addEvent(new GUISwitchLangEvent("ko_kr")),
        new GUIItem(ItemBuilder.of(Base64Head.FLAG_ARGENTINA.get())
                        .setName("&bEspañol (Argentinian Spanish)").setLore(CLICK_MESSAGE, Text.of("&7es_ar")))
            .addEvent(new GUISwitchLangEvent("es_ar")),
        new GUIItem(ItemBuilder.of(Base64Head.FLAG_CHILE.get())
                        .setName("&bEspañol (Chilean Spanish)").setLore(CLICK_MESSAGE, Text.of("&7es_cl")))
            .addEvent(new GUISwitchLangEvent("es_cl")),
        new GUIItem(ItemBuilder.of(Base64Head.FLAG_MEXICO.get())
                        .setName("&bEspañol (Mexican Spanish)").setLore(CLICK_MESSAGE, Text.of("&7es_mx")))
            .addEvent(new GUISwitchLangEvent("es_mx")),
        new GUIItem(ItemBuilder.of(Base64Head.FLAG_URUGUAY.get())
                        .setName("&bEspañol (Uruguayan Spanish)").setLore(CLICK_MESSAGE, Text.of("&7es_uy")))
            .addEvent(new GUISwitchLangEvent("es_uy")),
        new GUIItem(ItemBuilder.of(Base64Head.FLAG_VENEZUELA.get())
                        .setName("&bEspañol (Venezuelan Spanish)").setLore(CLICK_MESSAGE, Text.of("&7es_ve")))
            .addEvent(new GUISwitchLangEvent("es_ve")),
        new GUIItem(ItemBuilder.of(Base64Head.FLAG_GERMANY.get())
                        .setName("&bDeutsch (German)").setLore(CLICK_MESSAGE, Text.of("&7de_de")))
            .addEvent(new GUISwitchLangEvent("de_de")));

    private GUILanguageConstructor(SimpleStack plugin) {
        super(plugin, Text.of("simplestack.gui.language.title"), 5, MAPPER, null);
    }

    @Override
    public GUIContainer get() {
        GUIContainer gui = super.get();
        GUIAnimationModule animModule = new GUIAnimationModule(plugin, 1);
        gui.addModule(animModule);
        return gui;
    }

    @Override
    protected List<GUIItem> getUnmappedList() {
        return getLanguageList();
    }

    /**
     * Get the language list used in the Language Select GUI screen.
     *
     * @return A list of GUIItems for each language
     */
    public List<GUIItem> getLanguageList() {
        List<GUIItem> items = new ArrayList<>(languageItems);

        String currentLocale = SimpleStackAPI.getConfig().getLocale();
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


    /**
     * A <tt>GUIEvent</tt> that changes the default language of Simple Stack to a new language
     * from the config.
     *
     * @author Mikedeejay2
     */
    public static final class GUISwitchLangEvent extends GUIPlaySoundEvent {
        private final String locale;

        public GUISwitchLangEvent(String locale) {
            super(Sound.UI_BUTTON_CLICK, 0.3f, 1f);
            this.locale = locale;
        }

        @Override
        protected void executeClick(GUIEventInfo info) {
            SimpleStackConfig config = SimpleStackAPI.getConfig();
            config.setLocale(locale);
            GUIListModule listModule = info.getGUI().getModule(GUIListModule.class);
            List<GUIItem> langItems = GUILanguageConstructor.INSTANCE.getLanguageList();
            listModule.setGUIItems(langItems);
            super.executeClick(info);
        }

        public String getLocale() {
            return locale;
        }
    }
}
