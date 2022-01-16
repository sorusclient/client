package com.github.sorusclient.client.setting;

import com.github.sorusclient.client.adapter.Key;
import com.github.sorusclient.client.util.Color;

public abstract class ConfigurableData {

    public static class ConfigurableDataSingleSetting<T> extends ConfigurableData {

        private final String displayName;
        private final Setting<T> setting;

        public ConfigurableDataSingleSetting(String displayName, Setting<T> setting) {
            this.displayName = displayName;
            this.setting = setting;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public Setting<T> getSetting() {
            return this.setting;
        }

    }

    public static class Toggle extends ConfigurableDataSingleSetting<Boolean> {

        public Toggle(String displayName, Setting<Boolean> setting) {
            super(displayName, setting);
        }

    }

    public static class Slider extends ConfigurableDataSingleSetting<Number> {

        private final double minimum, maximum;

        public Slider(String displayName, Setting<? extends Number> setting, double minimum, double maximum) {
            super(displayName, (Setting<Number>) setting);
            this.minimum = minimum;
            this.maximum = maximum;
        }

        public double getMinimum() {
            return minimum;
        }

        public double getMaximum() {
            return maximum;
        }

    }

    public static class ClickThrough extends ConfigurableDataSingleSetting<Enum<?>> {

        public ClickThrough(String displayName, Setting<? extends Enum<?>> setting) {
            super(displayName, (Setting<Enum<?>>) setting);
        }

    }

    public static class KeyBind extends ConfigurableDataSingleSetting<Key> {

        public KeyBind(String displayName, Setting<Key> setting) {
            super(displayName, setting);
        }

    }

    public static class ColorPicker extends ConfigurableDataSingleSetting<Color> {

        public ColorPicker(String displayName, Setting<Color> setting) {
            super(displayName, setting);
        }

    }

    public static class Dependent extends ConfigurableData {

        private final ConfigurableData configurableData;
        private final Setting<?> setting;
        private final Object expectedValue;

        public <T> Dependent(ConfigurableData configurableData, Setting<T> setting, T expectedValue) {
            this.configurableData = configurableData;
            this.setting = setting;
            this.expectedValue = expectedValue;
        }

        public ConfigurableData getConfigurableData() {
            return configurableData;
        }

        public Setting<?> getSetting() {
            return setting;
        }

        public Object getExpectedValue() {
            return expectedValue;
        }

    }

}
