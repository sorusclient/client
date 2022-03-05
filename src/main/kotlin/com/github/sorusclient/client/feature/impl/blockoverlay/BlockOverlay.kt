package com.github.sorusclient.client.feature.impl.blockoverlay

import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.util.Color

class BlockOverlay {

    var borderColor: Setting<Color>
    var borderThickness: Setting<Double>
    var fillColor: Setting<Color>

    init {
        SettingManager.settingsCategory
            .apply {
                data["blockOverlay"] = CategoryData()
                    .apply {
                        data["borderColor"] = SettingData(Setting(Color.BLACK).also { borderColor = it })
                        data["borderThickness"] = SettingData(Setting(1.0).also { borderThickness = it })
                        data["fillColor"] = SettingData(Setting(Color.fromRGB(0, 0, 0, 0)).also { fillColor = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Block Overlay"))
                    .apply {
                        add(ColorPicker(borderColor, "Border Color", ))
                        add(Slider(borderThickness, "Border Thickness", 0.0, 5.0))
                        add(ColorPicker(fillColor, "Fill Color", ))
                    }
            }
    }

    private fun getBorderColor(): Color {
        return borderColor.value
    }

    private fun getBorderThickness(): Double {
        return borderThickness.value
    }

    private fun getFillColor(): Color {
        return fillColor.value
    }

}