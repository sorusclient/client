package com.github.sorusclient.client.setting;

import java.util.List;

public class Setting<T> {

    private final Class<T> type;
    private T value;
    private List<T> forcedValues;

    @SuppressWarnings("unchecked")
    public Setting(T defaultValue) {
        this((Class<T>) defaultValue.getClass(), defaultValue);
    }

    public <U extends T> Setting(Class<T> clazz, U defaultValue) {
        this.type = clazz;
        this.value = defaultValue;
    }

    public Class<T> getType() {
        return this.type;
    }

    public void setForcedValueRaw(List<Object> forcedValues) {
        this.forcedValues = (List<T>) forcedValues;
    }

    public boolean isForcedValue() {
        return this.forcedValues != null;
    }

    public List<T> getForcedValues() {
        return forcedValues;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public void setValueRaw(Object value) {
        this.value = (T) value;
    }

    public T getValue() {
        if (this.forcedValues != null) {
            return this.forcedValues.contains(this.value) ? this.value : this.forcedValues.get(0);
        } else {
            return this.value;
        }
    }

    public T getRealValue() {
        return this.value;
    }

}
