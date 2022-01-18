package com.github.sorusclient.client.hud.impl.sidebar

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.IScoreboard
import com.github.sorusclient.client.adapter.IScoreboardObjective
import com.github.sorusclient.client.adapter.IScoreboardScore
import com.github.sorusclient.client.hud.HUDElement
import com.github.sorusclient.client.setting.ConfigurableData
import com.github.sorusclient.client.setting.ConfigurableData.Toggle
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.ui.UserInterface
import com.github.sorusclient.client.util.Color

class Sidebar : HUDElement("sideBar") {

    private var showScores: Setting<Boolean>

    private val sidebarObjective: IScoreboardObjective?
        get() {
            val scoreboard = AdapterManager.getAdapter().world.scoreboard
            val sidebarObjective = scoreboard.getObjective(IScoreboard.Slot.SIDEBAR)
            val editing = UserInterface.isHudEditScreenOpen()
            return if (sidebarObjective != null || !editing) {
                sidebarObjective
            } else {
                val fakeScores: MutableList<IScoreboardScore> = ArrayList()
                fakeScores.add(FakeScoreboardScore("Steve", 0))
                fakeScores.add(FakeScoreboardScore("Alex", 1))
                FakeScoreboardObjective(fakeScores, "Points")
            }
        }
    override val width: Double
        get() {
            val renderer = AdapterManager.getAdapter().renderer
            val fontRenderer = renderer.getFontRenderer("minecraft")!!
            val sidebarObjective = sidebarObjective ?: return 0.0
            var maxWidth = fontRenderer.getWidth(sidebarObjective.name)
            for (score in sidebarObjective.scores) {
                val scoreString = if (showScores.value) " " + score.score else ""
                maxWidth = maxWidth.coerceAtLeast(fontRenderer.getWidth(score.name + scoreString))
            }
            return 2 + maxWidth + 6
        }
    override val height: Double
        get() {
            val renderer = AdapterManager.getAdapter().renderer
            val fontRenderer = renderer.getFontRenderer("minecraft")!!
            val sidebarObjective = sidebarObjective ?: return 0.0
            return (sidebarObjective.scores.size + 1) * (fontRenderer.height + 1) + 1
        }

    init {
        register("showScores", Setting(true).also {
            showScores = it
        })
    }

    override fun render(x: Double, y: Double, scale: Double) {
        val renderer = AdapterManager.getAdapter().renderer
        val fontRenderer = renderer.getFontRenderer("minecraft")!!
        renderer.drawRectangle(x, y, width * scale, height * scale, Color.fromRGB(0, 0, 0, 80))
        val sidebarObjective = sidebarObjective ?: return
        fontRenderer.drawString(
            sidebarObjective.name,
            x + width * scale / 2 - fontRenderer.getWidth(sidebarObjective.name) * scale / 2,
            y + 1 * scale,
            scale,
            Color.WHITE
        )
        var yOffset = (fontRenderer.height + 2) * scale
        for (score in sidebarObjective.scores) {
            fontRenderer.drawString(score.name, x + 2 * scale, y + yOffset, scale, Color.WHITE)
            val scoreString = score.score.toString()
            if (showScores.value) {
                fontRenderer.drawString(
                    scoreString,
                    x + width * scale - (fontRenderer.getWidth(scoreString) + 1) * scale,
                    y + yOffset,
                    scale,
                    Color.fromRGB(255, 85, 85, 255)
                )
            }
            yOffset += (fontRenderer.height + 1) * scale
        }
    }

    override fun addSettings(settings: MutableList<ConfigurableData>) {
        super.addSettings(settings)
        settings.add(Toggle("Show Scores", showScores))
    }

    private class FakeScoreboardObjective(override val scores: List<IScoreboardScore>, override val name: String) : IScoreboardObjective

    private class FakeScoreboardScore(override val name: String, override val score: Int) : IScoreboardScore

}