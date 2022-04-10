/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Button
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.event.*
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.hud.impl.armor.Armor
import com.github.sorusclient.client.hud.impl.bossbar.BossBar
import com.github.sorusclient.client.hud.impl.coordinates.Coordinates
import com.github.sorusclient.client.hud.impl.cps.CPS
import com.github.sorusclient.client.hud.impl.experience.Experience
import com.github.sorusclient.client.hud.impl.fps.FPS
import com.github.sorusclient.client.hud.impl.health.Health
import com.github.sorusclient.client.hud.impl.hotbar.HotBar
import com.github.sorusclient.client.hud.impl.hunger.Hunger
import com.github.sorusclient.client.hud.impl.potions.Potions
import com.github.sorusclient.client.hud.impl.sidebar.SideBar
import com.github.sorusclient.client.hud.impl.timer.Timer
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.AbstractData
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.ui.framework.Container
import com.github.sorusclient.client.ui.framework.Scroll
import com.github.sorusclient.client.ui.framework.Text
import com.github.sorusclient.client.ui.framework.constraint.Dependent
import com.github.sorusclient.client.ui.framework.constraint.Relative
import com.github.sorusclient.client.ui.framework.constraint.Side
import com.github.sorusclient.client.ui.toAbsolute
import com.github.sorusclient.client.ui.toCopy
import com.github.sorusclient.client.ui.toRelative
import com.github.sorusclient.client.ui.toSide
import com.github.sorusclient.client.util.Axis
import com.github.sorusclient.client.util.Color
import com.github.sorusclient.client.util.keybind.KeyBind
import com.github.sorusclient.client.util.keybind.KeyBindManager
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt

object HUDManager {

    //TODO: elements resizing does not affect attached elements
    private val possibleHuds: MutableList<HUDData> = ArrayList()
    private val huds: MutableList<HUDElement> = ArrayList()
    private var prevClickTime: Long = 0
    private var draggedHud: HUDElement? = null
    private var interactType: InteractType? = null
    private var initialMouseX = 0.0
    private var initialMouseY = 0.0
    private var initialHudX = 0.0
    private var initialHudY = 0.0
    private var initialScale = 0.0
    private var snapped: Array<Pair<*, *>> = emptyArray()
    private var hudToOpenSettings: HUDElement? = null

    private var hoveredCloseButton = false
    private var hoveredAddButton = false

    var isHudEditScreenOpen = AtomicBoolean(false)
    private var prevIsHudEditScreenOpen = false

    private lateinit var addHudUI: Container

    private fun initializeUserInterface() {
        addHudUI = Container()
            .apply {
                backgroundCornerRadius = 0.0155.toRelative()
                setPadding(0.0135.toRelative())
                paddingLeft = 0.0.toAbsolute()

                backgroundColor = Color.fromRGB(15, 15, 15, 200).toAbsolute()
                borderColor = Color.fromRGB(10, 10, 10, 150).toAbsolute()
                borderThickness = 0.4.toAbsolute()

                children += Container()
                    .apply {
                        y = Side.NEGATIVE.toSide()
                        height = 0.035.toRelative()
                        setPadding(0.02.toRelative())

                        children += Text()
                            .apply {
                                x = Side.NEGATIVE.toSide()

                                fontRenderer = "sorus/ui/font/Quicksand-Bold.ttf".toAbsolute()
                                scale = 0.0025.toRelative()
                                text = "Huds".toAbsolute()
                            }
                    }

                children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                    .apply {
                        addChild(com.github.sorusclient.client.ui.framework.List(com.github.sorusclient.client.ui.framework.List.GRID)
                            .apply {
                                columns = 5

                                val count = possibleHuds.size

                                height = Relative(
                                    ceil(count / 3.0) * 0.06 + (ceil(count / 3.0) + 1) * 0.015,
                                    true
                                )

                                for (hud in possibleHuds) {
                                    addChild(Container()
                                        .apply {
                                            width = 0.188.toRelative()
                                            height = 0.2.toCopy()

                                            backgroundCornerRadius = 0.01.toRelative()
                                            setPadding(0.01.toRelative())

                                            backgroundColor = Color.fromRGB(0, 0, 0, 65).toAbsolute()
                                            borderThickness = 0.4.toAbsolute()
                                            borderColor = Color.fromRGB(10, 10, 10, 150).toAbsolute()
                                            borderColor = Dependent { state ->
                                                if (state["hovered"] as Boolean) {
                                                    Color.fromRGB(60, 75, 250, 255)
                                                } else {
                                                    Color.fromRGB(0, 0, 0, 100)
                                                }
                                            }

                                            children += Container()
                                                .apply {
                                                    x = Side.NEGATIVE.toSide()
                                                    width = 1.0.toCopy()
                                                    height = 0.6.toRelative()
                                                    setPadding(Relative(0.2, true))
                                                }

                                            children += Text()
                                                .apply {
                                                    x = Side.NEGATIVE.toSide()

                                                    scale = 0.006.toRelative()
                                                    fontRenderer =
                                                        "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                    text = hud.name.toAbsolute()
                                                }

                                            onClick = { state ->
                                                state["tab"] = "settings"
                                                state["resetSettingsScreen"] = false
                                                (state["currentSettingsCategory"] as DisplayedCategory).onShow()
                                                add(hud.hudClass.getConstructor().newInstance()!!)
                                            }
                                        })
                                }
                            })
                    }
            }
    }

