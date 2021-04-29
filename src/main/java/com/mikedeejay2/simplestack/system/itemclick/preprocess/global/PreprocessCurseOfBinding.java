package com.mikedeejay2.simplestack.system.itemclick.preprocess.global;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocess.ItemClickPreprocess;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryAction;

public class PreprocessCurseOfBinding implements ItemClickPreprocess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.selectedNull) return;
        if(info.player.getGameMode() == GameMode.CREATIVE) return;
        if(info.clickedInv != info.bottomInv) return;
        if(info.slot != InventoryIdentifiers.BOOTS_SLOT &&
           info.slot != InventoryIdentifiers.LEGGINGS_SLOT &&
           info.slot != InventoryIdentifiers.CHESTPLATE_SLOT &&
           info.slot != InventoryIdentifiers.HELMET_SLOT) return;
        if(!info.selected.hasItemMeta()) return;
        if(!info.selected.getItemMeta().hasEnchant(Enchantment.BINDING_CURSE)) return;
        info.setAction(InventoryAction.NOTHING);
    }
}
