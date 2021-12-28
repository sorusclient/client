package com.github.sorusclient.client.hud.impl.health;

import com.github.glassmc.loader.GlassLoader;
import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.ILivingEntity;
import com.github.sorusclient.client.adapter.IPotionEffect;
import com.github.sorusclient.client.adapter.MinecraftAdapter;
import com.github.sorusclient.client.hud.HUDElement;

public class Health extends HUDElement {

    private double prevHealth;
    private long regenTime;

    private long damageTime;
    private double preDamageHealth;

    private long regenStartTime;
    private boolean prevHasRegen;

    public Health() {
        super("health");
    }

    @Override
    protected void render(double x, double y, double scale) {
        ILivingEntity player = Sorus.getInstance().get(MinecraftAdapter.class).getPlayer();
        if (player == null) return;

        boolean hasRegen = false;
        for (IPotionEffect effect : Sorus.getInstance().get(MinecraftAdapter.class).getPlayer().getEffects()) {
            if (effect.getType() == IPotionEffect.PotionType.REGENERATION) {
                hasRegen = true;
                break;
            }
        }
        if (hasRegen && !this.prevHasRegen) {
            this.regenStartTime = System.currentTimeMillis();
        }

        double health = player.getHealth();
        int healthInt = (int) Math.ceil(health);

        double absorption = player.getAbsorption();
        int absorptionInt = (int) Math.ceil(absorption);

        if (health > this.prevHealth) {
            this.regenTime = System.currentTimeMillis();
        }

        if (health < this.prevHealth) {
            this.damageTime = System.currentTimeMillis();
            this.preDamageHealth = this.prevHealth;
        }

        int preDamageHealth = (int) Math.ceil(this.preDamageHealth);

        IHealthRenderer.BackgroundType backgroundType = IHealthRenderer.BackgroundType.STANDARD;
        long timeSinceRegen = System.currentTimeMillis() - this.regenTime;
        if (timeSinceRegen < 500) {
            if ((timeSinceRegen < 500.0 * 3 / 4 && timeSinceRegen > 500.0 * 2 / 4) || (timeSinceRegen < 500.0 * 1 / 4 && timeSinceRegen > 500.0 * 0 / 4)) {
                backgroundType = IHealthRenderer.BackgroundType.FLASHING_OUTLINE;
            }
        }

        long timeSinceDamage = System.currentTimeMillis() - this.damageTime;
        boolean showDamageEffect = false;
        if (timeSinceDamage < 750) {
            if ((timeSinceDamage < 750.0 * 5 / 6 && timeSinceDamage > 750.0 * 4 / 6) || (timeSinceDamage < 750.0 * 3 / 6 && timeSinceDamage > 750.0 * 2 / 6) || (timeSinceDamage < 750.0 * 1 / 6 && timeSinceDamage > 750.0 * 0 / 6)) {
                backgroundType = IHealthRenderer.BackgroundType.FLASHING_OUTLINE;
                showDamageEffect = true;
            }
        }

        IHealthRenderer healthRenderer = GlassLoader.getInstance().getInterface(IHealthRenderer.class);

        int totalHealth = ((int) Math.ceil(player.getMaxHealth()) + absorptionInt) / 2;

        int totalRows = (int) Math.ceil(totalHealth / 10.0);

        for (int i = 0; i < totalHealth; i++) {
            double heartX = x + (1 + (i % 10) * 8) * scale;
            double heartY = y + (1 + 10 * (totalRows - 1) - (int) (i / 10) * 10) * scale;

            double timeSinceRegen2 = (System.currentTimeMillis() - regenStartTime) % (totalHealth * 50L + 1000);
            if (hasRegen && timeSinceRegen2 >= i * 50 && timeSinceRegen2 <= (i + 1) * 50) {
                //heartY -= 2;
            }

            healthRenderer.renderHeartBackground(heartX, heartY, scale, backgroundType);

            if (i < healthInt / 2) {
                healthRenderer.renderHeart(heartX, heartY, scale, IHealthRenderer.HeartType.HEALTH, IHealthRenderer.HeartRenderType.FULL);
            } else if (i < (healthInt + 1) / 2) {
                if (i + 1 <= preDamageHealth / 2 && showDamageEffect) {
                    healthRenderer.renderHeart(heartX, heartY, scale, IHealthRenderer.HeartType.HEALTH, IHealthRenderer.HeartRenderType.HALF_DAMAGE);
                } else {
                    healthRenderer.renderHeart(heartX, heartY, scale, IHealthRenderer.HeartType.HEALTH, IHealthRenderer.HeartRenderType.HALF_EMPTY);
                }
            } else if (i * 2 + 1 == preDamageHealth && showDamageEffect) {
                healthRenderer.renderHeart(heartX, heartY, scale, IHealthRenderer.HeartType.HEALTH, IHealthRenderer.HeartRenderType.DAMAGE_EMPTY);
            } else if (i * 2 + 1 <= preDamageHealth && showDamageEffect) {
                healthRenderer.renderHeart(heartX, heartY, scale, IHealthRenderer.HeartType.HEALTH, IHealthRenderer.HeartRenderType.DAMAGE);
            } else if (i >= totalHealth - absorptionInt / 2) {
                healthRenderer.renderHeart(heartX, heartY, scale, IHealthRenderer.HeartType.ABSORPTION, IHealthRenderer.HeartRenderType.FULL);
            } else {
                healthRenderer.renderHeart(heartX, heartY, scale, IHealthRenderer.HeartType.HEALTH, IHealthRenderer.HeartRenderType.EMPTY);
            }
        }

        this.prevHealth = health;
        this.prevHasRegen = hasRegen;
    }

    @Override
    public double getWidth() {
        return 1 + 8 * 10 + 1 + 1;
    }

    @Override
    public double getHeight() {
        ILivingEntity player = Sorus.getInstance().get(MinecraftAdapter.class).getPlayer();
        if (player == null) return 0;

        double absorption = player.getAbsorption();
        int absorptionInt = (int) Math.ceil(absorption);

        int totalHealth = ((int) Math.ceil(player.getMaxHealth()) + absorptionInt) / 2;

        int totalRows = (int) Math.ceil(totalHealth / 10.0);

        return totalRows * 11;
    }

}
