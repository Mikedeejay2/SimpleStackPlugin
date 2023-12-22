package com.mikedeejay2.simplestack.config;

import com.mikedeejay2.mikedeejay2lib.BukkitPlugin;
import com.mikedeejay2.mikedeejay2lib.config.ConfigFile;
import com.mikedeejay2.mikedeejay2lib.data.FileType;
import com.mikedeejay2.mikedeejay2lib.text.language.TranslationManager;
import com.mikedeejay2.mikedeejay2lib.util.debug.CrashReportSection;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.api.SimpleStackConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.mikedeejay2.simplestack.config.SimpleStackConfigTypes.*;

/**
 * Config class for holding all configuration values for Simple Stack and
 * managing file saving / loading.
 *
 * @author Mikedeejay2
 */
public class SimpleStackConfigImpl extends ConfigFile implements SimpleStackConfig {
    private final ItemsFile itemsFile = child(new ItemsFile(plugin));

    // Localization code specified in the config
    private final ConfigValue<String> locale = value(LOCALE_TYPE, "Language");
    // Whether the override the default stack sizes with the Max Stack Override
    private final ConfigValueBoolean overrideDefaultStackSizes = valueBoolean(ValueType.BOOLEAN, "Override Default Stack Sizes");
    // The max amount for all items in minecraft
    private final ConfigValueInteger maxStackOverride = valueInteger(MAX_AMOUNT_TYPE, "Max Stack Override");
    // Whether stacked armor can be worn or not
    private final ConfigValueBoolean stackedArmorWearable = valueBoolean(ValueType.BOOLEAN, "Stacked Armor Wearable");

    private final ConfigValue<ConfigItemMap> itemMap = itemsFile.itemMap;

    public SimpleStackConfigImpl(SimpleStack plugin) {
        super(plugin, "config.yml", FileType.YAML, true);

        // Remove or relocate legacy config values
        updater.relocate("Items", "Item Types")
            .relocate("ListMode", "List Mode")
            .remove("Hopper Movement Checks")
            .remove("Ground Stacking Checks")
            .remove("Creative Item Dragging");
    }

    @Override
    protected boolean internalLoadFromJar() {
        boolean success = super.internalLoadFromJar();
        if(success) setLocale(TranslationManager.SYSTEM_LOCALE);
        return success;
    }

    public List<ItemConfigValue> getItemsRef() {
        return itemMap.get().getList();
    }

    public ConfigItemMap getItemMap() {
        return itemMap.get();
    }

    @Override
    public int getAmount(@NotNull ItemStack item) {
        return itemMap.get().getItemStack(item);
    }

    @Override
    public int getAmount(@NotNull Material type) {
        final int amount = itemMap.get().getMaterial(type);
        if(amount == -1 && overrideDefaultStackSizes()) {
            return getMaxStackOverride();
        }
        return amount;
    }

    public void addItem(ItemConfigValue value) {
        itemMap.get().addItem(value);
        setItemsModified(true);
    }

    public void removeItem(ItemConfigValue value) {
        itemMap.get().removeItem(value);
        setItemsModified(true);
    }

    public boolean containsItem(ItemConfigValue value) {
        return itemMap.get().containsItem(value);
    }

    @Override
    public boolean isStackedArmorWearable() {
        return stackedArmorWearable.getBoolean();
    }

    @Override
    public void setStackedArmorWearable(boolean stackedArmorWearable) {
        this.stackedArmorWearable.setBoolean(stackedArmorWearable);
        setModified(true);
    }

    @Override
    public boolean overrideDefaultStackSizes() {
        return overrideDefaultStackSizes.getBoolean();
    }

    @Override
    public void setOverrideDefaultStackSizes(boolean overrideDefaultStackSizes) {
        this.overrideDefaultStackSizes.setBoolean(overrideDefaultStackSizes);
        setModified(true);
    }

    @Override
    public int getMaxStackOverride() {
        return maxStackOverride.getInteger();
    }

    @Override
    public void setMaxStackOverride(int maxStackOverride) {
        this.maxStackOverride.setInteger(maxStackOverride);
        setModified(true);
    }

    @Override
    public @NotNull String getLocale() {
        return locale.get();
    }

    @Override
    public void setLocale(@NotNull String newLocale) {
        this.locale.set(newLocale);
        TranslationManager.GLOBAL.setGlobalLocale(newLocale);
        setModified(true);
    }

    public void setItemsModified(boolean modified) {
        this.itemsFile.setModified(modified);
    }

    public void fillCrashReportSection(CrashReportSection section) {
        fillCrashReportSection_(section, this);
        section.addDetail("Items", itemMap.get().fillCrashReportSection());
        section.addDetail("Is Config Loaded", String.valueOf(isLoaded()));
        section.addDetail("Is Config Modified", String.valueOf(isModified()));
    }

    private void fillCrashReportSection_(CrashReportSection section, ConfigFile file) {
        for(ConfigValue<?> value : file.getValues()) {
            section.addDetail(value.getPath(), String.valueOf(value.get()));
        }
    }

    private static final class ItemsFile extends ConfigFile {
        private final ConfigValue<ConfigItemMap> itemMap = value(ITEM_MAP_TYPE, "items", new ConfigItemMap());

        public ItemsFile(BukkitPlugin plugin) {
            super(plugin, "items.yml", FileType.YAML, false);
//            updater.convert("unique_items.json", "unique_items.yml", (oldAccessor, newAccessor) -> { // TODO: Update conversion
//                newAccessor.setItemStackList("items", oldAccessor.getItemStackList("items"));
//            }).rename("unique_items.json", "unique_items_old.json");
        }
    }
}
