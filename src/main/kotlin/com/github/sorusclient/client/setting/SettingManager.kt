package com.github.sorusclient.client.setting

import com.github.sorusclient.client.server.ServerIntegrationManager
import org.apache.commons.io.FileUtils
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object SettingManager {

    private val profileFile = File("sorus/profile")
    private val settingContainers: MutableMap<String?, SettingContainer> = HashMap()
    lateinit var mainProfile: Profile
    var currentProfile: Profile? = null
        private set
    private val allProfiles: MutableList<Profile> = ArrayList()

    val settingsCategory: MutableMap<String, Any> = HashMap()
    val mainUICategory: Category = Category("Main")

    init {
        profileFile.mkdirs()
    }

    fun initialize() {
        loadProfiles()
        load("/")
        ServerIntegrationManager.joinListeners["settings"] = this::handleJoinForced
        ServerIntegrationManager.leaveListeners.add(this::handleLeaveForced)
    }

    private fun handleJoinForced(json: Any) {
        handleJoinForced(settingsCategory, json)
    }

    private fun handleJoinForced(setting: Any, json: Any) {
        if (setting is Map<*, *>) {
            for (entry in setting) {
                (json as Map<*, *>)[entry.key]?.let { handleJoinForced(entry.value as Any, it) }
            }
        } else if (setting is Setting<*>) {
            val javaData = Util.toJava(setting.type, json)
            if (javaData is List<*>) {
                javaData.let { setting.setForcedValueRaw(it as List<Any>) }
            } else {
                javaData?.let { setting.setForcedValueRaw(ArrayList(Collections.singletonList(javaData))) }
            }
        }
    }

    private fun handleLeaveForced() {
        handleLeaveForced(settingsCategory)
    }

    private fun handleLeaveForced(setting: Any) {
        if (setting is Map<*, *>) {
            for (entry in setting) {
                handleLeaveForced(entry.value!!)
            }
        } else if (setting is Setting<*>) {
            setting.setForcedValueRaw(null)
        }
    }

    fun register(settingContainer: SettingContainer) {
        settingContainers[settingContainer.id] = settingContainer
    }

    fun loadProfiles() {
        val mainProfileFolder = File(profileFile, "main")
        if (!mainProfileFolder.exists()) {
            mainProfileFolder.mkdirs()
            FileUtils.write(File(mainProfileFolder, "settings.json"), "{}")
        }

        mainProfile = Profile.read(mainProfileFolder)
        addAllProfiles(mainProfile)
    }

    private fun addAllProfiles(profile: Profile) {
        allProfiles.add(profile)
        allProfiles.addAll(profile.children.values)
    }

    fun load(profileId: String) {
        val profile = mainProfile.getProfile(profileId)!!
        load(profile)
    }

    fun load(profile: Profile) {
        currentProfile?.let { save(it) }
        currentProfile = profile
        loadInternal(profile)
    }

    fun delete(profile: Profile) {
        if (profile === mainProfile) return
        this.load(profile.parent!!)
        allProfiles.remove(profile)
        profile.delete()

        for (entry in HashMap(profile.parent!!.children)) {
            if (entry.value == profile) {
                profile.parent!!.children.remove(entry.key)
            }
        }
    }

    private fun loadInternal(profile: Profile) {
        val profiles: MutableList<Profile> = ArrayList()
        var tempProfile: Profile? = profile
        while (tempProfile != null) {
            profiles.add(0, tempProfile)
            tempProfile = tempProfile.parent
        }

        for (profileParent in profiles) {
            load(JSONObject(profileParent.readSettings()).toMap(), profileParent == profile)
        }
    }

    private fun load(json: Any, isPrimary: Boolean) {
        load(settingsCategory, json, isPrimary)
    }

    private fun load(setting: Any, json: Any, isPrimary: Boolean) {
        if (setting is Map<*, *>) {
            for (entry in setting) {
                (json as Map<*, *>)[entry.key]?.let { load(entry.value as Any, it, isPrimary) }
            }
        } else if (setting is Setting<*>) {
            Util.toJava(setting.type, json)?.let { setting.setValueRaw(it, isPrimary) }
        }
    }

    fun getAllProfiles(): List<Profile> {
        return allProfiles
    }

    fun saveCurrent() {
        save(currentProfile!!)
    }

    private fun save(profile: Profile) {
        profile.writeSettings(JSONObject(save() as Map<*, *>).toString(2))
    }

    private fun save(): Any? {
        return handleSave(settingsCategory)
    }

    private fun handleSave(setting: Any): Any? {
        if (setting is Map<*, *>) {
            val map: HashMap<String, Any> = HashMap()
            for (entry in setting) {
                handleSave(entry.value as Any)?.let { map[entry.key as String] = it }
            }
            return map
        } else if (setting is Setting<*>) {
            return if (setting.overriden) {
                Util.toData(setting.realValue!!)
            } else {
                null
            }
        }
        return null
    }

    private fun updateProfile(profile: Profile?) {
        val settings = JSONObject(profile!!.readSettings())
        val newSettings: MutableMap<String?, Any?> = HashMap()
        for ((key, value) in settings.toMap()) {
            val settingContainer = settingContainers[key]
            if (profile === mainProfile || settingContainer == null || !settingContainer.shared) {
                newSettings[key] = value
            }
        }
        profile.writeSettings(JSONObject(newSettings).toString(2))
        for (profile1 in profile.children.values) {
            updateProfile(profile1)
        }
    }

    fun createNewProfile(profile: Profile) {
        val currentNames: MutableList<String> = ArrayList()
        for (profile in profile.children.values) {
            if (profile.id.length > 1) {
                currentNames.add(profile.id.substring(1, profile.id.length - 1))
            }
        }
        var newProfile: Profile? = null
        var i = 0
        while (newProfile == null) {
            val id = "Profile$i"
            if (!currentNames.contains(id)) {
                newProfile = profile.createProfile("/$id/")
            }
            i++
        }
        allProfiles.add(newProfile)
        this.load(newProfile)
    }

}