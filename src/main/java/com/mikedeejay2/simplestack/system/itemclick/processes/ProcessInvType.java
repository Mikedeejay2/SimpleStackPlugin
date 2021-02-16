package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.inventorytype.*;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProcessInvType implements ItemClickProcess
{
    protected final Simplestack plugin;

    protected Map<InventoryType, List<ItemClickProcess>> invTypeProcesses;

    public ProcessInvType(Simplestack plugin)
    {
        this.plugin = plugin;
        for(InventoryType type : InventoryType.values())
        {
            invTypeProcesses.put(type, new ArrayList<>());
        }
    }

    public void initDefault()
    {
        ItemClickProcess crafting = new ProcessCrafting();
        addProcess(InventoryType.CRAFTING, crafting);
        addProcess(InventoryType.WORKBENCH, crafting);
        addProcess(InventoryType.MERCHANT, new ProcessMerchant());
        addProcess(InventoryType.ANVIL, new ProcessAnvil());
        addProcess(InventoryType.SMITHING, new ProcessSmithing());
        addProcess(InventoryType.CARTOGRAPHY, new ProcessCartography());
        addProcess(InventoryType.GRINDSTONE, new ProcessGrindstone());
        addProcess(InventoryType.STONECUTTER, new ProcessStonecutter());
    }

    public ProcessInvType addProcess(InventoryType type, ItemClickProcess process)
    {
        invTypeProcesses.get(type).add(process);
        return this;
    }

    @Override
    public void invoke(ItemClickInfo info)
    {
        invTypeProcesses.get(info.invView.getType()).forEach(process -> process.invoke(info));
    }
}
