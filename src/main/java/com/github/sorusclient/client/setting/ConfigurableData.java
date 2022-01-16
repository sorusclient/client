package com.github.sorusclient.client.setting;

public abstract class ConfigurableData {

    private final String displayName;
    private final Setting<?> setting;

    public ConfigurableData(String displayName, Setting<?> setting) {
        this.displayName = displayName;
        this.setting = setting;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Setting<?> getSetting() {
        return setting;
    }

    public static class Toggle extends ConfigurableData {

        public Toggle(String displayName, Setting<?> setting) {
            super(displayName, setting);
        }

    }

    public static class Slider extends ConfigurableData {

        private final double minimum, maximum;

        public Slider(String displayName, Setting<?> setting, double minimum, double maximum) {
            super(displayName, setting);
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

    public static class ClickThrough extends ConfigurableData {

        public ClickThrough(String displayName, Setting<?> setting) {
            super(displayName, setting);
        }

    }

    public static class KeyBind extends ConfigurableData {

        public KeyBind(String displayName, Setting<?> setting) {
            super(displayName, setting);
        }

    }

    public static class ColorPicker extends ConfigurableData {

        public ColorPicker(String displayName, Setting<?> setting) {
            super(displayName, setting);
        }

    }

}
