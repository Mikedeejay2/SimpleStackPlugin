package com.mikedeejay2.simplestack.system.itemclick.processes.inventorytype;

import com.mikedeejay2.mikedeejay2lib.nms.merchant.NMS_Merchant;
import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.*;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.*;

public class ProcessMerchant implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.rawSlot != 2) return;
        /* DEBUG */ System.out.println("Process Merchant");
        if(!InventoryIdentifiers.takeResult(info.getAction())) return;
        MerchantInventory inventory = (MerchantInventory) info.topInv;
        ItemStack result = inventory.getItem(2);
        if(result == null) return;
        Merchant merchant = inventory.getMerchant();
        MerchantRecipe recipe = inventory.getSelectedRecipe();
        if(recipe == null) return;
        int curUses = recipe.getUses();
        int maxUses = recipe.getMaxUses();
        int usesLeft = maxUses - curUses;
        NMS_Merchant nmsMerchant = info.plugin.getNMSHandler().getMerchant();
        AbstractVillager aVillager = nmsMerchant.getVillager(merchant);
        boolean isVillager = aVillager instanceof Villager;
        boolean isAbstractVillager = aVillager != null;

        ItemStack ingredient1 = nmsMerchant.getBuyItem1(recipe);
        ItemStack ingredient2 = nmsMerchant.getBuyItem2(recipe);
        ItemStack input1 = inventory.getItem(0);
        ItemStack input2 = inventory.getItem(1);

        boolean useMax = info.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY;
        int takeValue = 1;
        if(useMax)
        {
            int maxTake = Integer.MAX_VALUE;

            System.out.println("curInput: " + input1);
            if(ingredient1.getType() != Material.AIR &&
               input1 != null &&
               ingredient1.getAmount() != 0 &&
               input1.getAmount() != 0)
            {
                maxTake = Math.min(maxTake, input1.getAmount() / ingredient1.getAmount());
            }
            else if(ingredient2.getType() != Material.AIR &&
                    input2 != null &&
                    ingredient2.getAmount() != 0 &&
                    input2.getAmount() != 0)
            {
                maxTake = Math.min(maxTake, input2.getAmount() / ingredient2.getAmount());
            }
            else
            {
                maxTake = 0;
            }

            maxTake = Math.min(usesLeft, maxTake);
            takeValue = MoveUtils.resultSlotShift(info, maxTake);
        }

        if(input1 != null)
        {
            input1.setAmount(input1.getAmount() - (ingredient1.getAmount() * takeValue));
        }
        if(input2 != null)
        {
            input2.setAmount(input2.getAmount() - (ingredient2.getAmount() * takeValue));
        }
        recipe.setUses(curUses + takeValue);

        System.out.println("Is: " + merchant + " a villager? " + isVillager);
        System.out.println("Is: " + merchant + " an abstract villager? " + isAbstractVillager);
        System.out.println("Take amount: " + takeValue);

        for(int i = 0; i < takeValue; ++i)
        {
            if(isVillager)
            {
                Villager villager = (Villager) aVillager;
                villager.setVillagerExperience(villager.getVillagerExperience() + recipe.getVillagerExperience());
                nmsMerchant.postProcess(villager, recipe);
                System.out.println("Villager XP: " + villager.getVillagerExperience());
                System.out.println("Villager level: " + villager.getVillagerLevel());
            }
        }

        aVillager.getWorld().playSound(aVillager.getLocation(), Sound.ENTITY_VILLAGER_YES, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        info.player.incrementStatistic(Statistic.TRADED_WITH_VILLAGER);
        info.player.incrementStatistic(Statistic.CRAFT_ITEM, result.getType(), takeValue * result.getAmount());
    }
}
