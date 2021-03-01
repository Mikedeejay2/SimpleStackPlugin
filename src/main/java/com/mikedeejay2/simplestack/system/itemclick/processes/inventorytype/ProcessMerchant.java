package com.mikedeejay2.simplestack.system.itemclick.processes.inventorytype;

import com.mikedeejay2.mikedeejay2lib.nms.xpcalc.NMS_XP;
import com.mikedeejay2.mikedeejay2lib.util.debug.DebugUtil;
import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.Material;
import org.bukkit.Statistic;
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
        System.out.println(DebugUtil.getLineNumber());
        if(!InventoryIdentifiers.takeResult(info.getAction())) return;
        MerchantInventory inventory = (MerchantInventory) info.topInv;
        ItemStack result = inventory.getItem(2);
        System.out.println(DebugUtil.getLineNumber());
        if(result == null) return;
        Merchant merchant = inventory.getMerchant();
        MerchantRecipe recipe = inventory.getSelectedRecipe();
        System.out.println(DebugUtil.getLineNumber());
        if(recipe == null) return;
        System.out.println(DebugUtil.getLineNumber());
        int curUses = recipe.getUses();
        int maxUses = recipe.getMaxUses();
        int usesLeft = maxUses - curUses;
        float priceMultiplier = recipe.getPriceMultiplier();
        boolean isVillager = merchant instanceof Villager;
        boolean isAbstractVillager = merchant instanceof AbstractVillager;

        List<ItemStack> ingredientList = recipe.getIngredients();
        ItemStack[] ingredients = new ItemStack[ingredientList.size()];
        System.out.println(DebugUtil.getLineNumber());
        for(int i = 0; i < ingredientList.size(); ++i)
        {
            ItemStack curItem = ingredientList.get(i);
            curItem.setAmount((int) (curItem.getAmount() * (priceMultiplier + 1)));
            System.out.println("PriceMultiplier: " + priceMultiplier);
            ingredients[i] = curItem;
        }

        boolean useMax = info.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY;
        int takeValue = 1;
        System.out.println(DebugUtil.getLineNumber());
        if(useMax)
        {
            System.out.println(DebugUtil.getLineNumber());
            int maxTake = Integer.MAX_VALUE;
            for(int i = 0; i < ingredients.length; ++i)
            {
                System.out.println(DebugUtil.getLineNumber());
                ItemStack curIngredient = ingredients[i];
                ItemStack curInput = inventory.getItem(i);
                if(curInput == null)
                {
                    System.out.println(DebugUtil.getLineNumber());
                    maxTake = 0;
                    continue;
                }
                int ingAmt = curIngredient.getAmount();
                int inAmt = curInput.getAmount();
                int max = ingAmt == 0 || inAmt == 0 ? 0 : inAmt / ingAmt;
                maxTake = Math.min(maxTake, max);
            }
            System.out.println(DebugUtil.getLineNumber());
            maxTake = Math.min(usesLeft, maxTake);
            takeValue = MoveUtils.resultSlotShift(info, maxTake);
        }

        System.out.println(DebugUtil.getLineNumber());
        for(int i = 0; i < ingredients.length; ++i)
        {
            System.out.println(DebugUtil.getLineNumber());
            ItemStack curIngredient = ingredients[i];
            ItemStack curInput = inventory.getItem(i);
            if(curInput == null) continue;
            int ingAmt = curIngredient.getAmount();
            int takeAmt = ingAmt * takeValue;
            curInput.setAmount(curInput.getAmount() - takeAmt);
            System.out.println(DebugUtil.getLineNumber());
        }
        recipe.setUses(curUses + takeValue);

        System.out.println("Is: " + merchant + " a villager? " + isVillager);
        System.out.println("Is: " + merchant + " an abstract villager? " + isAbstractVillager);
        if(isVillager)
        {
            System.out.println(DebugUtil.getLineNumber());
            Villager villager = (Villager) merchant;
            int experience = villager.getVillagerExperience();
            int newXP = recipe.getVillagerExperience();
            villager.setVillagerExperience(experience + newXP);
        }

        if(isAbstractVillager)
        {
            System.out.println(DebugUtil.getLineNumber());
            NMS_XP xp = info.plugin.NMS().getXP();
            xp.calculateXP((AbstractVillager) merchant);
        }

        System.out.println(DebugUtil.getLineNumber());
        info.player.incrementStatistic(Statistic.TRADED_WITH_VILLAGER);
    }
}