    class HudCategoryData(private val huds: MutableList<HUDElement>): AbstractData() {

        override fun loadForced(json: Any) {

        }

        override fun clearForced() {

        }

        override fun load(json: Any, isPrimary: Boolean) {
            this.huds.clear()

            val json = json as Map<String, Any>
            val huds: List<String> = json["huds"] as List<String>

            for (hudId in huds) {
                val hudData = json[hudId] as Map<String, Any>
                val hud = Class.forName(hudData["class"] as String?).getConstructor().newInstance() as HUDElement
                hud.category.load(hudData, isPrimary)

                this.huds.add(hud)
            }
        }

        override fun save(): Any {
            val map: MutableMap<String, Any> = HashMap()
            val list: MutableList<String> = ArrayList()
            map["huds"] = list

            for (hud in huds) {
                list.add(hud.internalId.value)
                map[hud.internalId.value] = hud.category.save()
                (map[hud.internalId.value] as MutableMap<String, Any>)
                    .apply {
                        put("class", hud.javaClass.name)
                    }
            }

            return map
        }

    }

    class HudDisplayedCategory(var isHudEditScreenOpen: AtomicBoolean) : DisplayedCategory("HUD") {
        override var showUI: Boolean = false

        override fun onShow() {
            isHudEditScreenOpen.set(true)
        }

        override fun onHide() {
            isHudEditScreenOpen.set(false)
        }

    }

    var hudDisplayedCategory: HudDisplayedCategory

    init {
        SettingManager.settingsCategory
            .apply {
                data["hud"] = HudCategoryData(huds)
            }

        SettingManager.mainUICategory
            .apply {
                add(HudDisplayedCategory(isHudEditScreenOpen).also { hudDisplayedCategory = it })
            }

        initializePossibleElements()
        setupDefaultHud()
        val eventManager = EventManager
        eventManager.register { event: ArmorBarRenderEvent -> event.canceled = true }
        eventManager.register { event: BossBarRenderEvent -> event.canceled = true }
        eventManager.register { event: ExperienceBarRenderEvent -> event.canceled = true }
        eventManager.register { event: HealthBarRenderEvent -> event.canceled = true }
        eventManager.register { event: HotBarRenderEvent -> event.canceled = true }
        eventManager.register { event: HungerBarRenderEvent -> event.canceled = true }
        eventManager.register { event: SideBarRenderEvent -> event.canceled = true }
    }

    private fun setupDefaultHud() {
        val hotBar = HotBar()
        hotBar.addAttached(null, AttachType(0.0, 0.0, Axis.X))
        hotBar.addAttached(null, AttachType(1.0, 1.0, Axis.Y))
        add(hotBar)

        val experience = Experience()
        experience.addAttached(hotBar, AttachType(0.0, 0.0, Axis.X))
        experience.addAttached(hotBar, AttachType(1.0, -1.0, Axis.Y))
        add(experience)

        hotBar.addAttached(experience, AttachType(0.0, 0.0, Axis.X))
        hotBar.addAttached(experience, AttachType(-1.0, 1.0, Axis.Y))

        val health = Health()
        health.addAttached(experience, AttachType(-1.0, -1.0, Axis.X))
        health.addAttached(experience, AttachType(1.0, -1.0, Axis.Y))
        add(health)

        experience.addAttached(health, AttachType(-1.0, -1.0, Axis.X))
        experience.addAttached(health, AttachType(-1.0, 1.0, Axis.Y))

        val armor = Armor()
        armor.addAttached(health, AttachType(-1.0, -1.0, Axis.X))
        armor.addAttached(health, AttachType(1.0, -1.0, Axis.Y))
        add(armor)

        health.addAttached(armor, AttachType(-1.0, -1.0, Axis.X))
        health.addAttached(armor, AttachType(-1.0, 1.0, Axis.Y))

        val hunger = Hunger()
        hunger.addAttached(experience, AttachType(1.0, 1.0, Axis.X))
        hunger.addAttached(experience, AttachType(1.0, -1.0, Axis.Y))
        add(hunger)

        experience.addAttached(hunger, AttachType(1.0, 1.0, Axis.X))
        experience.addAttached(hunger, AttachType(-1.0, 1.0, Axis.Y))

        val bossBar = BossBar()
        bossBar.addAttached(null, AttachType(0.0, 0.0, Axis.X))
        bossBar.addAttached(null, AttachType(-1.0, -1.0, Axis.Y))
        add(bossBar)

        val sideBar = SideBar()
        sideBar.addAttached(null, AttachType(1.0, 1.0, Axis.X))
        sideBar.addAttached(null, AttachType(0.0, 0.0, Axis.Y))
        add(sideBar)
    }

