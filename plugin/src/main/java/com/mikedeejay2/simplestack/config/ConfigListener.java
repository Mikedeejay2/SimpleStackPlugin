package com.mikedeejay2.simplestack.config;

import com.mikedeejay2.simplestack.api.SimpleStackConfig;
import com.mikedeejay2.simplestack.api.event.ArmorSlotMaxAmountEvent;
import com.mikedeejay2.simplestack.api.event.ItemStackMaxAmountEvent;
import com.mikedeejay2.simplestack.api.event.MaterialMaxAmountEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ConfigListener implements Listener {
    private final SimpleStackConfig config;

    public ConfigListener(SimpleStackConfig config) {
        this.config = config;
    }

    @EventHandler
    private void onMaterial(MaterialMaxAmountEvent event) {
        final int amount = config.getAmount(event.getType());
        if(amount != -1) {
            event.setAmount(amount);
            return;
        }
        if(config.overrideDefaultStackSizes()) {
            event.setAmount(config.getMaxStackOverride());
        }
    }

    @EventHandler
    private void onItemStack(ItemStackMaxAmountEvent event) {
        final int amount = config.getAmount(event.getItemStack());
        if(amount != -1) {
            event.setAmount(amount);
        }
    }

    @EventHandler
    private void onArmorSlot(ArmorSlotMaxAmountEvent event) {
        if(config.isStackedArmorWearable()) {
            event.setAmount(config.overrideDefaultStackSizes() ? config.getMaxStackOverride() : 64);
        }
    }
}
