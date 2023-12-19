package com.mikedeejay2.simplestack.config.matcher;

import com.mikedeejay2.simplestack.api.MatcherDataType;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Function;

public class MatcherDataSource<I extends ItemMeta, T> {
    private final MatcherDataType dataType;
    private final String nameKey;
    private final List<String> descriptionKeys;
    private final Function<I, T> getterFunction;
    private final Class<I> itemMetaClass;
    private final Class<T> dataTypeClass;

    public MatcherDataSource(MatcherDataType dataType, String nameKey, Class<I> metaClass, Class<T> dataTypeClass, Function<I, T> getterFunction, String... descriptionKeys) {
        this.dataType = dataType;
        this.nameKey = "simplestack.config.matcher_data_source." + nameKey;
        this.descriptionKeys = new ArrayList<>();
        Collections.addAll(this.descriptionKeys, descriptionKeys);
        this.getterFunction = getterFunction;
        this.itemMetaClass = metaClass;
        this.dataTypeClass = dataTypeClass;
        MatcherDataSourceRegistry.REGISTRY_BY_CLASS.put(this.itemMetaClass, this);
        MatcherDataSourceRegistry.REGISTRY_BY_TYPE.put(dataType, this);
    }

    public MatcherDataSource(MatcherDataType dataType, Class<I> metaClass, Class<T> dataTypeClass, Function<I, T> getterFunction, String... descriptionKeys) {
        this(dataType, dataType.name().toLowerCase(), metaClass, dataTypeClass, getterFunction, descriptionKeys);
    }

    public T getData(I meta) {
        return getterFunction.apply(meta);
    }

    public MatcherDataType getDataType() {
        return dataType;
    }

    public String getNameKey() {
        return nameKey;
    }

    public List<String> getDescriptionKeys() {
        return descriptionKeys;
    }

    public Class<I> getItemMetaClass() {
        return itemMetaClass;
    }

    public Class<T> getDataTypeClass() {
        return dataTypeClass;
    }
}
