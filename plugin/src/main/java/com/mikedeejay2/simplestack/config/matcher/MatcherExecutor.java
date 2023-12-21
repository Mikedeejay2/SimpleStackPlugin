package com.mikedeejay2.simplestack.config.matcher;

import com.mikedeejay2.simplestack.api.MatcherDataType;
import com.mikedeejay2.simplestack.api.MatcherOperatorType;
import org.apache.commons.lang3.Validate;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class MatcherExecutor<I extends ItemMeta, T> {
    private final MatcherDataSource<I, T> dataSource;
    private final MatcherOperator<T> operator;
    private final Class<I> itemMetaClass;
    private final Class<T> dataTypeClass;

    public MatcherExecutor(MatcherDataSource<I, T> dataSource, MatcherOperator<T> operator) {
        this.dataSource = dataSource;
        this.operator = operator;
        this.itemMetaClass = dataSource.getItemMetaClass();
        this.dataTypeClass = dataSource.getDataTypeClass();
    }

    public MatcherExecutor(MatcherDataSource<I, ?> dataSource, MatcherOperator<?> operator, Class<T> dataTypeClass) {
        Validate.isTrue(operator.getInputClass().isAssignableFrom(dataTypeClass));
        Validate.isTrue(dataSource.getDataTypeClass().isAssignableFrom(dataTypeClass));
        this.dataSource = (MatcherDataSource<I, T>) dataSource;
        this.operator = (MatcherOperator<T>) operator;
        this.itemMetaClass = this.dataSource.getItemMetaClass();
        this.dataTypeClass = this.dataSource.getDataTypeClass();
    }

    public boolean check(ItemMeta itemMeta, ItemMeta propertiesMeta) {
        // TODO: Is null ItemMeta check needed? Might already be covered
        if(itemMeta == null || !itemMetaClass.isAssignableFrom(itemMeta.getClass())) return false;
        final I castedPropertiesMeta = itemMetaClass.cast(propertiesMeta);
        final I castedItemMeta = itemMetaClass.cast(itemMeta);
        final T propertiesData = dataSource.getData(castedPropertiesMeta);
        final T itemData = dataSource.getData(castedItemMeta);
        return operator.match(itemData, propertiesData);
    }

    public MatcherDataSource<I, T> getDataSource() {
        return dataSource;
    }

    public MatcherOperator<T> getOperator() {
        return operator;
    }

    public MatcherDataType getDataType() {
        return dataSource.getDataType();
    }

    public MatcherOperatorType getOperatorType() {
        return operator.getOperatorType();
    }

    public Class<I> getItemMetaClass() {
        return itemMetaClass;
    }

    public Class<T> getDataTypeClass() {
        return dataTypeClass;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        MatcherExecutor<?, ?> that = (MatcherExecutor<?, ?>) o;
        return Objects.equals(dataSource, that.dataSource) && Objects.equals(operator, that.operator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataSource, operator);
    }

    @Override
    public String toString() {
        return dataSource.getDataType().name() + ": " + getOperatorType().name();
    }
}