    fun initialize() {
        EventManager.apply {
            register<RenderInGameEvent> { render() }
            register(this@HUDManager::onClick)
            register<InitializeEvent> { initializeUserInterface() }

            register { event: KeyEvent ->
                if (event.isPressed && !event.isRepeat && event.key === Key.U) {
                    initializeUserInterface()
                }
            }
        }

        KeyBindManager.register(KeyBind({ listOf(Key.ESCAPE) }, { pressed ->
            if (pressed) {
                isHudEditScreenOpen.set(false)
            }
        }))
    }

    private fun initializePossibleElements() {
        register(Armor::class.java, "Armor", "yes yersy")
        register(BossBar::class.java, "BossBar", "yes yersy")
        register(Coordinates::class.java, "Coordinates", "yes yersy")
        register(CPS::class.java, "CPS", "yes yersy")
        register(Experience::class.java, "Experience", "yes yersy")
        register(FPS::class.java, "FPS", "yes yersy")
        register(Health::class.java, "Health", "yes yersy")
        register(HotBar::class.java, "HotBar", "yes yersy")
        register(Hunger::class.java, "Hunger", "yes yersy")
        register(Potions::class.java, "PotionStatus", "yes yersy")
        register(SideBar::class.java, "Sidebar", "yes yersy")
        register(Timer::class.java, "Timer", "yes yersy")
    }

    private fun register(hudClass: Class<out HUDElement>, name: String, description: String) {
        possibleHuds.add(HUDData(hudClass, name, description))
    }

    fun add(hud: HUDElement) {
        huds.add(hud)
    }

    fun remove(hud: HUDElement) {
        huds.remove(hud)
    }

