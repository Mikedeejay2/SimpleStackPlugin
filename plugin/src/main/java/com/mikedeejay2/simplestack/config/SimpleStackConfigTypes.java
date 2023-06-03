package com.mikedeejay2.simplestack.config;

import com.mikedeejay2.mikedeejay2lib.config.ConfigFile.ValueType;
import com.mikedeejay2.mikedeejay2lib.text.Text;
import com.mikedeejay2.mikedeejay2lib.text.language.TranslationManager;
import com.mikedeejay2.simplestack.SimpleStack;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

final class SimpleStackConfigTypes {
    private static final SimpleStack plugin = SimpleStack.getInstance();

//    static final ValueType<Boolean> WHITELIST_TYPE = ValueType.STRING
//        .map(String::toLowerCase, StringUtils::capitalize) // Lowercase on load, Capitalize on save
//        .bool("whitelist", "blacklist", value -> { // Whitelist is true, Blacklist is false
//            plugin.sendWarning(Text.of("simplestack.warnings.invalid_list_mode").placeholder(
//                PlaceholderFormatter.of("mode", value)));
//            return false;
//        });

    static final ValueType<ConfigItemMap> ITEM_MAP_TYPE = ValueType.of(
        (accessor, name) -> ConfigItemMap.deserialize((List<Map<String, Object>>) accessor.get(name)),
        (accessor, name, value) -> accessor.set(name, value.serialize()));

    // TODO: REMOVE
//    static final ValueType<ReferenceSet<Material>> MATERIAL_LIST_TYPE = ValueType.STRING_LIST
//        .onSaveDo(list -> list.add(0, "Example Item"))
//        .map(list -> new ReferenceLinkedOpenHashSet<>(
//                 list.stream()
//                     .filter(str -> !str.equals("Example Item")) // Filter out the example item
//                     .map(str -> { // Map to a list of Materials
//                         final Material mat = Material.matchMaterial(str);
//                         if(mat == null)
//                             plugin.sendWarning(Text.of("simplestack.warnings.invalid_material").placeholder(
//                                 PlaceholderFormatter.of("mat", str)));
//                         return mat;
//                     }).filter(Objects::nonNull) // Filter out null Materials
//                     .collect(Collectors.toList())),
//             set -> set.stream()
//                 .map(Enum::toString) // Map to String name of Material
//                 .collect(Collectors.toList()));

    static final ValueType<String> LOCALE_TYPE = ValueType.STRING
        .onLoadDo(TranslationManager.GLOBAL::setGlobalLocale); // Set the global locale on load

    // TODO: REMOVE
//    static final ValueType<Reference2IntMap<Material>> ITEM_AMOUNTS_TYPE = ValueType.keyValues(ValueType.INTEGER).map(
//        map -> new Reference2IntLinkedOpenHashMap<>(
//            map.entrySet().stream()
//                .filter(entry -> !entry.getKey().equals("Example Item")) // Filter out the example item
//                .filter(entry -> { // Filter out invalid max stack ranges
//                    if(entry.getValue() > 0 && entry.getValue() <= 64) return true;
//                    plugin.sendWarning(Text.of("simplestack.warnings.number_outside_of_range").placeholder(
//                        PlaceholderFormatter.of("mat", entry.getKey())));
//                    return false;
//                }).map(entry -> { // Map from (String, Integer) to (Material, Integer)
//                    final Material mat = Material.matchMaterial(entry.getKey());
//                    if(mat == null) {
//                        plugin.sendWarning(Text.of("simplestack.warnings.invalid_material").placeholder(
//                            PlaceholderFormatter.of("mat", entry.getKey())));
//                        return null;
//                    }
//                    return new MutablePair<>(mat, entry.getValue());
//                }).filter(Objects::nonNull) // Filter out null Materials
//                .collect(toLinkedMap(Pair::getKey, Pair::getValue))),
//        map -> map.reference2IntEntrySet().stream()
//            .map(entry -> new MutablePair<>(entry.getKey().toString(), entry.getIntValue())) // Map to pairs of (String, Integer)
//            .collect(toLinkedMap(Pair::getKey, Pair::getValue)));

    // TODO: REMOVE
//    static final ValueType<Object2IntMap<ItemStack>> UNIQUE_ITEM_LIST_TYPE = ValueType.ITEM_STACK_LIST
//        .map(list -> new Object2IntLinkedOpenHashMap<>(
//                 list.stream().filter(item -> { // Filter all valid items
//                         if(item != null && !item.getType().isAir()) return true;
//                         plugin.sendWarning(Text.of("simplestack.warnings.invalid_unique_item"));
//                         return false;
//                     }).map(item -> new MutablePair<>(item, item.getAmount()))
//                     .collect(toLinkedMap(Pair::getKey, Pair::getValue))),
//             map -> map.object2IntEntrySet().stream()
//                 .map(Map.Entry::getKey)
//                 .collect(Collectors.toList()));

    static final ValueType<Integer> MAX_AMOUNT_TYPE = ValueType.INTEGER.onLoadReplace(amount -> {
        if(amount > 64 || amount <= 0) {
            plugin.sendMessage(Text.of("simplestack.warnings.invalid_max_amount"));
            return 64;
        }
        return amount;
    });

    public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedMap(
        Function<? super T, ? extends K> keyMapper,
        Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(keyMapper, valueMapper, (m1, m2) -> m1, LinkedHashMap::new);
    }
}
