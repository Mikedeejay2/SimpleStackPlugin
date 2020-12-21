package com.mikedeejay2.simplestack.gui.events;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.event.GUIEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIListModule;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.gui.GUICreator;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

/**
 * A <tt>GUIEvent</tt> that changes the default language of Simple Stack to a new language
 * from the config.
 *
 * @author Mikedeejay2
 */
public class GUISwitchLangEvent implements GUIEvent
{
    private final Simplestack plugin;
    private String locale;

    public GUISwitchLangEvent(Simplestack plugin, String locale)
    {
        this.plugin = plugin;
        this.locale = locale;
    }

    @Override
    public void execute(InventoryClickEvent event, GUIContainer gui)
    {
        Player player = (Player) event.getWhoClicked();
        Config config = plugin.config();
        config.setLangLocale(locale);
        GUIListModule listModule = gui.getModule(GUIListModule.class);
        List<GUIItem> langItems  = GUICreator.getLanguageList(plugin, player);
        listModule.setGUIItems(langItems);
    }

    public String getLocale()
    {
        return locale;
    }
}
