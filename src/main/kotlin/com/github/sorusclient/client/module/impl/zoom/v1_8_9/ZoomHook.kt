package com.github.sorusclient.client.module.impl.zoom.v1_8_9

import com.github.sorusclient.client.module.ModuleManager
import com.github.sorusclient.client.module.impl.zoom.Zoom

object ZoomHook {

    @JvmStatic
    fun modifyFOV(fov: Float): Float {
        val zoom = ModuleManager.get<Zoom>()
        return if (zoom.applyZoom()) {
            zoom.getFovValue().toFloat()
        } else {
            fov
        }
    }

    @JvmStatic
    fun modifySensitivity(sensitivity: Float): Float {
        val zoom = ModuleManager.get<Zoom>()
        return if (zoom.applyZoom()) {
            sensitivity * zoom.getSensitivityValue().toFloat()
        } else {
            sensitivity
        }
    }

    @JvmStatic
    fun useCinematicCamera(): Boolean {
        val zoom = ModuleManager.get<Zoom>()
        return zoom.applyZoom() && zoom.useCinematicCamera()
    }

}