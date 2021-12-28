package com.github.sorusclient.client.module.impl.zoom.v1_8_9;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.module.ModuleManager;
import com.github.sorusclient.client.module.impl.zoom.Zoom;

public class ZoomHook {

    public static float modifyFOV(float fov) {
        Zoom zoom = Sorus.getInstance().get(ModuleManager.class).get(Zoom.class);
        if (zoom.applyZoom()) {
            return (float) zoom.getFov();
        } else {
            return fov;
        }
    }

    public static float modifySensitivity(float sensitivity) {
        Zoom zoom = Sorus.getInstance().get(ModuleManager.class).get(Zoom.class);

        if (zoom.applyZoom()) {
            return sensitivity * (float) zoom.getSensitivity();
        } else {
            return sensitivity;
        }
    }

    public static boolean useCinematicCamera() {
        Zoom zoom = Sorus.getInstance().get(ModuleManager.class).get(Zoom.class);

        return zoom.applyZoom() && zoom.useCinematicCamera();
    }

}
