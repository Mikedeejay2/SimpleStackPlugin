package com.mikedeejay2.simplestack.config;

import com.mikedeejay2.mikedeejay2lib.config.ConfigFile.ValueType;
import com.mikedeejay2.mikedeejay2lib.text.PlaceholderFormatter;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.text.language.TranslationManager;
import com.mikedeejay2.mikedeejay2lib.util.structure.tuple.MutablePair;
import com.mikedeejay2.mikedeejay2lib.util.structure.tuple.Pair;
import com.mikedeejay2.simplestack.SimpleStack;
import com.mikedeejay2.simplestack.config.SimpleStackConfigImpl.MaterialAndAmount;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class SimpleStackConfigTypes {
    private static final SimpleStack plugin = SimpleStack.getInstance();

    static final ValueType<Boolean> WHITELIST_TYPE = ValueType.STRING
        .map(String::toLowerCase, StringUtils::capitalize) // Lowercase on load, Capitalize on save
        .bool("whitelist", "blacklist", value -> { // Whitelist is true, Blacklist is false
            plugin.sendWarning(Text.of("simplestack.warnings.invalid_list_mode").placeholder(
                PlaceholderFormatter.of("mode", value)));
            return false;
        });

    static final ValueType<List<Material>> MATERIAL_LIST_TYPE = ValueType.STRING_LIST
        .onSaveDo(list -> list.add("Example Item"))
        .map(list -> list.stream()
                 .filter(str -> !str.equals("Example Item")) // Filter out the example item
                 .map(str -> { // Map to a list of Materials
                     final Material mat = Material.matchMaterial(str);
                     if(mat == null)
                         plugin.sendWarning(Text.of("simplestack.warnings.invalid_material").placeholder(
                             PlaceholderFormatter.of("mat", str)));
                     return mat;
                 }).filter(Objects::nonNull) // Filter out null Materials
                 .collect(Collectors.toList()),
             list -> list.stream()
                 .map(Enum::toString) // Map to String name of Material
                 .collect(Collectors.toList()));

    static final ValueType<String> LOCALE_TYPE = ValueType.STRING
        .onLoadDo(TranslationManager.GLOBAL::setGlobalLocale); // Set the global locale on load

    static final ValueType<List<MaterialAndAmount>> ITEM_AMOUNTS_TYPE = ValueType.keyValues(ValueType.INTEGER).map(
        map -> map.entrySet().stream()
            .filter(entry -> !entry.getKey().equals("Example Item")) // Filter out the example item
            .filter(entry -> { // Filter out invalid max stack ranges
                if(entry.getValue() > 0 && entry.getValue() <= 64) return true;
                plugin.sendWarning(Text.of("simplestack.warnings.number_outside_of_range").placeholder(
                    PlaceholderFormatter.of("mat", entry.getKey())));
                return false;
            }).map(entry -> { // Map from (String, Integer) to (Material, Integer)
                final Material mat = Material.matchMaterial(entry.getKey());
                if(mat == null) {
                    plugin.sendWarning(Text.of("simplestack.warnings.invalid_material").placeholder(
                        PlaceholderFormatter.of("mat", entry.getKey())));
                    return null;
                }
                return new MutablePair<>(mat, entry.getValue());
            }).filter(Objects::nonNull) // Filter out null Materials
            .map(pair -> new MaterialAndAmount(pair.getKey(), pair.getValue())) // Map to a MaterialAndAmount
            .collect(Collectors.toList()),
        map -> map.stream()
            .map(mata -> new MutablePair<>(mata.getMaterial().toString(), mata.getAmount())) // Map to pairs of (String, Integer)
            .collect(Collectors.toMap(Pair::getKey, Pair::getValue)));

    static final ValueType<List<ItemStack>> UNIQUE_ITEM_LIST_TYPE = ValueType.ITEM_STACK_LIST
        .onLoadReplace(list -> list.stream().filter(item -> { // Filter all valid items
            if(item != null && !item.getType().isAir()) return true;
            plugin.sendWarning(Text.of("simplestack.warnings.invalid_unique_item"));
            return false;
        }).collect(Collectors.toList()));

    static final ValueType<Integer> MAX_AMOUNT_TYPE = ValueType.INTEGER.onLoadReplace(amount -> {
        if(amount > 64 || amount <= 0) {
            plugin.sendMessage(Text.of("simplestack.warnings.invalid_max_amount"));
            return 64;
        }
        return amount;
    });
}
