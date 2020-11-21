package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.mikedeejay2lib.util.head.Base64Heads;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemCreator;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.config.ListMode;
import com.mikedeejay2.simplestack.gui.events.GUISwitchListModeEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GUIItemTypeListModule extends GUIModule
{
    private final Simplestack plugin;

    public GUIItemTypeListModule(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onOpenHead(Player player, GUIContainer gui)
    {
        GUIItem switchListMode = new GUIItem(null);
        if(plugin.config().getListMode() == ListMode.BLACKLIST)
        {
            switchListMode.setItem(ItemCreator.createHeadItem(Base64Heads.X_BLACK, 1,
                    "&b&l" + plugin.langManager().getText(player, "simplestack.list_type.blacklist"),
                    "&7" + plugin.langManager().getText(player, "simplestack.gui.item_types.change_mode_whitelist"),
                    "",
                    "&a&l⊳ " + plugin.langManager().getText(player, "simplestack.list_type.blacklist"),
                    "&7  " + plugin.langManager().getText(player, "simplestack.list_type.whitelist")));
        }
        else
        {
            switchListMode.setItem(ItemCreator.createHeadItem(Base64Heads.CHECKMARK_WHITE, 1,
                    "&b&l" + plugin.langManager().getText(player, "simplestack.list_type.whitelist"),
                    "&7" + plugin.langManager().getText(player, "simplestack.gui.item_types.change_mode_blacklist"),
                    "",
                    "&7  " + plugin.langManager().getText(player, "simplestack.list_type.blacklist"),
                    "&a&l⊳ " + plugin.langManager().getText(player, "simplestack.list_type.whitelist")));
        }
        switchListMode.addEvent(new GUISwitchListModeEvent(plugin));
        GUILayer layer = gui.getLayer(0);
        layer.setItem(1, 8, switchListMode);
    }

    @Override
    public void onClose(Player player, GUIContainer gui)
    {
        GUIListModule list = gui.getModule(GUIListModule.class);
        List<GUIItem> listItems = list.getList();
        List<Material> newItems = new ArrayList<>();
        for(GUIItem guiItem : listItems)
        {
            newItems.add(guiItem.getItemBase().getType());
        }
        plugin.config().setMaterialList(newItems);
    }
}
