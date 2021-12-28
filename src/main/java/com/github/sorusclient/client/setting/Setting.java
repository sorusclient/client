package com.github.sorusclient.client.setting;

public class Setting<T> {

    private final Class<T> type;
    private final T defaultValue;
    private T value;

    @SuppressWarnings("unchecked")
    public Setting(T defaultValue) {
        this((Class<T>) defaultValue.getClass(), defaultValue);
    }

    public <U extends T> Setting(Class<T> clazz, U defaultValue) {
        this.type = clazz;
        this.value = this.defaultValue = defaultValue;
    }

    public Class<T> getType() {
        return type;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public void setValueRaw(Object value) {
        this.value = (T) value;
    }

    public T getValue() {
        return value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

}
