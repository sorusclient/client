package com.github.sorusclient.client.hud.impl.health

import com.github.glassmc.loader.api.GlassLoader
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.ILivingEntity
import com.github.sorusclient.client.adapter.IPotionEffect
import com.github.sorusclient.client.hud.HUDElement
import kotlin.math.ceil

class Health : HUDElement("health") {

    override val width: Double
        get() = (1 + 8 * 10 + 1 + 1).toDouble()
    override val height: Double
        get() {
            val player: ILivingEntity = AdapterManager.getAdapter().player!!
            val absorption = player.absorption
            val absorptionInt = ceil(absorption).toInt()
            val totalHealth = (ceil(player.maxHealth).toInt() + absorptionInt) / 2
            val totalRows = ceil(totalHealth / 10.0).toInt()
            return (totalRows * 11).toDouble()
        }

    override val displayName: String
        get() = "Health"

    private var prevHealth = 0.0
    private var regenTime: Long = 0
    private var damageTime: Long = 0
    private var preDamageHealth = 0.0
    private var regenStartTime: Long = 0
    private var prevHasRegen = false
    override fun render(x: Double, y: Double, scale: Double) {
        val player: ILivingEntity = AdapterManager.getAdapter().player!!
        var hasRegen = false
        for (effect in player.effects) {
            if (effect.type == IPotionEffect.PotionType.REGENERATION) {
                hasRegen = true
                break
            }
        }
        if (hasRegen && !prevHasRegen) {
            regenStartTime = System.currentTimeMillis()
        }
        val health = player.health
        val healthInt = ceil(health).toInt()
        val absorption = player.absorption
        val absorptionInt = ceil(absorption).toInt()
        if (health > prevHealth) {
            regenTime = System.currentTimeMillis()
        }
        if (health < prevHealth) {
            damageTime = System.currentTimeMillis()
            preDamageHealth = prevHealth
        }
        val preDamageHealth = ceil(preDamageHealth).toInt()
        var backgroundType = IHealthRenderer.BackgroundType.STANDARD
        val timeSinceRegen = System.currentTimeMillis() - regenTime
        if (timeSinceRegen < 500) {
            if (timeSinceRegen < 500.0 * 3 / 4 && timeSinceRegen > 500.0 * 2 / 4 || timeSinceRegen < 500.0 * 1 / 4 && timeSinceRegen > 500.0 * 0 / 4) {
                backgroundType = IHealthRenderer.BackgroundType.FLASHING_OUTLINE
            }
        }
        val timeSinceDamage = System.currentTimeMillis() - damageTime
        var showDamageEffect = false
        if (timeSinceDamage < 750) {
            if (timeSinceDamage < 750.0 * 5 / 6 && timeSinceDamage > 750.0 * 4 / 6 || timeSinceDamage < 750.0 * 3 / 6 && timeSinceDamage > 750.0 * 2 / 6 || timeSinceDamage < 750.0 * 1 / 6 && timeSinceDamage > 750.0 * 0 / 6) {
                backgroundType = IHealthRenderer.BackgroundType.FLASHING_OUTLINE
                showDamageEffect = true
            }
        }
        val healthRenderer = GlassLoader.getInstance().getInterface(IHealthRenderer::class.java)
        val totalHealth = (ceil(player.maxHealth).toInt() + absorptionInt) / 2
        val totalRows = ceil(totalHealth / 10.0).toInt()
        for (i in 0 until totalHealth) {
            val heartX = x + (1 + i % 10 * 8) * scale
            val heartY = y + (1 + 10 * (totalRows - 1) - (i / 10) * 10) * scale
            healthRenderer.renderHeartBackground(heartX, heartY, scale, backgroundType)
            if (i < healthInt / 2) {
                healthRenderer.renderHeart(
                    heartX,
                    heartY,
                    scale,
                    IHealthRenderer.HeartType.HEALTH,
                    IHealthRenderer.HeartRenderType.FULL
                )
            } else if (i < (healthInt + 1) / 2) {
                if (i + 1 <= preDamageHealth / 2 && showDamageEffect) {
                    healthRenderer.renderHeart(
                        heartX,
                        heartY,
                        scale,
                        IHealthRenderer.HeartType.HEALTH,
                        IHealthRenderer.HeartRenderType.HALF_DAMAGE
                    )
                } else {
                    healthRenderer.renderHeart(
                        heartX,
                        heartY,
                        scale,
                        IHealthRenderer.HeartType.HEALTH,
                        IHealthRenderer.HeartRenderType.HALF_EMPTY
                    )
                }
            } else if (i * 2 + 1 == preDamageHealth && showDamageEffect) {
                healthRenderer.renderHeart(
                    heartX,
                    heartY,
                    scale,
                    IHealthRenderer.HeartType.HEALTH,
                    IHealthRenderer.HeartRenderType.DAMAGE_EMPTY
                )
            } else if (i * 2 + 1 <= preDamageHealth && showDamageEffect) {
                healthRenderer.renderHeart(
                    heartX,
                    heartY,
                    scale,
                    IHealthRenderer.HeartType.HEALTH,
                    IHealthRenderer.HeartRenderType.DAMAGE
                )
            } else if (i >= totalHealth - absorptionInt / 2) {
                healthRenderer.renderHeart(
                    heartX,
                    heartY,
                    scale,
                    IHealthRenderer.HeartType.ABSORPTION,
                    IHealthRenderer.HeartRenderType.FULL
                )
            } else {
                healthRenderer.renderHeart(
                    heartX,
                    heartY,
                    scale,
                    IHealthRenderer.HeartType.HEALTH,
                    IHealthRenderer.HeartRenderType.EMPTY
                )
            }
        }
        prevHealth = health
        prevHasRegen = hasRegen
    }

}