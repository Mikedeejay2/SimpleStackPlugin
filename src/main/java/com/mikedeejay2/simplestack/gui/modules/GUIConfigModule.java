package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.GUILayer;
import com.mikedeejay2.mikedeejay2lib.gui.event.GUIEvent;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemCreator;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.gui.events.ModifyMaxStackEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GUIConfigModule extends GUIModule
{
    private final Simplestack plugin;

    public GUIConfigModule(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onOpenHead(Player player, GUIContainer gui)
    {
        GUILayer layer = gui.getLayer(0);
        GUIItem itemTypeList = new GUIItem(ItemCreator.createItem(Material.PISTON, 1, "Item Type List"));
        GUIItem uniqueItemList = new GUIItem(ItemCreator.createItem(Material.CYAN_CONCRETE_POWDER, 1, "Unique Item List"));
        GUIItem language = new GUIItem(ItemCreator.createItem(Material.BLUE_CONCRETE, 1, "Default Language"));
        GUIItem defaultMaxAmount = new GUIItem(ItemCreator.createItem(Material.RED_CONCRETE, plugin.config().getMaxAmount(),
                "Default Max Amount",
                "Sets the default maximum stack amount",
                " for ALL items in Minecraft"));
        GUIEvent maxAmountEvent = new ModifyMaxStackEvent(plugin);
        defaultMaxAmount.addEvent(maxAmountEvent);

        layer.setItem(3, 4, itemTypeList);
        layer.setItem(3, 5, uniqueItemList);
        layer.setItem(4, 5, language);
        layer.setItem(3, 6, defaultMaxAmount);
    }
}
