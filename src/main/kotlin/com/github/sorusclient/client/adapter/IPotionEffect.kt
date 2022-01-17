package com.github.sorusclient.client.adapter

interface IPotionEffect {
    val duration: String
    val name: String?
    val amplifier: Int
    val type: PotionType?

    enum class PotionType {
        SPEED, SLOWNESS, HASTE, MINING_FATIGUE, STRENGTH, INSTANT_HEALTH, INSTANT_DAMAGE, JUMP_BOOST, NAUSEA, REGENERATION, RESISTANCE, FIRE_RESISTANCE, WATER_BREATHING, INVISIBILITY, BLINDNESS, NIGHT_VISION, HUNGER, WEAKNESS, POISON, WITHER, HEALTH_BOOST, ABSORPTION, SATURATION, UNKNOWN
    }
}