    fun render() {
        val adapter = AdapterManager.adapter
        val renderer = AdapterManager.adapter.renderer
        val screenDimensions = adapter.screenDimensions
        val mouseLocation = adapter.mouseLocation
        val isHudEditScreenOpen = prevIsHudEditScreenOpen
        if (!isHudEditScreenOpen) {
            for (element in getElements().values) {
                for ((attachedId, _) in element.attached.value) {
                    val attachedElement = getById(attachedId)
                    element.updatePosition(attachedElement, screenDimensions)
                }
            }
        }

        prevIsHudEditScreenOpen = this.isHudEditScreenOpen.get()

        for (element in getElements().values) {
            element.render()
        }
        if (isHudEditScreenOpen) {
            for (element in getElements().values) {
                val x = element.getX(screenDimensions[0])
                val y = element.getY(screenDimensions[1])
                val width = element.scaledWidth
                val height = element.scaledHeight
                renderer.drawRectangle(x - width / 2, y - height / 2, width, height, if (element == draggedHud && interactType == InteractType.MOVE) {
                    Color.fromRGB(200, 200, 200, 100)
                } else if ((mouseLocation[0] > x - width / 2 && mouseLocation[0] < x + width / 2 && mouseLocation[1] > y - height / 2 && mouseLocation[1] < y + height / 2) || element == draggedHud) {
                    Color.fromRGB(200, 200, 200, 75)
                } else {
                    Color.fromRGB(200, 200, 200, 50)
                })
                renderer.drawRectangleBorder(x - width / 2, y - height / 2, width, height, 0.5, Color.fromRGB(200, 200, 200, 150))

                renderer.drawRectangle(x - width / 2 - 2, y - height / 2 - 2, 4.0, 4.0, if (element == draggedHud && interactType == InteractType.RESIZE_TOP_LEFT) {
                    Color.fromRGB(200, 200, 200, 100)
                } else if (mouseLocation[0] > x - width / 2 - 2 && mouseLocation[0] < x - width / 2 + 2 && mouseLocation[1] > y - height / 2 - 2 && mouseLocation[1] < y - height / 2 + 2) {
                    Color.fromRGB(200, 200, 200, 75)
                } else {
                    Color.fromRGB(200, 200, 200, 50)
                })
                renderer.drawRectangle(x + width / 2 - 2, y - height / 2 - 2, 4.0, 4.0, if (element == draggedHud && interactType == InteractType.RESIZE_TOP_RIGHT) {
                    Color.fromRGB(200, 200, 200, 100)
                } else if (mouseLocation[0] > x + width / 2 - 2 && mouseLocation[0] < x + width / 2 + 2 && mouseLocation[1] > y - height / 2 - 2 && mouseLocation[1] < y - height / 2 + 2) {
                    Color.fromRGB(200, 200, 200, 75)
                } else {
                    Color.fromRGB(200, 200, 200, 50)
                })
                renderer.drawRectangle(x + width / 2 - 2, y + height / 2 - 2, 4.0, 4.0, if (element == draggedHud && interactType == InteractType.RESIZE_BOTTOM_RIGHT) {
                    Color.fromRGB(200, 200, 200, 100)
                } else if (mouseLocation[0] > x + width / 2 - 2 && mouseLocation[0] < x + width / 2 + 2 && mouseLocation[1] > y + height / 2 - 2 && mouseLocation[1] < y + height / 2 + 2) {
                    Color.fromRGB(200, 200, 200, 75)
                } else {
                    Color.fromRGB(200, 200, 200, 50)
                })
                renderer.drawRectangle(x - width / 2 - 2, y + height / 2 - 2, 4.0, 4.0, if (element == draggedHud && interactType == InteractType.RESIZE_BOTTOM_LEFT) {
                    Color.fromRGB(200, 200, 200, 100)
                } else if (mouseLocation[0] > x - width / 2 - 2 && mouseLocation[0] < x - width / 2 + 2 && mouseLocation[1] > y + height / 2 - 2 && mouseLocation[1] < y + height / 2 + 2) {
                    Color.fromRGB(200, 200, 200, 75)
                } else {
                    Color.fromRGB(200, 200, 200, 50)
                })

                renderer.drawImage("sorus/ui/navbar/settings.png", x - width / 2 + 2, y - height / 2 + 2, 4.0, 4.0, Color.WHITE)
                renderer.drawImage("sorus/ui/profiles/delete.png", x - width / 2 + 8, y - height / 2 + 2, 4.0, 4.0, Color.fromRGB(220, 50, 50, 255))
                renderer.drawRectangle(x - width / 2 + 14, y - height / 2 + 2, 4.0, 4.0, Color.WHITE)
            }

            val left = screenDimensions[0] - screenDimensions[1] * 0.075 - 2
            val size = screenDimensions[1] * 0.075
            val top = 2.0

            renderer.drawRectangle(left, top, size, size, screenDimensions[0] * 0.0075, Color.fromRGB(0, 0, 0, 65))
            renderer.drawRectangleBorder(left, top, size, size, screenDimensions[0] * 0.0075, 0.4, if (hoveredCloseButton) { Color.fromRGB(60, 75, 250, 255) } else { Color.fromRGB(0, 0, 0, 100) })
            renderer.drawImage("sorus/ui/settings/back.png", left + 0.3 * size, top + 0.3 * size, 0.4 * size, 0.4 * size, Color.WHITE)

            val left2 = screenDimensions[0] - (screenDimensions[1] * 0.075 + 2) * 2
            renderer.drawRectangle(left2, top, size, size, screenDimensions[0] * 0.0075, Color.fromRGB(0, 0, 0, 65))
            renderer.drawRectangleBorder(left2, top, size, size, screenDimensions[0] * 0.0075, 0.4, if (hoveredAddButton) { Color.fromRGB(60, 75, 250, 255) } else { Color.fromRGB(0, 0, 0, 100) })
        }

        if (draggedHud != null) {
            when (interactType) {
                InteractType.MOVE -> {
                    val xSnaps: MutableList<Snap> = ArrayList()
                    val ySnaps: MutableList<Snap> = ArrayList()
                    var wantedX = (initialHudX + (mouseLocation[0] - initialMouseX)).coerceIn(
                        draggedHud!!.scaledWidth / 2,
                        screenDimensions[0] - draggedHud!!.scaledWidth / 2
                    )
                    var wantedY = (initialHudY + (mouseLocation[1] - initialMouseY)).coerceIn(
                        draggedHud!!.scaledHeight / 2,
                        screenDimensions[1] - draggedHud!!.scaledHeight / 2
                    )
                    var snappedX: Pair<HUDElement?, AttachType>? = null
                    var snappedY: Pair<HUDElement?, AttachType>? = null
                    val possibleSnaps: MutableMap<HUDElement?, Pair<Pair<Double, Double>, Pair<Double, Double>>> =
                        HashMap()
                    for (hud in getElements().values) {
                        possibleSnaps[hud] = Pair(
                            Pair(
                                hud.getX(
                                    screenDimensions[0]
                                ), hud.scaledWidth
                            ), Pair(
                                hud.getY(
                                    screenDimensions[1]
                                ), hud.scaledHeight
                            )
                        )
                    }
                    possibleSnaps[null] = Pair(
                        Pair(
                            screenDimensions[0] / 2, screenDimensions[0]
                        ), Pair(
                            screenDimensions[1] / 2,
                            screenDimensions[1]
                        )
                    )
                    for ((key, value) in possibleSnaps) {
                        if (key === this.draggedHud) continue
                        val otherX = value.first.first
                        val otherWidth = value.first.second
                        val otherY = value.second.first
                        val otherHeight = value.second.second
                        val possibleSides = intArrayOf(-1, 0, 1)
                        for (selfSide in possibleSides) {
                            for (otherSide in possibleSides) {
                                val snapLocationX = otherX + otherWidth / 2 * otherSide
                                val snapOffsetX =
                                    abs(wantedX - draggedHud!!.scaledWidth / 2 * -selfSide - snapLocationX)
                                if (snapOffsetX < 5) {
                                    if (wantedY + draggedHud!!.scaledHeight / 2 + 5 >= otherY - otherHeight / 2 && wantedY - draggedHud!!.scaledHeight / 2 <= otherY + otherHeight / 2 + 5) {
                                        if (key == null || !key.isAttachedTo(draggedHud!!)) {
                                            var isMinSnap = true
                                            for (snap in xSnaps) {
                                                if (abs(snap.offset) <= abs(snapOffsetX)) {
                                                    isMinSnap = false
                                                    break
                                                }
                                            }
                                            if (isMinSnap) {
                                                wantedX = snapLocationX - selfSide * draggedHud!!.scaledWidth / 2
                                                for (snap in ArrayList(xSnaps)) {
                                                    if (snap.offset != snapOffsetX) {
                                                        xSnaps.remove(snap)
                                                        break
                                                    }
                                                }
                                                xSnaps.add(
                                                    Snap(
                                                        Axis.X,
                                                        snapOffsetX,
                                                        otherX + otherWidth / 2 * otherSide
                                                    )
                                                )
                                                snappedX = Pair(
                                                    key, AttachType(
                                                        selfSide.toDouble(), otherSide.toDouble(), Axis.X
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                                val snapLocationY = otherY + otherHeight / 2 * otherSide
                                val snapOffsetY =
                                    abs(wantedY - draggedHud!!.scaledHeight / 2 * -selfSide - snapLocationY)
                                if (snapOffsetY < 5) {
                                    if (wantedX + draggedHud!!.scaledWidth / 2 + 5 >= otherX - otherWidth / 2 && wantedX - draggedHud!!.scaledWidth / 2 <= otherX + otherWidth / 2 + 5) {
                                        if (key == null || !key.isAttachedTo(draggedHud!!)) {
                                            var isMinSnap = true
                                            for (snap in ySnaps) {
                                                if (abs(snap.offset) <= abs(snapOffsetY)) {
                                                    isMinSnap = false
                                                    break
                                                }
                                            }
                                            if (isMinSnap) {
                                                wantedY = snapLocationY - selfSide * draggedHud!!.scaledHeight / 2
                                                for (snap in ArrayList(ySnaps)) {
                                                    if (snap.offset != snapOffsetY) {
                                                        ySnaps.remove(snap)
                                                        break
                                                    }
                                                }
                                                ySnaps.add(
                                                    Snap(
                                                        Axis.Y,
                                                        snapOffsetY,
                                                        otherY + otherHeight / 2 * otherSide
                                                    )
                                                )
                                                snappedY = Pair(
                                                    key, AttachType(
                                                        selfSide.toDouble(), otherSide.toDouble(), Axis.Y
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    displaySnaps(screenDimensions, xSnaps)
                    displaySnaps(screenDimensions, ySnaps)
                    draggedHud!!.setPosition(wantedX, wantedY, screenDimensions)
                    for ((hudId, _) in draggedHud!!.attached.value) {
                        val hud = getById(hudId)
                        if (hud === draggedHud || hud == null) continue
                        hud.updatePosition(draggedHud, screenDimensions)
                    }
                    val snapped: MutableList<Pair<HUDElement?, AttachType>> = ArrayList()
                    if (snappedX != null) {
                        snapped.add(snappedX)
                        if (snappedY == null) {
                            val hud = snappedX.first
                            val hudY = hud?.getY(screenDimensions[1]) ?: (screenDimensions[1] / 2)
                            val hudHeight = hud?.scaledHeight ?: screenDimensions[1]
                            snapped.add(
                                Pair(
                                    hud, AttachType(
                                        0.0,
                                        (draggedHud!!.getY(screenDimensions[1]) - (hudY - hudHeight / 2)) / hudHeight * 2 - 1,
                                        Axis.Y
                                    )
                                )
                            )
                        }
                    }
                    if (snappedY != null) {
                        snapped.add(snappedY)
                        if (snappedX == null) {
                            val hud = snappedY.first
                            val hudX = hud?.getX(screenDimensions[0]) ?: (screenDimensions[0] / 2)
                            val hudWidth = hud?.scaledWidth ?: screenDimensions[0]
                            snapped.add(
                                Pair(
                                    hud, AttachType(
                                        0.0,
                                        (draggedHud!!.getX(screenDimensions[0]) - (hudX - hudWidth / 2)) / hudWidth * 2 - 1,
                                        Axis.X
                                    )
                                )
                            )
                        }
                    }
                    this.snapped = snapped.toTypedArray()
                }
                InteractType.RESIZE_BOTTOM_RIGHT -> {
                    val snaps: MutableList<Snap> = ArrayList()
                    var wantedScale =
                        (initialScale + (mouseLocation[0] - initialMouseX) / draggedHud!!.width).coerceIn(0.5..2.0)
                    wantedScale = getWantedScale(screenDimensions, 1.0, 1.0, snaps, wantedScale)
                    draggedHud!!.scale.realValue = wantedScale
                    draggedHud!!.setPosition(
                        initialHudX + (wantedScale - initialScale) * draggedHud!!.width / 2,
                        initialHudY + (wantedScale - initialScale) * draggedHud!!.height / 2,
                        screenDimensions
                    )
                    for ((hudId, _) in draggedHud!!.attached.value) {
                        val hud = getById(hudId)
                        if (hud === draggedHud || hud == null) continue
                        hud.updatePosition(draggedHud, screenDimensions)
                    }
                    displaySnaps(screenDimensions, snaps)
                }
                InteractType.RESIZE_TOP_RIGHT -> {
                    val snaps: MutableList<Snap> = ArrayList()
                    var wantedScale =
                        (initialScale + (mouseLocation[0] - initialMouseX) / draggedHud!!.width).coerceIn(0.5..2.0)
                    wantedScale = getWantedScale(screenDimensions, 1.0, -1.0, snaps, wantedScale)
                    draggedHud!!.scale.realValue = wantedScale
                    draggedHud!!.setPosition(
                        initialHudX + (wantedScale - initialScale) * draggedHud!!.width / 2,
                        initialHudY - (wantedScale - initialScale) * draggedHud!!.height / 2,
                        screenDimensions
                    )
                    for ((hudId, _) in draggedHud!!.attached.value) {
                        val hud = getById(hudId)
                        if (hud === draggedHud || hud == null) continue
                        hud.updatePosition(draggedHud, screenDimensions)
                    }
                    displaySnaps(screenDimensions, snaps)
                }
                InteractType.RESIZE_TOP_LEFT -> {
                    val snaps: MutableList<Snap> = ArrayList()
                    var wantedScale =
                        (initialScale - (mouseLocation[0] - initialMouseX) / draggedHud!!.width).coerceIn(0.5..2.0)
                    wantedScale = getWantedScale(screenDimensions, -1.0, -1.0, snaps, wantedScale)
                    draggedHud!!.scale.realValue = wantedScale
                    draggedHud!!.setPosition(
                        initialHudX - (wantedScale - initialScale) * draggedHud!!.width / 2,
                        initialHudY - (wantedScale - initialScale) * draggedHud!!.height / 2,
                        screenDimensions
                    )
                    displaySnaps(screenDimensions, snaps)
                }
                InteractType.RESIZE_BOTTOM_LEFT -> {
                    val snaps: MutableList<Snap> = ArrayList()
                    var wantedScale =
                        (initialScale - (mouseLocation[0] - initialMouseX) / draggedHud!!.width).coerceIn(0.5..2.0)
                    wantedScale = getWantedScale(screenDimensions, -1.0, 1.0, snaps, wantedScale)
                    draggedHud!!.scale.realValue = wantedScale
                    draggedHud!!.setPosition(
                        initialHudX - (wantedScale - initialScale) * draggedHud!!.width / 2,
                        initialHudY + (wantedScale - initialScale) * draggedHud!!.height / 2,
                        screenDimensions
                    )
                    displaySnaps(screenDimensions, snaps)
                }
                else -> {}
            }
        }
    }

    private fun displaySnaps(screenDimensions: DoubleArray, snaps: List<Snap>) {
        val renderer = AdapterManager.adapter.renderer
        for (snap in snaps) {
            if (snap.axis === Axis.X) {
                renderer.drawRectangle(
                    snap.location - 0.25, 0.0, 0.5,
                    screenDimensions[1], Color.fromRGB(255, 255, 255, 120)
                )
            } else {
                renderer.drawRectangle(
                    0.0, snap.location - 0.25,
                    screenDimensions[0], 0.5, Color.fromRGB(255, 255, 255, 120)
                )
            }
        }
    }

    private fun getWantedScale(
        screenDimensions: DoubleArray,
        xSideInt: Double,
        ySideInt: Double,
        snaps: MutableList<Snap>,
        wantedScale: Double
    ): Double {
        var newScale = wantedScale
        for (hud in getElements().values) {
            if (hud === draggedHud || draggedHud!!.isAttachedTo(hud)) continue
            val otherHudX = hud.getX(screenDimensions[0])
            val otherHudY = hud.getY(screenDimensions[1])
            val xSide =
                initialHudX - draggedHud!!.width / 2 * initialScale * xSideInt + draggedHud!!.width * newScale * xSideInt
            val ySide =
                initialHudY - draggedHud!!.height / 2 * initialScale * ySideInt + draggedHud!!.height * newScale * ySideInt
            val xOpposite = initialHudX - draggedHud!!.width / 2 * initialScale * xSideInt
            val yOpposite = initialHudY - draggedHud!!.height / 2 * initialScale * ySideInt
            val possibleSides = intArrayOf(-1, 0, 1)
            for (otherSide in possibleSides) {
                val snapOffsetX = abs(xSide - (otherHudX + hud.scaledWidth / 2 * otherSide))
                if (snapOffsetX < 5) {
                    if (yOpposite + draggedHud!!.height * newScale + 5 >= otherHudY - hud.scaledHeight / 2 && ySide - draggedHud!!.height * newScale <= otherHudY + hud.scaledHeight / 2 + 5) {
                        var isMinSnap = true
                        for (snap in snaps) {
                            if (abs(snap.offset) <= abs(snapOffsetX)) {
                                isMinSnap = false
                                break
                            }
                        }
                        if (isMinSnap) {
                            newScale =
                                abs(otherHudX + hud.scaledWidth / 2 * otherSide - xOpposite) / draggedHud!!.width
                            for (snap in ArrayList(snaps)) {
                                if (snap.offset != snapOffsetX || snap.axis !== Axis.X) {
                                    snaps.remove(snap)
                                    break
                                }
                            }
                            snaps.add(Snap(Axis.X, snapOffsetX, otherHudX + hud.scaledWidth / 2 * otherSide))
                        }
                    }
                }
                val snapOffsetY = abs(ySide - (otherHudY + hud.scaledHeight / 2 * otherSide))
                if (snapOffsetY < 5) {
                    if (xOpposite + draggedHud!!.width * newScale * xSideInt + 5 >= otherHudX - hud.scaledWidth / 2 && xSide - draggedHud!!.width * newScale * xSideInt <= otherHudX + hud.scaledWidth / 2 + 5) {
                        var isMinSnap = true
                        for (snap in snaps) {
                            if (abs(snap.offset) <= abs(snapOffsetY)) {
                                isMinSnap = false
                                break
                            }
                        }
                        if (isMinSnap) {
                            newScale =
                                abs(otherHudY + hud.scaledHeight / 2 * otherSide - yOpposite) / draggedHud!!.height
                            for (snap in ArrayList(snaps)) {
                                if (snap.offset != snapOffsetX || snap.axis !== Axis.Y) {
                                    snaps.remove(snap)
                                    break
                                }
                            }
                            snaps.add(Snap(Axis.Y, snapOffsetY, otherHudY + hud.scaledHeight / 2 * otherSide))
                        }
                    }
                }
            }
        }
        return newScale
    }

    private fun onClick(event: MouseEvent) {
        if (isHudEditScreenOpen.get()) {
            val screenDimensions = AdapterManager.adapter.screenDimensions
            if (event.x > screenDimensions[0] - screenDimensions[1] * 0.075 - 2 && event.y < screenDimensions[1] * 0.075) {
                hoveredCloseButton = true
                hoveredAddButton = false

                if (event.isPressed && event.button == Button.PRIMARY) {
                    hudDisplayedCategory.`return` = true
                    hoveredCloseButton = false
                }
            } else if (event.x > screenDimensions[0] - screenDimensions[1] * 0.075 * 2 - 4  && event.y < screenDimensions[1] * 0.075) {
                hoveredCloseButton = false
                hoveredAddButton = true

                if (event.isPressed && event.button == Button.PRIMARY) {
                    hudDisplayedCategory.customUI = addHudUI
                    hoveredAddButton = false
                }
            } else {
                hoveredCloseButton = false
                hoveredAddButton = false
            }
        }

        if (event.button != Button.PRIMARY) return

        if (event.isPressed && isHudEditScreenOpen.get()) {
            val screenDimensions = AdapterManager.adapter.screenDimensions
            for (element in getElements().values) {
                val x = element.getX(screenDimensions[0])
                val y = element.getY(screenDimensions[1])
                val left = x - element.scaledWidth / 2
                val right = x + element.scaledWidth / 2
                val top = y - element.scaledHeight / 2
                val bottom = y + element.scaledHeight / 2
                var interacting = false

                if (event.x > left + 2 && event.x < left + 6 && event.y > top + 2 && event.y < top + 6) {
                    hudDisplayedCategory.wantedOpenCategory = element.uiCategory
                } else if (event.x > left + 8 && event.x < left + 12 && event.y > top + 2 && event.y < top + 6) {
                    element.detach()
                    element.delete(ArrayList())
                } else if (event.x > left + 14 && event.x < left + 18 && event.y > top + 2 && event.y < top + 6) {
                    element.detach()
                } else if (event.x > left + 1 && event.x < left + 4 && event.y > top + 1 && event.y < top + 4) {
                    hudToOpenSettings = element
                } else if (distance(event.x, right, event.y, bottom) < 5) {
                    interactType = InteractType.RESIZE_BOTTOM_RIGHT
                    interacting = true
                } else if (distance(event.x, right, event.y, top) < 5) {
                    interactType = InteractType.RESIZE_TOP_RIGHT
                    interacting = true
                } else if (distance(event.x, left, event.y, top) < 5) {
                    interactType = InteractType.RESIZE_TOP_LEFT
                    interacting = true
                } else if (distance(event.x, left, event.y, bottom) < 5) {
                    interactType = InteractType.RESIZE_BOTTOM_LEFT
                    interacting = true
                } else if (event.x > left && event.x < right && event.y > top && event.y < bottom) {
                    interactType = InteractType.MOVE
                    interacting = true
                }
                if (interacting) {
                    draggedHud = element
                    initialMouseX = event.x
                    initialMouseY = event.y
                    initialHudX = x
                    initialHudY = y
                    initialScale = element.scale.value
                    prevClickTime = System.currentTimeMillis()
                }
            }
        } else {
            if (draggedHud == null) return
            draggedHud!!.clearStaticAttached(ArrayList())
            for (hud in snapped) {
                draggedHud!!.addAttached(hud.first as HUDElement?, hud.second as AttachType)
                if (hud.first != null) {
                    (hud.first as HUDElement).clearStaticAttached(ArrayList())
                    (hud.first as HUDElement).addAttached(draggedHud, (hud.second as AttachType).reverse())
                }
            }
            snapped = emptyArray()
            draggedHud = null
        }
    }

    private fun getElements(): Map<String, HUDElement> {
        val elements: MutableMap<String, HUDElement> = HashMap()
        for (hud in huds) {
            elements[hud.internalId.value] = hud
        }

        return elements
    }

    fun getById(id: String): HUDElement? {
        return getElements()[id]
    }

    private fun distance(x1: Double, x2: Double, y1: Double, y2: Double): Double {
        return sqrt((x1 - x2).pow(2.0) + (y1 - y2).pow(2.0))
    }

    private class Snap(
        val axis: Axis,
        val offset: Double,
        val location: Double
    )

    private enum class InteractType {
        MOVE, RESIZE_BOTTOM_RIGHT, RESIZE_TOP_RIGHT, RESIZE_TOP_LEFT, RESIZE_BOTTOM_LEFT
    }

}