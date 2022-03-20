package com.github.sorusclient.client.ui

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.ScreenType
import com.github.sorusclient.client.adapter.event.InitializeEvent
import com.github.sorusclient.client.adapter.event.TickEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.*
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.Displayed
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting
import com.github.sorusclient.client.ui.framework.ContainerRenderer
import com.github.sorusclient.client.ui.theme.ThemeManager
import com.github.sorusclient.client.util.AssetUtil
import com.github.sorusclient.client.util.keybind.KeyBind
import com.github.sorusclient.client.util.keybind.KeyBindManager
import org.json.JSONObject
import java.net.URL
import kotlin.collections.set
import kotlin.math.pow
import kotlin.math.sqrt

object UserInterface {

    private lateinit var mainGuiKey: Setting<out MutableList<Key>>
    private lateinit var searchBarKey: Setting<out MutableList<Key>>
    lateinit var searchResults: List<SearchResult>

    fun initialize() {
        val adapter = AdapterManager.getAdapter()

        KeyBindManager.register(KeyBind({ mainGuiKey.value }, { pressed ->
            if (pressed && adapter.openScreen == ScreenType.IN_GAME) {
                adapter.openScreen(ScreenType.DUMMY)
                ThemeManager.currentTheme.onOpenMainGui()
            }
        }))

        KeyBindManager.register(KeyBind({ listOf(Key.ESCAPE) }, { pressed ->
            if (pressed && adapter.openScreen == ScreenType.DUMMY) {
                adapter.openScreen(ScreenType.IN_GAME)
                ThemeManager.currentTheme.onCloseGui()
            }
        }))

        KeyBindManager.register(KeyBind({ searchBarKey.value }, {
            if (it && adapter.openScreen == ScreenType.IN_GAME) {
                adapter.openScreen(ScreenType.DUMMY)
                ThemeManager.currentTheme.onOpenSearchBar()
            }
        }))

        SettingManager.settingsCategory
            .apply {
                data["searchBarKey"] = SettingData(Setting(arrayListOf(Key.ALT_LEFT)).also { searchBarKey = it })
                data["mainGuiKey"] = SettingData(Setting(arrayListOf(Key.SHIFT_RIGHT)).also { mainGuiKey = it })
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedSetting.KeyBind(searchBarKey, "Search Bar KeyBind"))
                add(DisplayedSetting.KeyBind(mainGuiKey, "Main GUI KeyBind"))
            }

        EventManager.register<InitializeEvent> {
            val searchResults: MutableList<SearchResult> = ArrayList()
            addSettingResults(searchResults, SettingManager.mainUICategory, "")

            searchResults.add(MenuSearchResult("home", "Home"))
            searchResults.add(MenuSearchResult("settings", "Settings"))
            searchResults.add(MenuSearchResult("plugins", "Plugins"))
            searchResults.add(MenuSearchResult("themes", "Themes"))

            for (serverJsonData in AssetUtil.getAllServerJson()) {
                val serverJson = JSONObject(serverJsonData.value)
                val logoAssetName = "${serverJson["name"]}-logo"
                AdapterManager.getAdapter().renderer.createTexture(logoAssetName, URL(AssetUtil.baseServersUrl + "/${serverJsonData.key}/${serverJson["logo"]}"))
                searchResults.add(ServerSearchResult(serverJson["name"] as String, serverJson["ip"] as String, logoAssetName))
            }

            val screens = listOf(Pair("Controls", ScreenType.CONTROLS), Pair("Settings", ScreenType.SETTINGS), Pair("Video Settings", ScreenType.VIDEO_SETTINGS))
            for (screen in screens) {
                searchResults.add(MinecraftMenuSearchResult(screen.first, screen.second))
            }

            this.searchResults = searchResults
        }

