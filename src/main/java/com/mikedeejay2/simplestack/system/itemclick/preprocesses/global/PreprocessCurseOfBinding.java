package com.mikedeejay2.simplestack.system.itemclick.preprocesses.global;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocesses.ItemClickPreprocess;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class PreprocessCurseOfBinding implements ItemClickPreprocess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.player.getGameMode() == GameMode.CREATIVE) return;
        if(info.clickedInv != info.bottomInv) return;
        if(info.slot != InventoryIdentifiers.BOOTS_SLOT &&
           info.slot != InventoryIdentifiers.LEGGINGS_SLOT &&
           info.slot != InventoryIdentifiers.CHESTPLATE_SLOT &&
           info.slot != InventoryIdentifiers.HELMET_SLOT) return;
        ItemStack item = info.selected;
        if(item == null || !item.hasItemMeta()) return;
        if(!item.getItemMeta().hasEnchant(Enchantment.BINDING_CURSE)) return;
        info.setAction(InventoryAction.NOTHING);
    }
}
