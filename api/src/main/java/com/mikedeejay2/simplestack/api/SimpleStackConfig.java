package com.mikedeejay2.simplestack.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * Simple Stack's configuration. Can be used to retrieve data or update the config via the API.
 *
 * @author Mikedeejay2
 * @since 2.0.0
 */
public interface SimpleStackConfig {
    int getAmount(@NotNull Material type);
    int getAmount(@NotNull ItemStack item);

    boolean isStackedArmorWearable();
    void setStackedArmorWearable(boolean stackedArmorWearable);

    boolean overrideDefaultStackSizes();
    void setOverrideDefaultStackSizes(boolean overrideDefaultStackSizes);
    int getMaxStackOverride();
    void setMaxStackOverride(int maxStackOverride);

    @NotNull String getLocale();
    void setLocale(@NotNull String newLocale);

    boolean isModified();
}
