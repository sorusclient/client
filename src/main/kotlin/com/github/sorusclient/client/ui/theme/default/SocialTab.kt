/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.ui.theme.default

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.social.SocialManager
import com.github.sorusclient.client.ui.*
import com.github.sorusclient.client.ui.framework.*
import com.github.sorusclient.client.ui.framework.List
import com.github.sorusclient.client.ui.framework.constraint.Dependent
import com.github.sorusclient.client.ui.framework.constraint.Flexible
import com.github.sorusclient.client.ui.framework.constraint.Relative
import com.github.sorusclient.client.ui.framework.constraint.Side
import com.github.sorusclient.client.util.Color
import com.github.sorusclient.client.util.MojangUtil
import com.github.sorusclient.client.util.Rectangle
import com.github.sorusclient.client.websocket.WebSocketManager

class SocialTab(private val defaultTheme: DefaultTheme): Container() {

    init {
        onClose += { state ->
            val displayedCategory = state["currentSettingsCategory"] as DisplayedCategory
            displayedCategory.onHide()
        }

        onInit += { state ->
            if (state.second["resetSettingsScreen"] == null || state.second["resetSettingsScreen"] as Boolean) {
                state.second["currentSettingsCategory"] = SettingManager.mainUICategory
            }
        }

        children += Container()
            .apply {
                x = Side.NEGATIVE.toSide()
                width = 0.3.toRelative()

                backgroundCornerRadius = 0.0155.toRelative()
                setPadding(0.0125.toRelative())
                paddingLeft = 0.0.toAbsolute()

                backgroundColor = { defaultTheme.backgroundColor.value }.toDependent()
                borderColor = { defaultTheme.borderColor.value }.toDependent()
                borderThickness = 0.001.toRelative()

                onStateUpdate["hasInitGroups"] = { state ->
                    state["hasInit"] = false
                }

                onInit += {
                    clear()

                    children += Container()
                        .apply {
                            y = Side.NEGATIVE.toSide()
                            height = 0.035.toRelative()
                            setPadding(0.05.toRelative())

                            children += Text()
                                .apply {
                                    x = Side.NEGATIVE.toSide()

                                    fontRenderer = "sorus/ui/font/Quicksand-Bold.ttf".toAbsolute()
                                    scale = 0.0075.toRelative()
                                    text = "Groups".toAbsolute()
                                    textColor = { defaultTheme.elementColor.value }.toDependent()
                                }
                        }

                    children += TabHolder()
                        .apply {
                            onUpdate += { state ->
                                if (!WebSocketManager.connected) {
                                    state["groupsTab"] = "noSocket"
                                } else if (SocialManager.currentGroup != null) {
                                    state["groupsTab"] = "group"
                                } else {
                                    state["groupsTab"] = "noGroup"
                                }
                            }

                            storedState += "groupsTab"
                            stateId = "groupsTab"

                            addChild("noSocket", Container()
                                .apply {
                                    children += Text()
                                        .apply {
                                            y = (-0.05).toRelative()

                                            scale = 0.006.toRelative()
                                            fontRenderer = "sorus/ui/font/Quicksand-Regular.ttf".toAbsolute()
                                            text = "Socket Not Connected".toAbsolute()
                                            textColor = { defaultTheme.elementColor.value }.toDependent()
                                        }
                                })

                            addChild("noGroup", Container()
                                .apply {
                                    children += Container()
                                        .apply {
                                            y = (-0.05).toRelative()
                                            width = 0.4.toRelative()
                                            height = 0.3.toCopy()

                                            backgroundCornerRadius = 0.025.toRelative()
                                            backgroundColor = Dependent { state ->
                                                if (state["clicked"] != null && state["clicked"] as Boolean) {
                                                    { defaultTheme.selectedColor.value }.toDependent()
                                                } else {
                                                    { defaultTheme.midgroundColor.value }.toDependent()
                                                }
                                            }
                                            borderThickness = 0.001.toRelative()
                                            borderColor = Dependent { state ->
                                                if ((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) {
                                                    { defaultTheme.selectedBorderColor.value }.toDependent()
                                                } else {
                                                    { defaultTheme.borderColor.value }.toDependent()
                                                }
                                            }

                                            children += Container()
                                                .apply {
                                                    x = Side.NEGATIVE.toSide()
                                                    width = 1.0.toCopy()
                                                    height = 0.5.toRelative()
                                                    setPadding(Relative(0.2, true))

                                                    backgroundImage = "sorus/ui/groups/invite.png".toAbsolute()
                                                    backgroundColor = { defaultTheme.elementColor.value }.toDependent()
                                                }

                                            children += Text()
                                                .apply {
                                                    setPadding(Relative(0.2, true))

                                                    scale = 0.012.toRelative()
                                                    fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                    text = "Create".toAbsolute()
                                                    textColor = { defaultTheme.elementColor.value }.toDependent()
                                                }

                                            onClick = { state ->
                                                SocialManager.createGroup()
                                                state["hasInitGroups"] = false
                                            }
                                        }
                                })

                            addChild("group", Container()
                                .apply {
                                    onInit += {
                                        clear()

                                        if (SocialManager.currentGroup!!.owner) {
                                            children += Container()
                                                .apply {
                                                    y = Side.NEGATIVE.toSide()
                                                    height = 0.06125.toRelative()

                                                    paddingLeft = 0.075.toRelative()
                                                    paddingRight = 0.075.toRelative()

                                                    children += Container()
                                                        .apply {
                                                            x = Side.NEGATIVE.toSide()
                                                            width = 0.475.toRelative()

                                                            backgroundCornerRadius = 0.025.toRelative()
                                                            backgroundColor = Dependent { state ->
                                                                if (state["clicked"] != null && state["clicked"] as Boolean && AdapterManager.adapter.currentServer != null) {
                                                                    { defaultTheme.selectedColor.value }.toDependent()
                                                                } else {
                                                                    { defaultTheme.midgroundColor.value }.toDependent()
                                                                }
                                                            }
                                                            borderThickness = 0.0025.toRelative()
                                                            borderColor = Dependent { state ->
                                                                if (((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) && AdapterManager.adapter.currentServer != null) {
                                                                    { defaultTheme.selectedBorderColor.value }.toDependent()
                                                                } else {
                                                                    { defaultTheme.borderColor.value }.toDependent()
                                                                }
                                                            }

                                                            children += Container()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()
                                                                    width = 1.0.toCopy()
                                                                    height = 0.5.toRelative()
                                                                    setPadding(Relative(0.2, true))

                                                                    backgroundImage = "sorus/ui/groups/warp.png".toAbsolute()
                                                                    backgroundColor = { defaultTheme.elementColor.value }.toDependent()
                                                                }

                                                            children += Text()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()
                                                                    setPadding(Relative(0.2, true))

                                                                    scale = 0.012.toRelative()
                                                                    fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                    text = "Warp".toAbsolute()
                                                                    textColor = { defaultTheme.elementColor.value }.toDependent()
                                                                }

                                                            onClick = {
                                                                if (AdapterManager.adapter.currentServer != null) {
                                                                    SocialManager.warpGroup()
                                                                }
                                                            }
                                                        }

                                                    children += Container()
                                                        .apply {
                                                            x = Side.POSITIVE.toSide()
                                                            width = 0.475.toRelative()

                                                            backgroundCornerRadius = 0.025.toRelative()
                                                            backgroundColor = Dependent { state ->
                                                                if (state["clicked"] != null && state["clicked"] as Boolean) {
                                                                    { defaultTheme.selectedColor.value }.toDependent()
                                                                } else {
                                                                    { defaultTheme.midgroundColor.value }.toDependent()
                                                                }
                                                            }
                                                            borderThickness = 0.0025.toRelative()
                                                            borderColor = Dependent { state ->
                                                                if (((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean)) {
                                                                    { defaultTheme.selectedBorderColor.value }.toDependent()
                                                                } else {
                                                                    { defaultTheme.borderColor.value }.toDependent()
                                                                }
                                                            }

                                                            children += Container()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()
                                                                    width = 1.0.toCopy()
                                                                    height = 0.5.toRelative()
                                                                    setPadding(Relative(0.2, true))

                                                                    backgroundImage = "sorus/ui/groups/disband.png".toAbsolute()
                                                                    backgroundColor = { defaultTheme.elementColor.value }.toDependent()
                                                                }

                                                            children += Text()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()
                                                                    setPadding(Relative(0.2, true))

                                                                    scale = 0.012.toRelative()
                                                                    fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                    text = "Disband".toAbsolute()
                                                                    textColor = { defaultTheme.elementColor.value }.toDependent()
                                                                }

                                                            onClick = {
                                                                SocialManager.disbandGroup()
                                                                SocialManager.currentGroup = null
                                                            }
                                                        }
                                                }

                                            children += Container()
                                                .apply {
                                                    y = Side.NEGATIVE.toSide()
                                                    height = 0.06125.toRelative()
                                                    backgroundColor = { defaultTheme.midgroundColor.value }.toDependent()
                                                    borderColor = { defaultTheme.borderColor.value }.toDependent()
                                                    backgroundCornerRadius = 0.025.toRelative()
                                                    borderThickness = 0.0025.toRelative()

                                                    paddingLeft = 0.075.toRelative()
                                                    paddingRight = 0.075.toRelative()
                                                    paddingTop = 0.0375.toRelative()
                                                    paddingBottom = 0.0375.toRelative()

                                                    var message = ""
                                                    var selected = false

                                                    onStateUpdate["selected"] = { state ->
                                                        selected = state["selected"] as Boolean
                                                    }

                                                    var prevKeyTime = System.currentTimeMillis()

                                                    onChar = { state ->
                                                        message += state.second.character
                                                        prevKeyTime = System.currentTimeMillis()
                                                    }

                                                    onKey = { state ->
                                                        if (state.second.isPressed) {
                                                            when (state.second.key) {
                                                                Key.BACKSPACE -> message = message.substring(0, message.length - 1)
                                                                Key.ENTER -> {
                                                                    SocialManager.invite(MojangUtil.getUUID(message))
                                                                    message = ""
                                                                }
                                                            }
                                                            prevKeyTime = System.currentTimeMillis()
                                                        }
                                                    }

                                                    children += Container()
                                                        .apply {
                                                            x = Side.NEGATIVE.toSide()
                                                            width = 1.0.toCopy()
                                                            height = 0.5.toRelative()

                                                            setPadding(Relative(0.2, true))
                                                            paddingLeft = Relative(0.3, true)

                                                            backgroundImage = "sorus/ui/groups/invite.png".toAbsolute()
                                                            backgroundColor = { defaultTheme.elementColor.value }.toDependent()
                                                        }

                                                    children += Text().apply {
                                                        x = Side.NEGATIVE.toSide()
                                                        y = 0.0.toRelative()

                                                        paddingLeft = 0.05.toRelative()

                                                        text = {
                                                            if (message.isNotEmpty() || selected) { message } else { "Invite..." }
                                                        }.toDependent()
                                                        fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()

                                                        scale = 0.0065.toRelative()
                                                    }

                                                    children += Container()
                                                        .apply {
                                                            x = Side.NEGATIVE.toSide()
                                                            width = 0.2.toAbsolute()
                                                            height = 0.6.toRelative()

                                                            backgroundColor = { defaultTheme.elementColor.value }.toDependent()

                                                            onUpdate += { state ->
                                                                state["hidden"] = !selected || ((System.currentTimeMillis() - prevKeyTime) % 1000 > 500)
                                                            }
                                                        }
                                                }

                                            children += Container()
                                                .apply {
                                                    y = Side.NEGATIVE.toSide()
                                                    width = 0.8.toRelative()
                                                    height = 0.6.toAbsolute()

                                                    paddingLeft = 0.05.toRelative()
                                                    paddingRight = 0.05.toRelative()
                                                    paddingTop = 0.025.toRelative()
                                                    paddingBottom = 0.025.toRelative()

                                                    backgroundColor = { defaultTheme.borderColor.value }.toDependent()
                                                }
                                        }

                                        children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                            .apply {
                                                paddingLeft = 0.075.toRelative()
                                                paddingRight = 0.075.toRelative()
                                                paddingTop = 0.0375.toRelative()
                                                paddingBottom = 0.0375.toRelative()

                                                var cachedMembers = 0

                                                onUpdate += { state ->
                                                    if (SocialManager.currentGroup!!.members.size != cachedMembers) {
                                                        cachedMembers = SocialManager.currentGroup!!.members.size
                                                        state["hasInit"] = false
                                                    }
                                                }

                                                onInit += {
                                                    clear()

                                                    for (member in SocialManager.currentGroup!!.members) {
                                                        addChild(Container()
                                                            .apply {
                                                                height = 0.15.toCopy()

                                                                children += Container()
                                                                    .apply {
                                                                        x = Side.POSITIVE.toSide()

                                                                        backgroundCornerRadius = 0.035.toRelative()
                                                                        borderThickness = 0.0025.toRelative()

                                                                        backgroundColor = { defaultTheme.midgroundColor.value }.toDependent()

                                                                        borderColor = { defaultTheme.borderColor.value }.toDependent()

                                                                        Thread {
                                                                            AdapterManager.adapter.renderer.createTexture("$member-skin", MojangUtil.getSkin(member).openStream(), false)
                                                                        }.start()

                                                                        children += Container()
                                                                            .apply {
                                                                                x = Side.NEGATIVE.toSide()
                                                                                width = 1.0.toCopy()
                                                                                height = 0.5.toRelative()
                                                                                setPadding(Relative(0.25, true))

                                                                                children += Container()
                                                                                    .apply {
                                                                                        x = 0.0.toRelative()
                                                                                        y = 0.0.toRelative()
                                                                                        width = 1.0.toRelative()
                                                                                        height = 1.0.toRelative()

                                                                                        backgroundCornerRadius = 0.15.toRelative()
                                                                                        backgroundImage = "$member-skin".toAbsolute()
                                                                                        backgroundImageBounds = Rectangle(0.125, 0.125, 0.125, 0.125)
                                                                                    }

                                                                                children += Container()
                                                                                    .apply {
                                                                                        x = 0.0.toRelative()
                                                                                        y = 0.0.toRelative()
                                                                                        width = 1.0.toRelative()
                                                                                        height = 1.0.toRelative()

                                                                                        backgroundCornerRadius = 0.15.toRelative()
                                                                                        backgroundImage = "$member-skin".toAbsolute()
                                                                                        backgroundImageBounds = Rectangle(0.625, 0.125, 0.125, 0.125)
                                                                                    }
                                                                            }

                                                                        children += Text()
                                                                            .apply {
                                                                                x = Side.NEGATIVE.toSide()

                                                                                scale = 0.0075.toRelative()
                                                                                fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()

                                                                                var name = ""

                                                                                Thread {
                                                                                    name = MojangUtil.getUsername(member)
                                                                                }.start()

                                                                                text = { name }.toDependent()
                                                                                textColor = { defaultTheme.elementColor.value }.toDependent()
                                                                            }

                                                                        children += Container()
                                                                            .apply {
                                                                                x = Side.POSITIVE.toSide()
                                                                                width = 1.0.toCopy()
                                                                                height = 0.65.toRelative()

                                                                                setPadding(Relative(0.175, true))

                                                                                backgroundCornerRadius = 0.0175.toRelative()
                                                                                borderThickness = 0.0025.toRelative()

                                                                                backgroundColor = Dependent { state ->
                                                                                    if (state["clicked"] != null && state["clicked"] as Boolean) {
                                                                                        { defaultTheme.selectedColor.value }.toDependent()
                                                                                    } else {
                                                                                        { defaultTheme.midgroundColor.value }.toDependent()
                                                                                    }
                                                                                }
                                                                                borderColor = Dependent { state ->
                                                                                    if (((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean)) {
                                                                                        { defaultTheme.selectedBorderColor.value }.toDependent()
                                                                                    } else {
                                                                                        { defaultTheme.borderColor.value }.toDependent()
                                                                                    }
                                                                                }

                                                                                children += Container()
                                                                                    .apply {
                                                                                        width = 0.55.toRelative()
                                                                                        height = 1.0.toCopy()

                                                                                        backgroundColor = { defaultTheme.elementColor.value }.toDependent()
                                                                                        backgroundImage = "sorus/ui/friends/unfriend.png".toAbsolute()
                                                                                    }

                                                                                onClick = {
                                                                                    SocialManager.removeGroupMember(member)
                                                                                }
                                                                            }
                                                                    }
                                                            })

                                                        addChild(Container()
                                                            .apply {
                                                                height = 0.05.toCopy()
                                                            })
                                                    }
                                                }

                                                for (onInit in onInit) {
                                                    onInit(Pair(this, HashMap()))
                                                }

                                                onStateUpdate["hasInitProfiles"] = { state ->
                                                    if (state["hasInitProfiles"] == false) {
                                                        state["hasInitProfiles"] = true
                                                        state["hasInit"] = false
                                                    }
                                                }
                                            }
                                    }
                                })
                        }
                }

                for (onInit in onInit) {
                    onInit(Pair(this, HashMap()))
                }

                storedState += "hasInitGroups"
            }

        children += Container()
            .apply {
                x = Side.NEGATIVE.toSide()
                width = 0.4.toRelative()

                backgroundCornerRadius = 0.0155.toRelative()
                setPadding(0.0125.toRelative())

                backgroundColor = { defaultTheme.backgroundColor.value }.toDependent()
                borderColor = { defaultTheme.borderColor.value }.toDependent()
                borderThickness = 0.001.toRelative()

                children += Container()
                    .apply {
                        y = Side.NEGATIVE.toSide()
                        height = 0.02625.toRelative()
                        setPadding(0.05.toRelative())

                        children += Text()
                            .apply {
                                x = Side.NEGATIVE.toSide()

                                fontRenderer = "sorus/ui/font/Quicksand-Bold.ttf".toAbsolute()
                                scale = 0.005625.toRelative()
                                text = "Friends".toAbsolute()
                                textColor = { defaultTheme.elementColor.value }.toDependent()
                            }
                    }

                children += TabHolder()
                    .apply {
                        onUpdate += { state ->
                            if (!WebSocketManager.connected) {
                                state["groupsTab"] = "noSocket"
                            } else {
                                state["groupsTab"] = "friendsList"
                            }
                        }

                        storedState += "groupsTab"
                        stateId = "groupsTab"

                        addChild("noSocket", Container()
                            .apply {
                                children += Text()
                                    .apply {
                                        y = (-0.05).toRelative()

                                        scale = 0.006.toRelative()
                                        fontRenderer = "sorus/ui/font/Quicksand-Regular.ttf".toAbsolute()
                                        text = "Socket Not Connected".toAbsolute()
                                        textColor = { defaultTheme.elementColor.value }.toDependent()
                                    }
                            })

                        addChild("noGroup", Container()
                            .apply {
                                children += Container()
                                    .apply {
                                        y = (-0.05).toRelative()
                                        width = 0.4.toRelative()
                                        height = 0.3.toCopy()

                                        backgroundCornerRadius = 0.025.toRelative()
                                        backgroundColor = Dependent { state ->
                                            if (state["clicked"] != null && state["clicked"] as Boolean) {
                                                { defaultTheme.selectedColor.value }.toDependent()
                                            } else {
                                                { defaultTheme.midgroundColor.value }.toDependent()
                                            }
                                        }
                                        borderThickness = 0.0025.toRelative()
                                        borderColor = Dependent { state ->
                                            if ((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) {
                                                { defaultTheme.selectedBorderColor.value }.toDependent()
                                            } else {
                                                { defaultTheme.borderColor.value }.toDependent()
                                            }
                                        }

                                        children += Container()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = 1.0.toCopy()
                                                height = 0.5.toRelative()
                                                setPadding(Relative(0.2, true))

                                                backgroundImage = "sorus/ui/groups/invite.png".toAbsolute()
                                                backgroundColor = { defaultTheme.elementColor.value }.toDependent()
                                            }

                                        children += Text()
                                            .apply {
                                                setPadding(Relative(0.2, true))

                                                scale = 0.012.toRelative()
                                                fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                text = "Create".toAbsolute()
                                                textColor = { defaultTheme.elementColor.value }.toDependent()
                                            }

                                        onClick = { state ->
                                            SocialManager.createGroup()
                                            state["hasInitGroups"] = false
                                        }
                                    }
                            })

                        addChild("friendsList", Container()
                            .apply {
                                onInit += {
                                    clear()

                                    children += Container()
                                        .apply {
                                            y = Side.NEGATIVE.toSide()
                                            height = 0.08.toRelative()
                                            backgroundColor = { defaultTheme.midgroundColor.value }.toDependent()
                                            borderColor = { defaultTheme.borderColor.value }.toDependent()
                                            backgroundCornerRadius = 0.025.toRelative()
                                            borderThickness = 0.0025.toRelative()

                                            paddingLeft = 0.075.toRelative()
                                            paddingRight = 0.075.toRelative()
                                            paddingTop = 0.0375.toRelative()
                                            paddingBottom = 0.0375.toRelative()

                                            var message = ""
                                            var selected = false

                                            onStateUpdate["selected"] = { state ->
                                                selected = state["selected"] as Boolean
                                            }

                                            var prevKeyTime = System.currentTimeMillis()

                                            onChar = { state ->
                                                message += state.second.character
                                                prevKeyTime = System.currentTimeMillis()
                                            }

                                            onKey = { state ->
                                                if (state.second.isPressed) {
                                                    when (state.second.key) {
                                                        Key.BACKSPACE -> message = message.substring(0, message.length - 1)
                                                        Key.ENTER -> {
                                                            SocialManager.sendFriend(MojangUtil.getUUID(message))
                                                            message = ""
                                                        }
                                                    }
                                                    prevKeyTime = System.currentTimeMillis()
                                                }
                                            }

                                            children += Container()
                                                .apply {
                                                    x = Side.NEGATIVE.toSide()
                                                    width = 1.0.toCopy()
                                                    height = 0.5.toRelative()
                                                    setPadding(Relative(0.2, true))
                                                    paddingLeft = Relative(0.3, true)

                                                    backgroundImage = "sorus/ui/friends/friend.png".toAbsolute()
                                                    backgroundColor = { defaultTheme.elementColor.value }.toDependent()
                                                }

                                            children += Text().apply {
                                                x = Side.NEGATIVE.toSide()
                                                y = 0.0.toRelative()

                                                paddingLeft = 0.05.toRelative()

                                                text = {
                                                    if (message.isNotEmpty() || selected) { message } else { "Send Friend Request..." }
                                                }.toDependent()
                                                fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()

                                                scale = 0.005.toRelative()
                                            }

                                            children += Container()
                                                .apply {
                                                    x = Side.NEGATIVE.toSide()
                                                    width = 0.2.toAbsolute()
                                                    height = 0.6.toRelative()

                                                    backgroundColor = { defaultTheme.elementColor.value }.toDependent()

                                                    onUpdate += { state ->
                                                        state["hidden"] = !selected || ((System.currentTimeMillis() - prevKeyTime) % 1000 > 500)
                                                    }
                                                }
                                        }

                                    children += Container()
                                        .apply {
                                            y = Side.NEGATIVE.toSide()
                                            width = 0.8.toRelative()
                                            height = 0.6.toAbsolute()

                                            paddingLeft = 0.05.toRelative()
                                            paddingRight = 0.05.toRelative()
                                            paddingTop = 0.025.toRelative()
                                            paddingBottom = 0.025.toRelative()

                                            backgroundColor = { defaultTheme.borderColor.value }.toDependent()
                                        }

                                    children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                        .apply {
                                            paddingLeft = 0.075.toRelative()
                                            paddingRight = 0.075.toRelative()
                                            paddingTop = 0.0375.toRelative()
                                            paddingBottom = 0.0375.toRelative()

                                            var cachedSize = 0

                                            onUpdate += { state ->
                                                if (SocialManager.friends.size != cachedSize) {
                                                    cachedSize = SocialManager.friends.size
                                                    state["hasInit"] = false
                                                }
                                            }

                                            onInit += {
                                                clear()

                                                for (friend in SocialManager.friends) {
                                                    addChild(Container()
                                                        .apply {
                                                            height = { if (friend.second.second == "offline") { 0.12 } else { 0.20 }.toCopy() }.toDependent()

                                                            children += List(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                                                .apply {
                                                                    backgroundCornerRadius = 0.035.toRelative()
                                                                    borderThickness = 0.0025.toRelative()

                                                                    backgroundColor = { defaultTheme.midgroundColor.value }.toDependent()

                                                                    borderColor = { defaultTheme.borderColor.value }.toDependent()

                                                                    children += Container()
                                                                        .apply {
                                                                            height = { if (friend.second.second == "offline") { Flexible() } else { 0.6.toRelative() } }.toDependent()
                                                                            setPadding(0.001.toRelative())

                                                                            Thread {
                                                                                AdapterManager.adapter.renderer.createTexture("${friend.first}-skin", MojangUtil.getSkin(friend.first).openStream(), false)
                                                                            }.start()

                                                                            children += Container()
                                                                                .apply {
                                                                                    x = Side.NEGATIVE.toSide()
                                                                                    width = 1.0.toCopy()
                                                                                    height = 0.5.toRelative()
                                                                                    setPadding(Relative(0.25, true))

                                                                                    children += Container()
                                                                                        .apply {
                                                                                            x = 0.0.toRelative()
                                                                                            y = 0.0.toRelative()
                                                                                            width = 1.0.toRelative()
                                                                                            height = 1.0.toRelative()

                                                                                            backgroundCornerRadius = 0.15.toRelative()
                                                                                            backgroundImage = "${friend.first}-skin".toAbsolute()
                                                                                            backgroundImageBounds = Rectangle(0.125, 0.125, 0.125, 0.125)
                                                                                        }

                                                                                    children += Container()
                                                                                        .apply {
                                                                                            x = 0.0.toRelative()
                                                                                            y = 0.0.toRelative()
                                                                                            width = 1.0.toRelative()
                                                                                            height = 1.0.toRelative()

                                                                                            backgroundCornerRadius = 0.15.toRelative()
                                                                                            backgroundImage = "${friend.first}-skin".toAbsolute()
                                                                                            backgroundImageBounds = Rectangle(0.625, 0.125, 0.125, 0.125)

                                                                                            borderThickness = 0.055.toRelative()
                                                                                            borderColor = Dependent { if (friend.second.second == "offline") { Color.fromRGB(0, 0, 0, 0).toAbsolute() } else { Color.fromRGB(45, 175, 60, 255).toAbsolute() } }
                                                                                        }
                                                                                }

                                                                            children += Text()
                                                                                .apply {
                                                                                    x = Side.NEGATIVE.toSide()

                                                                                    scale = 0.006.toRelative()
                                                                                    fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()

                                                                                    var name = ""

                                                                                    Thread {
                                                                                        name = MojangUtil.getUsername(friend.first)
                                                                                    }.start()

                                                                                    text = { name }.toDependent()
                                                                                    textColor = { defaultTheme.elementColor.value }.toDependent()
                                                                                }

                                                                            children += Container()
                                                                                .apply {
                                                                                    x = Side.POSITIVE.toSide()
                                                                                    width = 1.0.toCopy()
                                                                                    height = 0.65.toRelative()

                                                                                    setPadding(Relative(0.175, true))

                                                                                    backgroundCornerRadius = 0.0175.toRelative()
                                                                                    borderThickness = 0.0025.toRelative()

                                                                                    backgroundColor = Dependent { state ->
                                                                                        if (state["clicked"] != null && state["clicked"] as Boolean && friend.second.second != "offline" && SocialManager.currentGroup != null) {
                                                                                            { defaultTheme.selectedColor.value }.toDependent()
                                                                                        } else {
                                                                                            { defaultTheme.midgroundColor.value }.toDependent()
                                                                                        }
                                                                                    }
                                                                                    borderColor = Dependent { state ->
                                                                                        if (((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) && friend.second.second != "offline" && SocialManager.currentGroup != null) {
                                                                                            { defaultTheme.selectedBorderColor.value }.toDependent()
                                                                                        } else {
                                                                                            { defaultTheme.borderColor.value }.toDependent()
                                                                                        }
                                                                                    }

                                                                                    children += Container()
                                                                                        .apply {
                                                                                            width = 0.55.toRelative()
                                                                                            height = 1.0.toCopy()

                                                                                            backgroundColor = { defaultTheme.elementColor.value }.toDependent()
                                                                                            backgroundImage = "sorus/ui/groups/invite.png".toAbsolute()
                                                                                        }

                                                                                    onClick = {
                                                                                        if (friend.second.second != "offline" && SocialManager.currentGroup != null) {
                                                                                            SocialManager.invite(friend.first)
                                                                                        }
                                                                                    }
                                                                                }

                                                                            children += Container()
                                                                                .apply {
                                                                                    x = Side.POSITIVE.toSide()
                                                                                    width = 1.0.toCopy()
                                                                                    height = 0.65.toRelative()

                                                                                    setPadding(Relative(0.175, true))

                                                                                    backgroundCornerRadius = 0.0175.toRelative()
                                                                                    borderThickness = 0.0025.toRelative()

                                                                                    backgroundColor = Dependent { state ->
                                                                                        if (state["clicked"] != null && state["clicked"] as Boolean) {
                                                                                            { defaultTheme.selectedColor.value }.toDependent()
                                                                                        } else {
                                                                                            { defaultTheme.midgroundColor.value }.toDependent()
                                                                                        }
                                                                                    }
                                                                                    borderColor = Dependent { state ->
                                                                                        if (((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean)) {
                                                                                            { defaultTheme.selectedBorderColor.value }.toDependent()
                                                                                        } else {
                                                                                            { defaultTheme.borderColor.value }.toDependent()
                                                                                        }
                                                                                    }

                                                                                    children += Container()
                                                                                        .apply {
                                                                                            width = 0.55.toRelative()
                                                                                            height = 1.0.toCopy()

                                                                                            backgroundColor = { defaultTheme.elementColor.value }.toDependent()
                                                                                            backgroundImage = "sorus/ui/friends/unfriend.png".toAbsolute()
                                                                                        }

                                                                                    onClick = {
                                                                                        SocialManager.unfriend(friend.first)
                                                                                    }
                                                                                }
                                                                        }

                                                                    children += Container()
                                                                        .apply {
                                                                            height = { if(friend.second.second == "offline") { 0.0.toAbsolute() } else { 0.4.toRelative() } }.toDependent()

                                                                            children += Text()
                                                                                .apply {
                                                                                    x = Side.NEGATIVE.toSide()

                                                                                    paddingLeft = 0.125.toRelative()
                                                                                    paddingBottom = Relative(0.4, true)

                                                                                    scale = 0.0055.toRelative()
                                                                                    fontRenderer = "sorus/ui/font/Quicksand-Regular.ttf".toAbsolute()
                                                                                    text = {
                                                                                        if (friend.second.second == "offline") {
                                                                                            "Offline"
                                                                                        } else if (friend.second.second.isNotEmpty()) {
                                                                                            "Playing ${friend.second.second} on ${friend.second.first}"
                                                                                        } else {
                                                                                            "Playing on ${friend.second.first}"
                                                                                        }
                                                                                    }.toDependent()
                                                                                    textColor = { defaultTheme.elementColor.value }.toDependent()
                                                                                }

                                                                            onUpdate += { state ->
                                                                                state["hidden"] = friend.second.second == "offline"
                                                                            }
                                                                        }
                                                                }
                                                        })

                                                    addChild(Container()
                                                        .apply {
                                                            height = 0.05.toCopy()
                                                        })
                                                }
                                            }

                                            for (onInit in onInit) {
                                                onInit(Pair(this, HashMap()))
                                            }

                                            onStateUpdate["hasInitProfiles"] = { state ->
                                                if (state["hasInitProfiles"] == false) {
                                                    state["hasInitProfiles"] = true
                                                    state["hasInit"] = false
                                                }
                                            }
                                        }
                                }
                            })
                    }

                storedState += "hasInitGroups"
            }
    }

}