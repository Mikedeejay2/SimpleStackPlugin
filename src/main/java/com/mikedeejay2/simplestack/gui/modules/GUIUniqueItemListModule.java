package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The <tt>GUIModule</tt> for the unique items list.
 * This module is simply for saving the list upon close
 *
 * @author Mikedeejay2
 */
public class GUIUniqueItemListModule extends GUIModule
{
    private final Simplestack plugin;

    public GUIUniqueItemListModule(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Overridden <tt>onClose</tt> method for saving the unique items list
     * to the config after it's been modified
     *
     * @param player The player that closed the GUI
     * @param gui    The GUIContainer
     */
    @Override
    public void onClose(Player player, GUIContainer gui)
    {
        GUIListModule   list      = gui.getModule(GUIListModule.class);
        List<GUIItem>   listItems = list.getList();
        List<ItemStack> newItems  = new ArrayList<>();
        for(GUIItem guiItem : listItems)
        {
            newItems.add(guiItem.getItemBase());
        }
        plugin.config().setUniqueItemList(newItems);
    }
}
