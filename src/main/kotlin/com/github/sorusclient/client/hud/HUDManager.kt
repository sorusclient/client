package com.github.sorusclient.client.hud

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Button
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.event.*
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.hud.impl.armor.Armor
import com.github.sorusclient.client.hud.impl.bossbar.BossBar
import com.github.sorusclient.client.hud.impl.coordinates.Coordinates
import com.github.sorusclient.client.hud.impl.experience.Experience
import com.github.sorusclient.client.hud.impl.health.Health
import com.github.sorusclient.client.hud.impl.hotbar.HotBar
import com.github.sorusclient.client.hud.impl.hunger.Hunger
import com.github.sorusclient.client.hud.impl.potions.Potions
import com.github.sorusclient.client.hud.impl.sidebar.Sidebar
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingContainer
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.ui.UserInterface
import com.github.sorusclient.client.util.Axis
import com.github.sorusclient.client.util.Color
import com.github.sorusclient.client.util.Pair
import org.lwjgl.opengl.GL11
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object HUDManager : SettingContainer {

    //TODO: elements resizing does not affect attached elements
    private val possibleHuds: MutableList<HUDData> = ArrayList()
    private val elements: MutableMap<String, HUDElement> = HashMap()
    private var prevClickTime: Long = 0
    private var draggedHud: HUDElement? = null
    private var interactType: InteractType? = null
    private var initialMouseX = 0.0
    private var initialMouseY = 0.0
    private var initialHudX = 0.0
    private var initialHudY = 0.0
    private var initialScale = 0.0
    private var snapped: Array<Pair<*, *>> = emptyArray()
    private val sharedInternal: Setting<Boolean> = Setting(false)
    private var hudToOpenSettings: HUDElement? = null

    init {
        initializePossibleElements()
        setupDefaultHud()
        val eventManager = EventManager
        eventManager.register(
            ArmorBarRenderEvent::class.java
        ) { event: ArmorBarRenderEvent -> event.isCanceled = true }
        eventManager.register(
            BossBarRenderEvent::class.java
        ) { event: BossBarRenderEvent -> event.isCanceled = true }
        eventManager.register(
            ExperienceBarRenderEvent::class.java
        ) { event: ExperienceBarRenderEvent -> event.isCanceled = true }
        eventManager.register(
            HealthBarRenderEvent::class.java
        ) { event: HealthBarRenderEvent -> event.isCanceled = true }
        eventManager.register(
            HotBarRenderEvent::class.java
        ) { event: HotBarRenderEvent -> event.isCanceled = true }
        eventManager.register(
            HungerBarRenderEvent::class.java
        ) { event: HungerBarRenderEvent -> event.isCanceled = true }
        eventManager.register(
            SideBarRenderEvent::class.java
        ) { event: SideBarRenderEvent -> event.isCanceled = true }
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
        val sideBar = Sidebar()
        sideBar.addAttached(null, AttachType(1.0, 1.0, Axis.X))
        sideBar.addAttached(null, AttachType(0.0, 0.0, Axis.Y))
        add(sideBar)
    }

    fun initialize() {
        val eventManager = EventManager
        eventManager.register(RenderInGameEvent::class.java) { render() }
        eventManager.register(MouseEvent::class.java) { event: MouseEvent -> onClick(event) }
        eventManager.register(KeyEvent::class.java) { event: KeyEvent -> onKey(event) }
        SettingManager.register(this)
    }

    private fun initializePossibleElements() {
        registerPossibleElement(Armor::class.java, "Armor", "yes yersy")
        registerPossibleElement(BossBar::class.java, "BossBar", "yes yersy")
        registerPossibleElement(Coordinates::class.java, "Coordinates", "yes yersy")
        registerPossibleElement(Experience::class.java, "Experience", "yes yersy")
        registerPossibleElement(Health::class.java, "Health", "yes yersy")
        registerPossibleElement(HotBar::class.java, "HotBar", "yes yersy")
        registerPossibleElement(Hunger::class.java, "Hunger", "yes yersy")
        registerPossibleElement(Potions::class.java, "PotionStatus", "yes yersy")
        registerPossibleElement(Sidebar::class.java, "Sidebar", "yes yersy")
    }

    private fun registerPossibleElement(hudClass: Class<out HUDElement>, name: String, description: String) {
        possibleHuds.add(HUDData(hudClass, name, description))
    }

    fun add(hud: HUDElement) {
        val id = hud.id + "-" + System.nanoTime() % 1000
        elements[id] = hud
        hud.setInternalId(id)
    }

    fun remove(hud: HUDElement) {
        elements.remove(hud.internalId.value)
    }

    fun render() {
        val adapter = AdapterManager.getAdapter()
        val renderer = AdapterManager.getAdapter().renderer
        val screenDimensions = adapter.screenDimensions
        val mouseLocation = adapter.mouseLocation
        val isHudEditScreenOpen = UserInterface.isHudEditScreenOpen()
        if (!isHudEditScreenOpen) {
            for (element in elements.values) {
                for ((attachedId, _) in element.attached.value) {
                    val attachedElement = getById(attachedId)
                    element.updatePosition(attachedElement, screenDimensions)
                }
            }
        }
        val blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND)
        val textureEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D)
        for (element in elements.values) {
            element.render()
        }
        if (isHudEditScreenOpen) {
            for (element in elements.values) {
                val x = element.getX(screenDimensions[0])
                val y = element.getY(screenDimensions[1])
                val width = element.scaledWidth
                val height = element.scaledHeight
                renderer.drawRectangle(
                    x - width / 2,
                    y - height / 2,
                    width,
                    height,
                    Color.fromRGB(200, 200, 200, 50)
                )
                renderer.drawRectangle(x - width / 2 + 1, y - height / 2 + 1, 3.0, 3.0, Color.WHITE)
            }
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
                    for (hud in elements.values) {
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
        if (blendEnabled) {
            GL11.glEnable(GL11.GL_BLEND)
        } else {
            GL11.glDisable(GL11.GL_BLEND)
        }
        if (textureEnabled) {
            GL11.glEnable(GL11.GL_TEXTURE_2D)
        } else {
            GL11.glDisable(GL11.GL_TEXTURE_2D)
        }
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
    }

    private fun displaySnaps(screenDimensions: DoubleArray, snaps: List<Snap>) {
        val renderer = AdapterManager.getAdapter().renderer
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
        for (hud in elements.values) {
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
        if (event.button != Button.PRIMARY) return
        if (event.isPressed && UserInterface.isHudEditScreenOpen()) {
            val screenDimensions = AdapterManager.getAdapter().screenDimensions
            for (element in elements.values) {
                val x = element.getX(screenDimensions[0])
                val y = element.getY(screenDimensions[1])
                val left = x - element.scaledWidth / 2
                val right = x + element.scaledWidth / 2
                val top = y - element.scaledHeight / 2
                val bottom = y + element.scaledHeight / 2
                var interacting = false
                if (event.x > left + 1 && event.x < left + 4 && event.y > top + 1 && event.y < top + 4) {
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
                    if (System.currentTimeMillis() - prevClickTime < 400) {
                        element.detach()
                        interacting = false
                    }
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
                    hud.first.clearStaticAttached(ArrayList())
                    hud.first.addAttached(draggedHud, hud.second.reverse())
                }
            }
            snapped = emptyArray()
            draggedHud = null
        }
    }

    private fun onKey(event: KeyEvent) {
        if (event.isPressed && event.key == Key.D && draggedHud != null) {
            draggedHud!!.delete(ArrayList())
            draggedHud = null
        }
    }

    fun getById(id: String): HUDElement? {
        return elements[id]
    }

    private fun distance(x1: Double, x2: Double, y1: Double, y2: Double): Double {
        return sqrt((x1 - x2).pow(2.0) + (y1 - y2).pow(2.0))
    }

    override val id: String
        get() = "hud"

    override fun load(settings: Map<String, Any>) {
        elements.clear()
        for ((key, value) in settings) {
            val hudSettings = value as Map<String, Any>
            try {
                val hudClass = Class.forName(hudSettings["class"] as String?)
                val hudInstance = hudClass.newInstance() as HUDElement
                hudInstance.load(hudSettings)
                elements[key] = hudInstance
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    override fun loadForced(settings: Map<String, Any>) {}
    override fun removeForced() {}
    override fun save(): Map<String, Any> {
        val settingsMap: MutableMap<String, Any> = HashMap()
        for ((key, value)  in elements) {
            settingsMap[key] = value.save()
        }
        return settingsMap
    }

    override var shared: Boolean = false
        get() = sharedInternal.value

    private class Snap(
        val axis: Axis,
        val offset: Double,
        val location: Double
    )

    private enum class InteractType {
        MOVE, RESIZE_BOTTOM_RIGHT, RESIZE_TOP_RIGHT, RESIZE_TOP_LEFT, RESIZE_BOTTOM_LEFT
    }

}