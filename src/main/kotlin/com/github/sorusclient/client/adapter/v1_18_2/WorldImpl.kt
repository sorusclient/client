package com.github.sorusclient.client.adapter.v1_18_2

import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.adapter.IBossBar
import com.github.sorusclient.client.adapter.IScoreboard
import com.github.sorusclient.client.adapter.IWorld
import v1_18_2.net.minecraft.client.MinecraftClient
import v1_18_2.net.minecraft.client.gui.hud.BossBarHud
import v1_18_2.net.minecraft.client.gui.hud.ClientBossBar
import v1_18_2.net.minecraft.world.World
import java.lang.reflect.Field
import java.util.UUID

class WorldImpl(private val world: World) : IWorld {

    override val scoreboard: IScoreboard
        get() = ScoreboardImpl(world.scoreboard)

    private val bossBarsField: Field = run {
        val bossBars = Identifier.parse("v1_18_2/net/minecraft/client/gui/hud/BossBarHud#bossBars")
        val field = BossBarHud::class.java.getDeclaredField(bossBars.fieldName)
        field.isAccessible = true
        field
    }

    override val bossBars: List<IBossBar>
        get() {
            val bossBars = bossBarsField.get(MinecraftClient.getInstance().inGameHud.bossBarHud) as Map<UUID, ClientBossBar>

            val bossBarsList = java.util.ArrayList<IBossBar>()
            for (bossBar in bossBars) {
                bossBarsList.add(BossBarImpl(bossBar.value))
            }

            return bossBarsList
        }

}