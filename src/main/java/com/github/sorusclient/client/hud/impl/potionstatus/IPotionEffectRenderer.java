package com.github.sorusclient.client.hud.impl.potionstatus;

import com.github.sorusclient.client.adapter.IPotionEffect;

public interface IPotionEffectRenderer {

    void render(IPotionEffect.PotionType type, double x, double y, double scale);

}