        EventManager.register<TickEvent> {
            if (AdapterManager.getAdapter().openScreen == ScreenType.IN_GAME && ContainerRenderer.containers.size > 0) {
                ThemeManager.currentTheme.onForceClose()
            }
        }
    }

    fun getProfiles(): List<Pair<Profile, Int>> {
        val profiles: MutableList<Pair<Profile, Int>> = ArrayList()
        addProfiles(profiles = profiles)
        return profiles
    }

    private fun addProfiles(profile: Profile = SettingManager.mainProfile, index: Int = 0, profiles: MutableList<Pair<Profile, Int>>) {
        profiles.add(Pair(profile, index))
        for (child in profile.children) {
            addProfiles(child.value, index + 1, profiles)
        }
    }

    fun search(searchTerm: String, results: List<SearchResult>, minimum: Double, maxResults: Int): List<SearchResult> {
        val scores: MutableList<Pair<SearchResult, Double>> = ArrayList()

        val lowerSearchTerm = searchTerm.lowercase()

        for (result in results) {
            var score = 0
            for (i in 1..lowerSearchTerm.length) {
                for (j in 0..lowerSearchTerm.length - i) {
                    if (result.searchString.lowercase().contains(lowerSearchTerm.substring(j, j + i))) {
                        score += i.toDouble().pow(2).toInt()
                    }
                }
            }

            scores.add(Pair(result, score.toDouble() / (sqrt(result.searchString.length.toDouble()) * lowerSearchTerm.length.toDouble())))
        }

        scores.retainAll {
            return@retainAll it.second > minimum
        }

        scores.sortBy {
            it.second
        }

        scores.reverse()

        var added = 0
        scores.retainAll {
            return@retainAll if (added < maxResults) {
                added++
                true
            } else {
                false
            }

        }

        val addedResults = ArrayList<String>()
        scores.retainAll {
            return@retainAll if (addedResults.contains(it.first.displayName)) {
                false
            } else {
                addedResults.add(it.first.displayName)
                true
            }
        }

        return scores.map {
            it.first
        }
    }

    private fun addSettingResults(list: MutableList<SearchResult>, category: DisplayedCategory, name: String) {
        list.add(SettingSearchResult(name, category))

        for (displayed in category.components) {
            val name = name + "/" + displayed.name
            if (displayed is DisplayedCategory) {
                addSettingResults(list, displayed, name)
            } else {
                list.add(SettingSearchResult(name, displayed))
            }
        }
    }

    abstract class SearchResult(val searchString: String, val displayName: String, val displayImage: String?) {
        abstract fun onSelect()
    }

    class SettingSearchResult(searchString: String, displayed: Displayed) : SearchResult(searchString, if (displayed is DisplayedCategory) { displayed.name } else { displayed.parent!!.name }, null) {

        private val linkedCategory: DisplayedCategory

        init {
            linkedCategory = if (displayed is DisplayedCategory) {
                displayed
            } else {
                displayed.parent!!
            }
        }

        override fun onSelect() {
            ThemeManager.currentTheme.openSettingsScreen(linkedCategory)
        }

    }

    class MenuSearchResult(private val menu: String, name: String) : SearchResult(name, name, "sorus/ui/sorus.png") {

        override fun onSelect() {
            ThemeManager.currentTheme.openUIScreen(menu)
        }

    }

    class MinecraftMenuSearchResult(val name: String, val type: ScreenType) : SearchResult(name, "Minecraft $name", "sorus/ui/grass_block.png") {

        override fun onSelect() {
            ThemeManager.currentTheme.closeGui()
            AdapterManager.getAdapter().renderer.unloadBlur()
            AdapterManager.getAdapter().openScreen(type)
        }

    }

    class ServerSearchResult(name: String, private val ip: String, image: String) : SearchResult(name, name, image) {

        override fun onSelect() {
            ThemeManager.currentTheme.closeGui()
            AdapterManager.getAdapter().openScreen(ScreenType.IN_GAME)
            AdapterManager.getAdapter().leaveWorld()
            AdapterManager.getAdapter().joinServer(ip)
        }

    }

}
