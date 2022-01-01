package com.github.sorusclient.client.setting;

public class Setting<T> {

    private final Class<T> type;
    private T value;
    private T forcedValue = null;

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

    public void setForcedValue(T forcedValue) {
        this.forcedValue = forcedValue;
    }

    public void setForcedValueRaw(Object forcedValue) {
        this.forcedValue = (T) forcedValue;
    }

    public boolean isForcedValue() {
        return this.forcedValue != null;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public void setValueRaw(Object value) {
        this.value = (T) value;
    }

    public T getValue() {
        return this.forcedValue != null ? this.forcedValue : this.value;
    }

    public T getRealValue() {
        return this.value;
    }

}
