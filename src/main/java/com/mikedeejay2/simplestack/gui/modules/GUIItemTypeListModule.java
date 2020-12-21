package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * The <tt>GUIModule</tt> for the item type list.
 * This module is simply for saving the list upon close and
 * adding the blacklist / whitelis button.
 *
 * @author Mikedeejay2
 */
public class GUIItemTypeListModule extends GUIModule
{
    private final Simplestack plugin;

    public GUIItemTypeListModule(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Overridden <tt>onClose</tt> method that saves the modified list to the
     * config.
     *
     * @param player The player that closed the GUI
     * @param gui    The <tt>GUIContainer</tt> of the GUI
     */
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
