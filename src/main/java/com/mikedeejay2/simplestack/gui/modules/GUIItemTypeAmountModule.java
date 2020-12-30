package com.mikedeejay2.simplestack.gui.modules;

import com.mikedeejay2.mikedeejay2lib.gui.GUIContainer;
import com.mikedeejay2.mikedeejay2lib.gui.item.GUIItem;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIListModule;
import com.mikedeejay2.mikedeejay2lib.gui.modules.GUIModule;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The <tt>GUIModule</tt> for the item type amount list.
 * This module is simply for saving the list upon close
 *
 * @author Mikedeejay2
 */
public class GUIItemTypeAmountModule implements GUIModule
{
    private final Simplestack plugin;

    public GUIItemTypeAmountModule(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Overridden <tt>onClose</tt> method for saving the list
     * to the config
     *
     * @param player The player closing the GUI
     * @param gui    The <tt>GUIContainer</tt> of the GUI
     */
    @Override
    public void onClose(Player player, GUIContainer gui)
    {
        GUIListModule list = gui.getModule(GUIListModule.class);
        List<GUIItem> listItems = list.getList();
        Map<Material, Integer> newItems = new HashMap<>();
        for(GUIItem guiItem : listItems)
        {
            if(guiItem == null || guiItem.getItemBase() == null) continue;
            ItemStack item = guiItem.getItemBase();
            Material material = item.getType();
            int amount = item.getAmount();
            newItems.put(material, amount);
        }
        plugin.config().setItemAmounts(newItems);
    }
}
