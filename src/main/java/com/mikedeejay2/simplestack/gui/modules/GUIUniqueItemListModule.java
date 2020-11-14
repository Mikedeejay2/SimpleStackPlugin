package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GUIUniqueItemListModule extends GUIModule
{
    private final Simplestack plugin;

    public GUIUniqueItemListModule(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onClickedTail(InventoryClickEvent event, GUIContainer gui)
    {
        GUIListModule list = gui.getModule(GUIListModule.class);
        List<GUIItem> listItems = list.getList();
        List<ItemStack> newItems = new ArrayList<>();
        for(GUIItem guiItem : listItems)
        {
            newItems.add(guiItem.getItemBase());
        }
        plugin.config().setUniqueItemList(newItems);
    }
}
