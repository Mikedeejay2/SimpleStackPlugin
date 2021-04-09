package com.mikedeejay2.simplestack.system.itemclick.processes.inventorytype;

import com.mikedeejay2.mikedeejay2lib.nms.merchant.NMS_Merchant;
import com.mikedeejay2.mikedeejay2lib.nms.xpcalc.NMS_XP;
import com.mikedeejay2.mikedeejay2lib.util.debug.DebugUtil;
import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.*;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Villager;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.*;

import java.util.List;

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

        List<ItemStack> ingredientList = recipe.getIngredients();
        ItemStack[] ingredients = new ItemStack[ingredientList.size()];
        for(int i = 0; i < ingredientList.size(); ++i)
        {
            ItemStack curItem = ingredientList.get(i);
            curItem.setAmount((int) (curItem.getAmount()));
            ingredients[i] = curItem;
        }

        boolean useMax = info.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY;
        int takeValue = 1;
        if(useMax)
        {
            int maxTake = Integer.MAX_VALUE;
            for(int i = 0; i < ingredients.length; ++i)
            {
                ItemStack curIngredient = ingredients[i];
                ItemStack curInput = inventory.getItem(i);
                System.out.println("curIngredient: " + curIngredient);
                System.out.println("curInput: " + curInput);
                if(curIngredient.getType() == Material.AIR) continue;
                if(curInput == null)
                {
                    maxTake = 0;
                    continue;
                }
                int ingAmt = curIngredient.getAmount();
                int inAmt = curInput.getAmount();
                int max = ingAmt == 0 || inAmt == 0 ? 0 : inAmt / ingAmt;
                maxTake = Math.min(maxTake, max);
            }
            maxTake = Math.min(usesLeft, maxTake);
            takeValue = MoveUtils.resultSlotShift(info, maxTake);
        }

        for(int i = 0; i < ingredients.length; ++i)
        {
            ItemStack curIngredient = ingredients[i];
            ItemStack curInput = inventory.getItem(i);
            if(curInput == null) continue;
            int ingAmt = curIngredient.getAmount();
            int takeAmt = ingAmt * takeValue;
            curInput.setAmount(curInput.getAmount() - takeAmt);
        }
        recipe.setUses(curUses + takeValue);

        System.out.println("Is: " + merchant + " a villager? " + isVillager);
        System.out.println("Is: " + merchant + " an abstract villager? " + isAbstractVillager);
        System.out.println("Take amount: " + takeValue);

        for(int i = 0; i < takeValue; ++i)
        {
            if(isVillager)
            {
//                nmsMerchant.forceTrade(aVillager, recipe, info.player, inventory);
                Villager villager = (Villager) aVillager;
//            nmsMerchant.setForcedExperience(info.player, villager.getVillagerExperience() + (recipe.getVillagerExperience() * takeValue));
//            nmsMerchant.forceTrade(aVillager, recipe, info.player, inventory);
                villager.setVillagerExperience(villager.getVillagerExperience() + recipe.getVillagerExperience());
                System.out.println("Villager XP: " + villager.getVillagerExperience());
                System.out.println("Villager level: " + villager.getVillagerLevel());
            }

            if(isAbstractVillager)
            {
                NMS_XP xp = info.plugin.getNMSHandler().getXP();
                int amount = xp.calculateXP(aVillager);
                xp.spawnXP(amount, aVillager.getLocation());
            }
        }

        aVillager.getWorld().playSound(aVillager.getLocation(), Sound.ENTITY_VILLAGER_YES, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        info.player.incrementStatistic(Statistic.TRADED_WITH_VILLAGER);
        info.player.incrementStatistic(Statistic.CRAFT_ITEM, result.getType(), takeValue * result.getAmount());
    }
}
